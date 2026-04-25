package icu.ruiyu.framework.integration.ratelimit;

import icu.ruiyu.framework.common.annotation.RateLimiter;

/**
 * 限流服务接口
 */
public interface RateLimiterService {

    /**
     * 滑动窗口限流检查
     *
     * @param key          限流 key
     * @param windowSeconds 时间窗口（秒）
     * @param maxRequests  窗口内最大请求数
     * @return true 表示允许通过，false 表示被限流
     */
    boolean isAllowed(String key, int windowSeconds, int maxRequests);

    /**
     * 令牌桶限流检查
     *
     * @param key         限流 key
     * @param capacity    桶容量
     * @param refillRate  令牌补充速率（个/秒）
     * @param requested   本次请求的令牌数
     * @return true 表示允许通过，false 表示被限流
     */
    boolean isAllowed(String key, int capacity, double refillRate, int requested);

    /**
     * 基于注解的限流检查
     *
     * @param key         限流 key
     * @param rateLimiter 注解配置
     * @return true 表示允许通过，false 表示被限流
     */
    boolean isAllowed(String key, RateLimiter rateLimiter);
}
