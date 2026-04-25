package icu.ruiyu.framework.common.annotation;

import java.lang.annotation.*;

/**
 * API 限流注解
 * 标注在 Controller 方法上，实现接口级别的限流控制
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimiter {

    /**
     * 限流 key 前缀，默认使用方法名
     */
    String keyPrefix() default "";

    /**
     * 时间窗口大小（秒），默认 60 秒
     */
    int windowSeconds() default 60;

    /**
     * 时间窗口内最大请求数，默认 100
     */
    int maxRequests() default 100;

    /**
     * 限流触发时的提示信息
     */
    String message() default "请求过于频繁，请稍后再试";

    /**
     * 限流算法
     */
    Algorithm algorithm() default Algorithm.SLIDING_WINDOW;

    /**
     * 令牌桶算法专用：令牌桶容量
     */
    int bucketCapacity() default 100;

    /**
     * 令牌桶算法专用：令牌补充速率（个/秒）
     */
    double refillRate() default 10.0;

    enum Algorithm {
        /**
         * 滑动窗口算法，基于 Redis ZSET 实现
         */
        SLIDING_WINDOW,
        /**
         * 令牌桶算法，基于 Redis Hash 实现，支持突发流量
         */
        TOKEN_BUCKET
    }
}
