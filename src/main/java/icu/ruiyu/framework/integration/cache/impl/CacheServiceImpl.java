package icu.ruiyu.framework.integration.cache.impl;

import icu.ruiyu.framework.integration.cache.CacheService;
import icu.ruiyu.framework.integration.cache.ExpireEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Redis 缓存服务实现
 */
@Slf4j
@Service
public class CacheServiceImpl implements CacheService {
    private static final String LOCK_PREFIX = "lock:";

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void set(String key, String value, ExpireEnum expire) {
        stringRedisTemplate.opsForValue().set(key, value, expire.getExpire());
    }

    @Override
    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    @Override
    public boolean delete(String key) {
        return Boolean.TRUE.equals(stringRedisTemplate.delete(key));
    }

    @Override
    public boolean exists(String key) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(key));
    }

    @Override
    public boolean setIfAbsent(String key, String value, ExpireEnum expire) {
        return Boolean.TRUE.equals(stringRedisTemplate.opsForValue().setIfAbsent(key, value, expire.getExpire()));
    }

    @Override
    public boolean expire(String key, ExpireEnum expire) {
        return Boolean.TRUE.equals(stringRedisTemplate.expire(key, expire.getExpire()));
    }

    @Override
    public long getExpire(String key) {
        Long result = stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
        return result != null ? result : -2;
    }

    @Override
    public boolean lock(String key, ExpireEnum expire) {
        String lockKey = LOCK_PREFIX + key;
        String lockValue = UUID.randomUUID().toString();
        while (true) {
            if (setIfAbsent(lockKey, lockValue, expire)) {
                log.debug("Lock acquired: {}", lockKey);
                return true;
            }
            try {
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
    }

    @Override
    public boolean unlock(String key) {
        String lockKey = LOCK_PREFIX + key;
        String lockValue = stringRedisTemplate.opsForValue().get(lockKey);
        if (lockValue == null) {
            return false;
        }
        if (delete(lockKey)) {
            log.debug("Lock released: {}", lockKey);
            return true;
        }
        return false;
    }

    @Override
    public boolean tryLock(String key, ExpireEnum expire, long timeoutSeconds) {
        String lockKey = LOCK_PREFIX + key;
        String lockValue = UUID.randomUUID().toString();
        long deadline = System.currentTimeMillis() + timeoutSeconds * 1000;
        while (System.currentTimeMillis() < deadline) {
            if (Boolean.TRUE.equals(setIfAbsent(lockKey, lockValue, expire))) {
                log.debug("TryLock acquired: {}", lockKey);
                return true;
            }
            try {
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        log.debug("TryLock timeout: {}", lockKey);
        return false;
    }
}