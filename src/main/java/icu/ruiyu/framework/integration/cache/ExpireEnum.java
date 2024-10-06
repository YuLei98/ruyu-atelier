package icu.ruiyu.framework.integration.cache;

import lombok.Getter;

import java.time.Duration;

@Getter
public enum ExpireEnum {
    ONE_DAY(Duration.ofDays(1)),
    ONE_HOUR(Duration.ofHours(1)),
    ONE_MINUTE(Duration.ofMinutes(1)),
    ONE_SECOND(Duration.ofSeconds(1)),
    HALF_AN_HOUR(Duration.ofMinutes(30)),
    THIRTY_SECONDS(Duration.ofSeconds(30));

    final Duration expire;

    ExpireEnum(Duration expire) {
        this.expire = expire;
    }
}
