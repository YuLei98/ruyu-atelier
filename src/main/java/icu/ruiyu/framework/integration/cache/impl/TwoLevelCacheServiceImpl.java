package icu.ruiyu.framework.integration.cache.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import icu.ruiyu.framework.common.config.CacheProperties;
import icu.ruiyu.framework.integration.cache.TwoLevelCacheService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 二级缓存服务实现
 * L1: Caffeine (本地) -> L2: Redis (分布式)
 */
@Slf4j
@Service
public class TwoLevelCacheServiceImpl implements TwoLevelCacheService {

    private static final String KEY_PREFIX = "twolevel:";

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private CacheProperties cacheProperties;

    /**
     * L1 缓存集合（按分区隔离）
     */
    private final ConcurrentHashMap<String, Cache<Object, Object>> caffeineCaches = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        log.info("TwoLevelCacheService initialized with L1 maxSize={}, expireAfterWriteMinutes={}",
                cacheProperties.getCaffeine().getMaxSize(),
                cacheProperties.getCaffeine().getExpireAfterWriteMinutes());
    }

    @Override
    public String get(String cacheName, String key) {
        String fullKey = buildKey(cacheName, key);

        // 1. 查 L1 (Caffeine)
        Cache<Object, Object> l1Cache = caffeineCaches.computeIfAbsent(cacheName, this::createL1Cache);
        Object l1Value = l1Cache.getIfPresent(key);
        if (l1Value != null) {
            log.debug("L1 cache hit: {}", fullKey);
            return (String) l1Value;
        }

        // 2. L1 未命中，查 L2 (Redis)
        String l2Value = stringRedisTemplate.opsForValue().get(fullKey);
        if (l2Value != null) {
            log.debug("L2 cache hit, backfill L1: {}", fullKey);
            // 回填 L1
            l1Cache.put(key, l2Value);
            return l2Value;
        }

        log.debug("Cache miss: {}", fullKey);
        return null;
    }

    @Override
    public void put(String cacheName, String key, String value, int l1ExpireMinutes, int l2ExpireMinutes) {
        String fullKey = buildKey(cacheName, key);

        // 1. 写入 L1
        Cache<Object, Object> l1Cache = caffeineCaches.computeIfAbsent(cacheName, this::createL1Cache);
        l1Cache.put(key, value);

        // 2. 写入 L2
        Duration l2Expire = Duration.ofMinutes(l2ExpireMinutes > 0 ? l2ExpireMinutes : cacheProperties.getRedis().getDefaultExpireMinutes());
        stringRedisTemplate.opsForValue().set(fullKey, value, l2Expire);

        log.debug("Cache put: {}", fullKey);
    }

    @Override
    public void evict(String cacheName, String key) {
        String fullKey = buildKey(cacheName, key);

        // 1. 删除 L1
        Cache<Object, Object> l1Cache = caffeineCaches.get(cacheName);
        if (l1Cache != null) {
            l1Cache.invalidate(key);
        }

        // 2. 删除 L2
        stringRedisTemplate.delete(fullKey);

        log.debug("Cache evict: {}", fullKey);
    }

    @Override
    public void clear(String cacheName) {
        // 1. 清空 L1
        Cache<Object, Object> l1Cache = caffeineCaches.remove(cacheName);
        if (l1Cache != null) {
            l1Cache.invalidateAll();
        }

        // 2. 清空 L2（需要遍历，简化处理：使用 Redis SCAN）
        // 注意：这里简化实现，实际生产环境可使用 Redis SCAN 模式匹配删除
        log.debug("Cache clear: {}", cacheName);
    }

    @Override
    public String buildKey(String cacheName, String key) {
        return KEY_PREFIX + cacheName + ":" + key;
    }

    /**
     * 创建 L1 缓存实例
     */
    private Cache<Object, Object> createL1Cache(String cacheName) {
        return Caffeine.newBuilder()
                .maximumSize(cacheProperties.getCaffeine().getMaxSize())
                .expireAfterWrite(cacheProperties.getCaffeine().getExpireAfterWriteMinutes(), TimeUnit.MINUTES)
                .build();
    }
}