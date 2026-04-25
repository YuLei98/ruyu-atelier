package icu.ruiyu.framework.integration.ratelimit;

import icu.ruiyu.framework.common.annotation.RateLimiter;
import icu.ruiyu.framework.common.config.RateLimiterProperties;
import icu.ruiyu.framework.integration.ratelimit.handler.RateLimitResponseHandler;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 全局限流过滤器
 * 基于 IP 或用户 ID 进行全局限流控制
 */
@Slf4j
public class RateLimiterFilter extends OncePerRequestFilter {

    private final RateLimiterProperties properties;
    private final RateLimiterService rateLimiterService;
    private final RateLimitResponseHandler responseHandler;

    public RateLimiterFilter(RateLimiterService rateLimiterService,
                            RateLimitResponseHandler responseHandler,
                            RateLimiterProperties properties) {
        this.properties = properties;
        this.rateLimiterService = rateLimiterService;
        this.responseHandler = responseHandler;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 检查是否启用全局限流
        if (!properties.getGlobal().isEnabled()) {
            filterChain.doFilter(request, response);
            return;
        }

        // 检查是否在排除路径中
        String path = request.getRequestURI();
        if (isExcluded(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 生成限流 key
        String key = generateKey(request);

        // 执行限流检查
        boolean allowed;
        if ("TOKEN_BUCKET".equalsIgnoreCase(properties.getGlobal().getAlgorithm())) {
            allowed = rateLimiterService.isAllowed(key,
                    properties.getTokenBucket().getCapacity(),
                    properties.getTokenBucket().getRefillRate(), 1);
        } else {
            allowed = rateLimiterService.isAllowed(key,
                    properties.getGlobal().getWindowSeconds(),
                    properties.getGlobal().getMaxRequests());
        }

        if (allowed) {
            filterChain.doFilter(request, response);
        } else {
            log.warn("Global rate limit exceeded for key: {}", key);
            responseHandler.handle(request, response, properties.getGlobal());
        }
    }

    private boolean isExcluded(String path) {
        return properties.getExcludePaths() != null
                && properties.getExcludePaths().contains(path);
    }

    private String generateKey(HttpServletRequest request) {
        String keyType = properties.getGlobal().getKeyType();
        String prefix = "ratelimit:global:";

        if ("USER_ID".equalsIgnoreCase(keyType)) {
            // 从 Security Context 获取用户 ID
            if (request.getUserPrincipal() != null) {
                return prefix + "user:" + request.getUserPrincipal().getName();
            }
            // 未登录用户降级为 IP
        }

        // 默认基于 IP
        String ip = getClientIp(request);
        return prefix + "ip:" + ip;
    }

    private String getClientIp(HttpServletRequest request) {
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
}
