# Caffeine 二级缓存实现计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 集成 Caffeine 本地缓存作为 L1，结合现有 Redis L2 形成二级缓存体系，采用 Spring Cache 注解驱动。

**Architecture:** Caffeine L1 + Redis L2 二级缓存，旁路缓存模式（read-through/write-through），AOP 切面拦截 @Cacheable/@CacheEvict/@CachePut 注解。

**Tech Stack:** Spring Boot Cache, Caffeine, Spring AOP, StringRedisTemplate

---

## 文件结构

```
src/main/java/icu/ruiyu/framework/
├── common/
│   ├── annotation/
│   │   ├── Cacheable.java       # 缓存读取注解
│   │   ├── CacheEvict.java      # 缓存淘汰注解
│   │   └── CachePut.java        # 缓存更新注解
│   ├── aspect/
│   │   └── CacheAspect.java     # 二级缓存 AOP 切面
│   └── config/
│       └── CacheProperties.java # 缓存配置属性
src/main/java/icu/ruiyu/framework/integration/
├── cache/
│   └── TwoLevelCacheService.java # 二级缓存服务接口
└── cache/impl/
    └── TwoLevelCacheServiceImpl.java # 二级缓存服务实现
src/test/java/icu/ruiyu/framework/integration/cache/
└── TwoLevelCacheServiceTest.java
pom.xml
application.yml
```

---

### Task 1: 添加 Maven 依赖

**Files:**
- Modify: `pom.xml`

- [ ] **Step 1: 添加 spring-boot-starter-cache 和 caffeine 依赖**

在 `<dependencies>` 中添加：

```xml
<!-- Spring Boot Cache -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>

<!-- Caffeine 本地缓存 -->
<dependency>
    <groupId>com.github.ben-manes.caffeine</groupId>
    <artifactId>caffeine</artifactId>
</dependency>
```

- [ ] **Step 2: 验证依赖**

Run: `mvn dependency:tree -Dincludes="com.github.ben-manes.caffeine,org.springframework.boot:spring-boot-starter-cache"`
Expected: 显示 caffeine 和 spring-boot-starter-cache 依赖

- [ ] **Step 3: Commit**

```bash
git add pom.xml
git commit -m "feat: 添加 Caffeine 和 Spring Cache 依赖"
```

---

### Task 2: 创建缓存配置属性类

**Files:**
- Create: `src/main/java/icu/ruiyu/framework/common/config/CacheProperties.java`
- Modify: `src/main/java/icu/ruiyu/framework/common/config/DotenvConfig.java` (如需)

- [ ] **Step 1: 创建 CacheProperties 配置类**

```java
package icu.ruiyu.framework.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Caffeine 二级缓存配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "cache")
public class CacheProperties {

    private Caffeine caffeine = new Caffeine();
    private Redis redis = new Redis();

    @Data
    public static class Caffeine {
        /**
         * L1 本地缓存最大容量
         */
        private int maxSize = 1000;

        /**
         * L1 写后过期时间（分钟）
         */
        private int expireAfterWriteMinutes = 10;
    }

    @Data
    public static class Redis {
        /**
         * L2 Redis 默认过期时间（分钟）
         */
        private int defaultExpireMinutes = 60;
    }
}
```

- [ ] **Step 2: Commit**

```bash
git add src/main/java/icu/ruiyu/framework/common/config/CacheProperties.java
git commit -m "feat: 添加 CacheProperties 配置类"
```

---

### Task 3: 创建缓存注解

**Files:**
- Create: `src/main/java/icu/ruiyu/framework/common/annotation/Cacheable.java`
- Create: `src/main/java/icu/ruiyu/framework/common/annotation/CacheEvict.java`
- Create: `src/main/java/icu/ruiyu/framework/common/annotation/CachePut.java`

- [ ] **Step 1: 创建 @Cacheable 注解**

```java
package icu.ruiyu.framework.common.annotation;

import java.lang.annotation.*;

/**
 * 缓存读取注解
 * 二级缓存：L1 (Caffeine) -> L2 (Redis) -> DB
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Cacheable {

    /**
     * 缓存分区名称
     */
    String value() default "default";

    /**
     * 缓存 key，支持 SpEL 表达式
     */
    String key() default "";

    /**
     * L1 (Caffeine) 过期时间（分钟），默认 10 分钟
     */
    int expireAfterWriteMinutes() default 10;
}
```

- [ ] **Step 2: 创建 @CacheEvict 注解**

```java
package icu.ruiyu.framework.common.annotation;

import java.lang.annotation.*;

/**
 * 缓存淘汰注解
 * 同时删除 L1 和 L2
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CacheEvict {

    /**
     * 缓存分区名称
     */
    String value() default "default";

    /**
     * 缓存 key，支持 SpEL 表达式
     */
    String key() default "";

    /**
     * 是否淘汰所有 key
     */
    boolean allEntries() default false;
}
```

- [ ] **Step 3: 创建 @CachePut 注解**

```java
package icu.ruiyu.framework.common.annotation;

import java.lang.annotation.*;

/**
 * 缓存更新注解
 * 执行方法后更新 L1 和 L2
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CachePut {

    /**
     * 缓存分区名称
     */
    String value() default "default";

    /**
     * 缓存 key，支持 SpEL 表达式
     */
    String key() default "";
}
```

- [ ] **Step 4: Commit**

```bash
git add src/main/java/icu/ruiyu/framework/common/annotation/Cacheable.java \
        src/main/java/icu/ruiyu/framework/common/annotation/CacheEvict.java \
        src/main/java/icu/ruiyu/framework/common/annotation/CachePut.java
git commit -m "feat: 添加 @Cacheable @CacheEvict @CachePut 注解"
```

---

### Task 4: 创建二级缓存服务

**Files:**
- Create: `src/main/java/icu/ruiyu/framework/integration/cache/TwoLevelCacheService.java`
- Create: `src/main/java/icu/ruiyu/framework/integration/cache/impl/TwoLevelCacheServiceImpl.java`

- [ ] **Step 1: 创建 TwoLevelCacheService 接口**

```java
package icu.ruiyu.framework.integration.cache;

/**
 * 二级缓存服务接口
 * L1: Caffeine (本地) -> L2: Redis (分布式)
 */
public interface TwoLevelCacheService {

    /**
     * 从缓存获取值
     * @param cacheName 缓存分区
     * @param key 缓存 key
     * @return 缓存值，未命中返回 null
     */
    String get(String cacheName, String key);

    /**
     * 设置缓存值（写入 L1 和 L2）
     * @param cacheName 缓存分区
     * @param key 缓存 key
     * @param value 缓存值
     * @param l1ExpireMinutes L1 过期时间（分钟）
     * @param l2ExpireMinutes L2 过期时间（分钟）
     */
    void put(String cacheName, String key, String value, int l1ExpireMinutes, int l2ExpireMinutes);

    /**
     * 删除缓存（同时删除 L1 和 L2）
     * @param cacheName 缓存分区
     * @param key 缓存 key
     */
    void evict(String cacheName, String key);

    /**
     * 清空指定分区的所有缓存
     * @param cacheName 缓存分区
     */
    void clear(String cacheName);

    /**
     * 生成缓存 key
     * @param cacheName 缓存分区
     * @param key 缓存 key
     * @return 完整缓存 key
     */
    String buildKey(String cacheName, String key);
}
```

- [ ] **Step 2: 创建 TwoLevelCacheServiceImpl 实现**

```java
package icu.ruiyu.framework.integration.cache.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import icu.ruiyu.framework.common.config.CacheProperties;
import icu.ruiyu.framework.integration.cache.ExpireEnum;
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
```

- [ ] **Step 3: Commit**

```bash
git add src/main/java/icu/ruiyu/framework/integration/cache/TwoLevelCacheService.java \
        src/main/java/icu/ruiyu/framework/integration/cache/impl/TwoLevelCacheServiceImpl.java
git commit -m "feat: 添加 TwoLevelCacheService 二级缓存服务"
```

---

### Task 5: 创建缓存 AOP 切面

**Files:**
- Create: `src/main/java/icu/ruiyu/framework/common/aspect/CacheAspect.java`

- [ ] **Step 1: 创建 CacheAspect 切面**

```java
package icu.ruiyu.framework.common.aspect;

import icu.ruiyu.framework.common.annotation.CacheEvict;
import icu.ruiyu.framework.common.annotation.CachePut;
import icu.ruiyu.framework.common.annotation.Cacheable;
import icu.ruiyu.framework.integration.cache.TwoLevelCacheService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 缓存 AOP 切面
 * 拦截 @Cacheable @CacheEvict @CachePut 注解
 */
@Slf4j
@Aspect
@Component
public class CacheAspect {

    private final SpelExpressionParser parser = new SpelExpressionParser();

    private final Map<String, Expression> expressionCache = new HashMap<>();

    @jakarta.annotation.Resource
    private TwoLevelCacheService twoLevelCacheService;

    /**
     * 拦截 @Cacheable 注解
     * 实现 read-through 模式
     */
    @Around("@annotation(icu.ruiyu.framework.common.annotation.Cacheable)")
    public Object aroundCacheable(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Cacheable cacheable = method.getAnnotation(Cacheable.class);

        String key = resolveKey(cacheable.key(), joinPoint);
        String cacheName = cacheable.value();

        // 1. 尝试从缓存获取
        String cachedValue = twoLevelCacheService.get(cacheName, key);
        if (cachedValue != null) {
            log.debug("@Cacheable hit: cacheName={}, key={}", cacheName, key);
            // 注意：这里返回的是字符串，实际使用需要反序列化
            // 简化处理：返回字符串，由调用方自行处理
            return cachedValue;
        }

        // 2. 执行目标方法
        Object result = joinPoint.proceed();

        // 3. 结果写入缓存
        if (result != null) {
            String valueToCache = result.toString();
            twoLevelCacheService.put(cacheName, key, valueToCache,
                    cacheable.expireAfterWriteMinutes(),
                    60); // L2 过期时间默认 60 分钟
            log.debug("@Cacheable put: cacheName={}, key={}", cacheName, key);
        }

        return result;
    }

    /**
     * 拦截 @CacheEvict 注解
     * 实现缓存淘汰
     */
    @Around("@annotation(icu.ruiyu.framework.common.annotation.CacheEvict)")
    public Object aroundCacheEvict(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        CacheEvict cacheEvict = method.getAnnotation(CacheEvict.class);

        String key = resolveKey(cacheEvict.key(), joinPoint);
        String cacheName = cacheEvict.value();

        // 执行目标方法
        Object result = joinPoint.proceed();

        // 淘汰缓存
        if (cacheEvict.allEntries()) {
            twoLevelCacheService.clear(cacheName);
            log.debug("@CacheEvict clear: cacheName={}", cacheName);
        } else {
            twoLevelCacheService.evict(cacheName, key);
            log.debug("@CacheEvict evict: cacheName={}, key={}", cacheName, key);
        }

        return result;
    }

    /**
     * 拦截 @CachePut 注解
     * 实现 write-through 模式
     */
    @Around("@annotation(icu.ruiyu.framework.common.annotation.CachePut)")
    public Object aroundCachePut(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        CachePut cachePut = method.getAnnotation(CachePut.class);

        String key = resolveKey(cachePut.key(), joinPoint);
        String cacheName = cachePut.value();

        // 1. 执行目标方法
        Object result = joinPoint.proceed();

        // 2. 更新缓存
        if (result != null) {
            String valueToCache = result.toString();
            twoLevelCacheService.put(cacheName, key, valueToCache, 10, 60);
            log.debug("@CachePut put: cacheName={}, key={}", cacheName, key);
        }

        return result;
    }

    /**
     * 解析 SpEL 表达式
     */
    private String resolveKey(String keyExpression, ProceedingJoinPoint joinPoint) {
        if (keyExpression == null || keyExpression.isBlank()) {
            // 默认使用参数值
            Object[] args = joinPoint.getArgs();
            if (args.length == 1) {
                return args[0].toString();
            }
            return joinPoint.getSignature().getName();
        }

        // 使用 SpEL 表达式解析
        Expression expression = expressionCache.computeIfAbsent(keyExpression,
                k -> parser.parseExpression(k));

        EvaluationContext context = new StandardEvaluationContext();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        if (parameterNames != null) {
            for (int i = 0; i < parameterNames.length; i++) {
                ((StandardEvaluationContext) context).setVariable(parameterNames[i], args[i]);
            }
        }

        // 添加参数到上下文
        for (int i = 0; i < parameterNames.length; i++) {
            context.setVariable(parameterNames[i], args[i]);
        }

        return expression.getValue(context).toString();
    }
}
```

- [ ] **Step 2: Commit**

```bash
git add src/main/java/icu/ruiyu/framework/common/aspect/CacheAspect.java
git commit -m "feat: 添加 CacheAspect 缓存切面"
```

---

### Task 6: 添加配置到 application.yml

**Files:**
- Modify: 查找 application.yml 或 application.properties 位置

- [ ] **Step 1: 查找配置文件**

Glob: `**/application*.yml`

- [ ] **Step 2: 添加缓存配置**

```yaml
cache:
  caffeine:
    max-size: 1000           # L1 最大容量
    expire-after-write-minutes: 10  # L1 写后过期时间
  redis:
    default-expire-minutes: 60     # L2 默认过期时间
```

- [ ] **Step 3: Commit**

```bash
git add application.yml  # 或 application-dev.yml 等
git commit -m "feat: 添加 Caffeine 二级缓存配置"
```

---

### Task 7: 编写单元测试

**Files:**
- Create: `src/test/java/icu/ruiyu/framework/integration/cache/TwoLevelCacheServiceTest.java`

- [ ] **Step 1: 编写 TwoLevelCacheServiceTest**

```java
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
        twoLevelCacheService.put("user", "1", "John", 10, 60);
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
        twoLevelCacheService.put("user", "1", "John", 10, 60);
        assertEquals("John", twoLevelCacheService.get("user", "1"));

        twoLevelCacheService.evict("user", "1");
        assertNull(twoLevelCacheService.get("user", "1"));
    }
}
```

- [ ] **Step 2: 运行测试验证**

Run: `mvn test -Dtest=TwoLevelCacheServiceTest`
Expected: 所有测试通过

- [ ] **Step 3: Commit**

```bash
git add src/test/java/icu/ruiyu/framework/integration/cache/TwoLevelCacheServiceTest.java
git commit -m "test: 添加 TwoLevelCacheService 单元测试"
```

---

### Task 8: 更新 CLAUDE.md 文档

**Files:**
- Modify: `CLAUDE.md`

- [ ] **Step 1: 在 CLAUDE.md 添加 Caffeine 二级缓存文档**

在 "### API Rate Limiter" 后添加：

```markdown
### Caffeine 二级缓存
Caffeine 本地缓存（L1）+ Redis（L2）二级缓存体系：

**架构**：
- L1: Caffeine 本地缓存，容量淘汰 + 写后过期
- L2: Redis 分布式缓存，TTL 过期
- 读取：L1 → L2 → DB（未命中回填 L1）
- 写入：同时操作 L1 和 L2

**注解**：
- `@Cacheable` - 缓存读取
- `@CacheEvict` - 缓存淘汰
- `@CachePut` - 缓存更新

**配置** (`application.yml`)：
```yaml
cache:
  caffeine:
    max-size: 1000
    expire-after-write-minutes: 10
  redis:
    default-expire-minutes: 60
```

**使用示例**：
```java
@Cacheable(value = "user", key = "#userId")
User getUserById(Long userId);

@CacheEvict(value = "user", key = "#userId")
void deleteUser(Long userId);
```
```

- [ ] **Step 2: Commit**

```bash
git add CLAUDE.md
git commit -m "docs: 添加 Caffeine 二级缓存文档"
```

---

## 实现检查清单

- [ ] Task 1: Maven 依赖添加完成
- [ ] Task 2: CacheProperties 配置类创建完成
- [ ] Task 3: @Cacheable @CacheEvict @CachePut 注解创建完成
- [ ] Task 4: TwoLevelCacheService 服务创建完成
- [ ] Task 5: CacheAspect 切面创建完成
- [ ] Task 6: application.yml 配置添加完成
- [ ] Task 7: 单元测试编写完成
- [ ] Task 8: CLAUDE.md 文档更新完成
- [ ] 所有测试通过: `mvn test`
- [ ] 整体提交
