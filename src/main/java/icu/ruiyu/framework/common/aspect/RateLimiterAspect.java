package icu.ruiyu.framework.common.aspect;

import icu.ruiyu.framework.common.annotation.RateLimiter;
import icu.ruiyu.framework.exception.RateLimitException;
import icu.ruiyu.framework.integration.ratelimit.RateLimiterService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;

/**
 * 限流 AOP 切面
 * 基于注解实现方法级别的限流控制
 */
@Slf4j
@Aspect
@Component
public class RateLimiterAspect {

    private static final String RATE_LIMIT_PREFIX = "ratelimit:annotation:";

    @Resource
    private RateLimiterService rateLimiterService;

    @Around("@annotation(icu.ruiyu.framework.common.annotation.RateLimiter)")
    public Object rateLimitCheck(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RateLimiter rateLimiter = method.getAnnotation(RateLimiter.class);

        String key = generateKey(joinPoint, rateLimiter);

        boolean allowed = rateLimiterService.isAllowed(key, rateLimiter);
        if (!allowed) {
            log.warn("Rate limit exceeded for method: {}, key: {}",
                    method.getName(), key);
            throw new RateLimitException(rateLimiter.message());
        }

        return joinPoint.proceed();
    }

    /**
     * 生成限流 key
     * 格式: ratelimit:annotation:{className}:{methodName}:{keyPrefix}
     */
    private String generateKey(ProceedingJoinPoint joinPoint, RateLimiter rateLimiter) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getMethod().getName();
        String className = signature.getDeclaringType().getSimpleName();

        String prefix = rateLimiter.keyPrefix();
        if (prefix == null || prefix.isBlank()) {
            prefix = methodName;
        }

        // 尝试获取用户 ID 用于更精细的限流
        String userId = getCurrentUserId();
        if (userId != null) {
            return RATE_LIMIT_PREFIX + className + ":" + prefix + ":user:" + userId;
        }

        // 降级为基于 IP
        String ip = getClientIp();
        return RATE_LIMIT_PREFIX + className + ":" + prefix + ":ip:" + ip;
    }

    private String getCurrentUserId() {
        try {
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                // 从 Security Context 获取认证信息
                if (request.getUserPrincipal() != null) {
                    return request.getUserPrincipal().getName();
                }
            }
        } catch (Exception e) {
            log.debug("Failed to get current user ID", e);
        }
        return null;
    }

    private String getClientIp() {
        try {
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String ip = request.getHeader("X-Forwarded-For");
                if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getHeader("X-Real-IP");
                }
                if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getRemoteAddr();
                }
                // 多级代理时取第一个 IP
                if (ip != null && ip.contains(",")) {
                    ip = ip.split(",")[0].trim();
                }
                return ip;
            }
        } catch (Exception e) {
            log.debug("Failed to get client IP", e);
        }
        return "unknown";
    }
}
