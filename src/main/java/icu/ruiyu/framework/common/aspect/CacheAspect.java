package icu.ruiyu.framework.common.aspect;

import icu.ruiyu.framework.common.annotation.CacheEvict;
import icu.ruiyu.framework.common.annotation.CachePut;
import icu.ruiyu.framework.common.annotation.Cacheable;
import icu.ruiyu.framework.integration.cache.TwoLevelCacheService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 缓存 AOP 切面
 * 拦截 @Cacheable @CacheEvict @CachePut 注解
 */
@Slf4j
@Aspect
@Component
public class CacheAspect {

    private final SpelExpressionParser parser = new SpelExpressionParser();

    private final Map<String, Expression> expressionCache = new HashMap<>();

    @Resource
    private TwoLevelCacheService twoLevelCacheService;

    /**
     * 拦截 @Cacheable 注解
     * 实现 read-through 模式
     */
    @Around("@annotation(icu.ruiyu.framework.common.annotation.Cacheable)")
    public Object aroundCacheable(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Cacheable cacheable = method.getAnnotation(Cacheable.class);

        String key = resolveKey(cacheable.key(), joinPoint);
        String cacheName = cacheable.value();

        // 1. 尝试从缓存获取
        String cachedValue = twoLevelCacheService.get(cacheName, key);
        if (cachedValue != null) {
            log.debug("@Cacheable hit: cacheName={}, key={}", cacheName, key);
            // 注意：这里返回的是字符串，实际使用需要反序列化
            // 简化处理：返回字符串，由调用方自行处理
            return cachedValue;
        }

        // 2. 执行目标方法
        Object result = joinPoint.proceed();

        // 3. 结果写入缓存
        if (result != null) {
            String valueToCache = result.toString();
            twoLevelCacheService.put(cacheName, key, valueToCache,
                    cacheable.expireAfterWriteMinutes());
            log.debug("@Cacheable put: cacheName={}, key={}", cacheName, key);
        }

        return result;
    }

    /**
     * 拦截 @CacheEvict 注解
     * 实现缓存淘汰
     */
    @Around("@annotation(icu.ruiyu.framework.common.annotation.CacheEvict)")
    public Object aroundCacheEvict(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        CacheEvict cacheEvict = method.getAnnotation(CacheEvict.class);

        String key = resolveKey(cacheEvict.key(), joinPoint);
        String cacheName = cacheEvict.value();

        // 执行目标方法
        Object result = joinPoint.proceed();

        // 淘汰缓存
        if (cacheEvict.allEntries()) {
            twoLevelCacheService.clear(cacheName);
            log.debug("@CacheEvict clear: cacheName={}", cacheName);
        } else {
            twoLevelCacheService.evict(cacheName, key);
            log.debug("@CacheEvict evict: cacheName={}, key={}", cacheName, key);
        }

        return result;
    }

    /**
     * 拦截 @CachePut 注解
     * 实现 write-through 模式
     */
    @Around("@annotation(icu.ruiyu.framework.common.annotation.CachePut)")
    public Object aroundCachePut(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        CachePut cachePut = method.getAnnotation(CachePut.class);

        String key = resolveKey(cachePut.key(), joinPoint);
        String cacheName = cachePut.value();

        // 1. 执行目标方法
        Object result = joinPoint.proceed();

        // 2. 更新缓存
        if (result != null) {
            String valueToCache = result.toString();
            // L2 过期时间默认 10 分钟
            twoLevelCacheService.put(cacheName, key, valueToCache, 10);
            log.debug("@CachePut put: cacheName={}, key={}", cacheName, key);
        }

        return result;
    }

    /**
     * 解析 SpEL 表达式
     */
    private String resolveKey(String keyExpression, ProceedingJoinPoint joinPoint) {
        if (keyExpression == null || keyExpression.isBlank()) {
            // 默认使用参数值
            Object[] args = joinPoint.getArgs();
            if (args.length == 1) {
                return args[0].toString();
            }
            return joinPoint.getSignature().getName();
        }

        // 使用 SpEL 表达式解析
        Expression expression = expressionCache.computeIfAbsent(keyExpression,
                k -> parser.parseExpression(keyExpression));

        EvaluationContext context = new StandardEvaluationContext();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        if (parameterNames != null) {
            for (int i = 0; i < parameterNames.length; i++) {
                ((StandardEvaluationContext) context).setVariable(parameterNames[i], args[i]);
            }
        }

        return expression.getValue(context).toString();
    }
}
