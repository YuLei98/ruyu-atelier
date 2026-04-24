package icu.ruiyu.framework.integration.cache;

import lombok.Getter;

import java.time.Duration;

/**
 * Redis 缓存过期时间枚举
 */
@Getter
public enum ExpireEnum {
    ONE_WEEK(Duration.ofDays(7)),
    ONE_DAY(Duration.ofDays(1)),
    THIRTY_MINUTES(Duration.ofMinutes(30)),
    TEN_MINUTES(Duration.ofMinutes(10)),
    FIVE_MINUTES(Duration.ofMinutes(5)),
    ONE_HOUR(Duration.ofHours(1)),
    HALF_AN_HOUR(Duration.ofMinutes(30)),
    THIRTY_SECONDS(Duration.ofSeconds(30)),
    ONE_MINUTE(Duration.ofMinutes(1)),
    ONE_SECOND(Duration.ofSeconds(1));

    final Duration expire;

    ExpireEnum(Duration expire) {
        this.expire = expire;
    }
}