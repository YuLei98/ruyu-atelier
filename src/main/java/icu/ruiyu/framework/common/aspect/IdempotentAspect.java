package icu.ruiyu.framework.common.aspect;

import com.alibaba.fastjson.JSON;
import icu.ruiyu.framework.common.annotation.Idempotent;
import icu.ruiyu.framework.common.CommonResult;
import icu.ruiyu.framework.integration.cache.CacheService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 幂等性 AOP 切面
 * 防止客户端重复提交
 */
@Slf4j
@Aspect
@Component
public class IdempotentAspect {

    private static final String IDEMPOTENT_PREFIX = "idempotent:";

    @Resource
    private CacheService cacheService;

    @Around("@annotation(icu.ruiyu.framework.common.annotation.Idempotent)")
    public Object idempotentCheck(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Idempotent idempotent = method.getAnnotation(Idempotent.class);

        String key = generateKey(joinPoint, idempotent);
        String value = System.currentTimeMillis() + ":" + method.getName();

        int expireSeconds = idempotent.expireSeconds();

        Boolean success = cacheService.setIfAbsent(key, value, expireSeconds);
        if (success == null || !success) {
            log.warn("Duplicate request detected, key: {}", key);
            return CommonResult.error(409, idempotent.message());
        }

        return joinPoint.proceed();
    }

    /**
     * 生成幂等 key
     * 格式: idempotent:{methodName}:{argsHash}
     */
    private String generateKey(ProceedingJoinPoint joinPoint, Idempotent idempotent) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getMethod().getName();
        String className = signature.getDeclaringType().getSimpleName();

        String prefix = idempotent.keyPrefix();
        if (prefix == null || prefix.isBlank()) {
            prefix = methodName;
        }

        Object[] args = joinPoint.getArgs();
        String argsHash = hashArgs(args);

        return IDEMPOTENT_PREFIX + className + ":" + prefix + ":" + argsHash;
    }

    /**
     * 对参数进行哈希
     */
    private String hashArgs(Object[] args) {
        if (args == null || args.length == 0) {
            return "empty";
        }
        try {
            String argsStr = JSON.toJSONString(args);
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(argsStr.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            return String.valueOf(args.hashCode());
        }
    }
}
