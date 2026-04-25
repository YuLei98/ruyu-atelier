package icu.ruiyu.framework.integration.ratelimit.impl;

import icu.ruiyu.framework.common.annotation.RateLimiter;
import icu.ruiyu.framework.integration.ratelimit.RateLimiterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.Collections;

/**
 * 限流服务实现
 * 使用 Redis + Lua 脚本实现原子操作
 */
@Slf4j
@Service
public class RateLimiterServiceImpl implements RateLimiterService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private DefaultRedisScript<Long> slidingWindowScript;
    private DefaultRedisScript<Long> tokenBucketScript;

    @PostConstruct
    public void init() {
        // 初始化滑动窗口脚本
        slidingWindowScript = new DefaultRedisScript<>();
        slidingWindowScript.setScriptSource(new ResourceScriptSource(
                new ClassPathResource("scripts/sliding_window.lua")));
        slidingWindowScript.setResultType(Long.class);

        // 初始化令牌桶脚本
        tokenBucketScript = new DefaultRedisScript<>();
        tokenBucketScript.setScriptSource(new ResourceScriptSource(
                new ClassPathResource("scripts/token_bucket.lua")));
        tokenBucketScript.setResultType(Long.class);
    }

    @Override
    public boolean isAllowed(String key, int windowSeconds, int maxRequests) {
        long now = System.currentTimeMillis();
        long windowMillis = windowSeconds * 1000L;

        Long result = stringRedisTemplate.execute(
                slidingWindowScript,
                Collections.singletonList(key),
                String.valueOf(now),
                String.valueOf(windowMillis),
                String.valueOf(maxRequests),
                String.valueOf(now - windowMillis)
        );

        boolean allowed = result != null && result == 1L;
        if (!allowed) {
            log.debug("Rate limit exceeded for key: {}", key);
        }
        return allowed;
    }

    @Override
    public boolean isAllowed(String key, int capacity, double refillRate, int requested) {
        long now = System.currentTimeMillis();

        Long result = stringRedisTemplate.execute(
                tokenBucketScript,
                Collections.singletonList(key),
                String.valueOf(now),
                String.valueOf(capacity),
                String.valueOf(refillRate),
                String.valueOf(requested)
        );

        boolean allowed = result != null && result == 1L;
        if (!allowed) {
            log.debug("Rate limit exceeded for key: {}", key);
        }
        return allowed;
    }

    @Override
    public boolean isAllowed(String key, RateLimiter rateLimiter) {
        if (rateLimiter.algorithm() == RateLimiter.Algorithm.TOKEN_BUCKET) {
            return isAllowed(key, rateLimiter.bucketCapacity(),
                    rateLimiter.refillRate(), 1);
        } else {
            return isAllowed(key, rateLimiter.windowSeconds(),
                    rateLimiter.maxRequests());
        }
    }
}
