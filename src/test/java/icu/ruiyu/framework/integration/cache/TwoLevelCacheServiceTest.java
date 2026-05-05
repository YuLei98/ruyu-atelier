package icu.ruiyu.framework.integration.cache;

import icu.ruiyu.framework.common.config.CacheProperties;
import icu.ruiyu.framework.integration.cache.impl.TwoLevelCacheServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TwoLevelCacheServiceTest {

    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private CacheProperties cacheProperties;
    private TwoLevelCacheServiceImpl twoLevelCacheService;

    @BeforeEach
    void setUp() {
        cacheProperties = new CacheProperties();
        cacheProperties.getCaffeine().setMaxSize(100);
        cacheProperties.getCaffeine().setExpireAfterWriteMinutes(10);
        cacheProperties.getRedis().setDefaultExpireMinutes(60);

        twoLevelCacheService = new TwoLevelCacheServiceImpl();
        twoLevelCacheService.setStringRedisTemplate(stringRedisTemplate);
        twoLevelCacheService.setCacheProperties(cacheProperties);
        twoLevelCacheService.init();
    }

    @Test
    void testBuildKey() {
        String key = twoLevelCacheService.buildKey("user", "123");
        assertEquals("twolevel:user:123", key);
    }

    @Test
    void testGet_L1Hit() {
        // L1 命中测试
        twoLevelCacheService.put("user", "1", "John", 60);
        String result = twoLevelCacheService.get("user", "1");
        assertEquals("John", result);
    }

    @Test
    void testGet_L2Hit_BackfillL1() {
        // L1 未命中，L2 命中，回填 L1
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("twolevel:user:2")).thenReturn("Jane");

        String result = twoLevelCacheService.get("user", "2");
        assertEquals("Jane", result);
    }

    @Test
    void testGet_CacheMiss() {
        // 缓存未命中
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(anyString())).thenReturn(null);

        String result = twoLevelCacheService.get("user", "999");
        assertNull(result);
    }

    @Test
    void testEvict() {
        // 淘汰缓存
        twoLevelCacheService.put("user", "1", "John", 60);
        assertEquals("John", twoLevelCacheService.get("user", "1"));

        twoLevelCacheService.evict("user", "1");
        assertNull(twoLevelCacheService.get("user", "1"));
    }
}