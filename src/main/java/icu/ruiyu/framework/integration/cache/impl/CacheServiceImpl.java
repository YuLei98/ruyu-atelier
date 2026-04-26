package icu.ruiyu.framework.integration.cache.impl;

import icu.ruiyu.framework.integration.cache.CacheService;
import icu.ruiyu.framework.integration.cache.ExpireEnum;
import lombok.extern.slf4j.Slf4j;
import jakarta.annotation.Resource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Redis 缓存服务实现
 */
@Slf4j
@Service
public class CacheServiceImpl implements CacheService {
    private static final String LOCK_PREFIX = "lock:";
    private static final long DEFAULT_LOCK_TIMEOUT_SECONDS = 30;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private DefaultRedisScript<Long> unlockScript;

    // ThreadLocal 存储当前线程持有的锁 UUID
    private final ConcurrentHashMap<String, String> lockValueMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        unlockScript = new DefaultRedisScript<>();
        unlockScript.setScriptSource(new ResourceScriptSource(
                new ClassPathResource("scripts/unlock.lua")));
        unlockScript.setResultType(Long.class);
    }

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
        return tryLock(key, expire, DEFAULT_LOCK_TIMEOUT_SECONDS);
    }

    @Override
    public boolean unlock(String key) {
        String lockKey = LOCK_PREFIX + key;
        String lockValue = lockValueMap.remove(lockKey);
        if (lockValue == null) {
            log.warn("No lock value found for key: {}, cannot unlock", lockKey);
            return false;
        }

        Long result = stringRedisTemplate.execute(
                unlockScript,
                Collections.singletonList(lockKey),
                lockValue
        );

        boolean success = result != null && result == 1L;
        if (success) {
            log.debug("Lock released: {}", lockKey);
        } else {
            log.warn("Failed to release lock: {}, lock may have expired or been released by owner", lockKey);
        }
        return success;
    }

    @Override
    public boolean tryLock(String key, ExpireEnum expire, long timeoutSeconds) {
        String lockKey = LOCK_PREFIX + key;
        String lockValue = UUID.randomUUID().toString();
        long deadline = System.currentTimeMillis() + timeoutSeconds * 1000;

        while (System.currentTimeMillis() < deadline) {
            if (Boolean.TRUE.equals(setIfAbsent(lockKey, lockValue, expire))) {
                lockValueMap.put(lockKey, lockValue);
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