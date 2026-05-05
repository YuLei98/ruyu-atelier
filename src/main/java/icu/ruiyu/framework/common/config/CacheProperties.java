package icu.ruiyu.framework.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Caffeine 二级缓存配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "cache")
public class CacheProperties {

    private Caffeine caffeine = new Caffeine();
    private Redis redis = new Redis();

    @Data
    public static class Caffeine {
        /**
         * L1 本地缓存最大容量
         */
        private int maxSize = 1000;

        /**
         * L1 写后过期时间（分钟）
         */
        private int expireAfterWriteMinutes = 10;
    }

    @Data
    public static class Redis {
        /**
         * L2 Redis 默认过期时间（分钟）
         */
        private int defaultExpireMinutes = 60;
    }
}