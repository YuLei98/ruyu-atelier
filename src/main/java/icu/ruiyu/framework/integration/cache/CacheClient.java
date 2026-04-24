package icu.ruiyu.framework.integration.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Component
public class CacheClient {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public void set(String key, String value, ExpireEnum expire) {
        stringRedisTemplate.opsForValue().set(key, value, expire.getExpire());
    }

    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * set key if not exists (SETNX with expiration)
     * @return true if set successfully, false if key already exists
     */
    public Boolean setIfAbsent(String key, String value, ExpireEnum expire) {
        return stringRedisTemplate.opsForValue().setIfAbsent(key, value, expire.getExpire());
    }

    /**
     * set key if not exists with custom expiration duration
     * @return true if set successfully, false if key already exists
     */
    public Boolean setIfAbsent(String key, String value, Duration duration) {
        return stringRedisTemplate.opsForValue().setIfAbsent(key, value, duration);
    }
}
