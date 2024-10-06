package icu.ruiyu.framework.integration.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

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
}
