package icu.ruiyu.framework.integration.cache;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = com.ruiyu.framework.FrameworkApplication.class)
class CacheServiceTest {

    @Autowired
    private CacheService cacheService;

    @MockBean
    private StringRedisTemplate stringRedisTemplate;

    private ValueOperations<String, String> mockValueOps;

    @BeforeEach
    void setUp() {
        mockValueOps = mock(ValueOperations.class);
        when(stringRedisTemplate.opsForValue()).thenReturn(mockValueOps);
    }

    @Test
    void testSetAndGet() {
        setUp();
        cacheService.set("key1", "value1", ExpireEnum.ONE_HOUR);
        when(mockValueOps.get("key1")).thenReturn("value1");
        assertEquals("value1", cacheService.get("key1"));
    }

    @Test
    void testGetNonExistent() {
        setUp();
        when(mockValueOps.get("nonexistent")).thenReturn(null);
        assertNull(cacheService.get("nonexistent"));
    }

    @Test
    void testDelete() {
        setUp();
        when(stringRedisTemplate.delete("key1")).thenReturn(true);
        assertTrue(cacheService.delete("key1"));
    }

    @Test
    void testDeleteNonExistent() {
        setUp();
        when(stringRedisTemplate.delete("nonexistent")).thenReturn(false);
        assertFalse(cacheService.delete("nonexistent"));
    }

    @Test
    void testExists() {
        setUp();
        when(stringRedisTemplate.hasKey("key1")).thenReturn(true);
        assertTrue(cacheService.exists("key1"));
    }

    @Test
    void testExistsFalse() {
        setUp();
        when(stringRedisTemplate.hasKey("nonexistent")).thenReturn(false);
        assertFalse(cacheService.exists("nonexistent"));
    }

    @Test
    void testSetIfAbsent() {
        setUp();
        when(mockValueOps.setIfAbsent(eq("newkey"), eq("value"), any())).thenReturn(true);
        assertTrue(cacheService.setIfAbsent("newkey", "value", ExpireEnum.ONE_HOUR));
    }

    @Test
    void testSetIfAbsentFailed() {
        setUp();
        when(mockValueOps.setIfAbsent(eq("existingkey"), eq("value"), any())).thenReturn(false);
        assertFalse(cacheService.setIfAbsent("existingkey", "value", ExpireEnum.ONE_HOUR));
    }

    @Test
    void testExpire() {
        setUp();
        when(stringRedisTemplate.expire("key1", java.time.Duration.ofHours(1))).thenReturn(true);
        assertTrue(cacheService.expire("key1", ExpireEnum.ONE_HOUR));
    }

    @Test
    void testGetExpire() {
        setUp();
        when(stringRedisTemplate.getExpire("key1", TimeUnit.SECONDS)).thenReturn(1800L);
        assertEquals(1800L, cacheService.getExpire("key1"));
    }

    @Test
    void testExpireEnumValues() {
        assertNotNull(ExpireEnum.ONE_WEEK);
        assertNotNull(ExpireEnum.ONE_DAY);
        assertNotNull(ExpireEnum.THIRTY_MINUTES);
        assertNotNull(ExpireEnum.TEN_MINUTES);
        assertNotNull(ExpireEnum.FIVE_MINUTES);
        assertNotNull(ExpireEnum.ONE_HOUR);
    }
}