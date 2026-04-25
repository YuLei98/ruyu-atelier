package icu.ruiyu.framework.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 限流配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "ratelimit")
public class RateLimiterProperties {

    private Global global = new Global();
    private TokenBucket tokenBucket = new TokenBucket();
    private List<String> excludePaths;

    @Data
    public static class Global {
        /**
         * 是否启用全局限流
         */
        private boolean enabled = true;

        /**
         * 限流算法：SLIDING_WINDOW 或 TOKEN_BUCKET
         */
        private String algorithm = "SLIDING_WINDOW";

        /**
         * 时间窗口大小（秒）
         */
        private int windowSeconds = 60;

        /**
         * 时间窗口内最大请求数
         */
        private int maxRequests = 100;

        /**
         * 限流维度：IP 或 USER_ID
         */
        private String keyType = "IP";

        /**
         * 限流响应状态码
         */
        private int responseStatus = 429;

        /**
         * 限流响应消息
         */
        private String responseMessage = "请求过于频繁，请稍后再试";
    }

    @Data
    public static class TokenBucket {
        /**
         * 令牌补充速率（个/秒）
         */
        private double refillRate = 10.0;

        /**
         * 令牌桶容量
         */
        private int capacity = 100;
    }
}
