# Caffeine 二级缓存集成设计

## 1. 概述

为 `ruiyu-atelier` 项目集成 Caffeine 本地缓存作为 L1，结合现有 Redis L2 形成二级缓存体系。采用 Spring Cache 注解驱动 + AOP 切面实现，业务代码只需添加注解即可启用缓存。

## 2. 架构

```
@Cacheable ──► CacheAspect ──► [L1: Caffeine] ──► 命中返回
                                    │
                                 未命中
                                    ▼
                              [L2: Redis] ──► 命中回填L1返回
                                    │
                                 未命中
                                    ▼
                              执行目标方法 ──► 结果写入L1和L2 ──► 返回
```

## 3. 组件清单

| 类/文件 | 位置 | 职责 |
|---------|------|------|
| `CachingConfig` | `icu.ruiyu.framework.common.config` | Caffeine 和 Spring Cache 配置 |
| `CacheAspect` | `icu.ruiyu.framework.common.aspect` | 二级缓存 AOP 切面实现 |
| `@Cacheable` | `icu.ruiyu.framework.common.annotation` | 缓存读取注解（复用 Spring 注解） |
| `@CacheEvict` | `icu.ruiyu.framework.common.annotation` | 缓存淘汰注解 |
| `@CachePut` | `icu.ruiyu.framework.common.annotation` | 缓存更新注解 |

## 4. 缓存读取流程（read-through）

```
1. 方法被 @Cacheable 标注
2. Aspect 拦截，提取 cacheName 和 key
3. 先查 Caffeine L1：
   - 命中 → 直接返回 L1 值
   - 未命中 → 查 Redis L2
4. L2 命中 → 回填 Caffeine L1，返回值
5. L2 未命中 → 执行目标方法，结果写入 Redis L2 和 Caffeine L1，返回值
```

## 5. 缓存写入流程（write-through）

```
@CacheEvict:
1. 删除 Redis L2 对应 key
2. 删除 Caffeine L1 对应 key（如果配置了）

@CachePut:
1. 执行目标方法
2. 结果写入 Redis L2
3. 更新 Caffeine L1
```

## 6. 淘汰策略

| 层级 | 淘汰方式 | 配置项 |
|------|----------|--------|
| L1 (Caffeine) | 基于容量 `maximumSize` | `cache.caffeine.max-size=1000` |
| L1 (Caffeine) | 写后过期 `expireAfterWrite` | `cache.caffeine.expire-after-write=10m` |
| L2 (Redis) | TTL 过期 | `cache.redis.default-expire=1h` |

## 7. 注解定义

```java
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Cacheable(value = "default", key = "#key")
public @interface Cacheable {
    String value() default "default";
    String key() default "";
    long expireAfterWriteSeconds() default 600; // L1 过期时间
}

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@CacheEvict(value = "default", key = "#key", allEntries = false)
public @interface CacheEvict {
    String value() default "default";
    String key() default "";
    boolean allEntries() default false;
}
```

## 8. 配置项

```yaml
cache:
  caffeine:
    max-size: 1000           # L1 最大容量
    expire-after-write: 10m  # L1 写后过期时间
  redis:
    default-expire: 1h      # L2 默认过期时间
```

## 9. 依赖添加

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>
<dependency>
    <groupId>com.github.ben-manes.caffeine</groupId>
    <artifactId>caffeine</artifactId>
</dependency>
```

## 10. 使用示例

```java
@Resource
private UserMapper userMapper;

// 读取缓存
@Cacheable(value = "user", key = "#userId")
public User getUserById(Long userId) {
    return userMapper.selectById(userId);
}

// 删除缓存
@CacheEvict(value = "user", key = "#userId")
public void deleteUser(Long userId) {
    userMapper.deleteById(userId);
}

// 更新缓存
@CachePut(value = "user", key = "#user.id")
public User updateUser(User user) {
    userMapper.updateById(user);
    return user;
}

// 清空分区
@CacheEvict(value = "user", allEntries = true)
public void clearUserCache() {
}
```

## 11. 注意事项

- **key 表达式**：使用 SpEL 表达式，如 `#userId`、`#user.id`
- **序列化**：L2 (Redis) 使用 String 序列化，L1 (Caffeine) 使用对象本身
- **分布式**：多实例部署时，L1 独立，L2 共享，保证最终一致
- **TTL 设计**：L1 过期时间应短于 L2，保证 L2 是 source of truth

## 12. 测试

- `CaffeineCacheTest` — 验证二级缓存读写流程
- `CacheAspectTest` — 验证 AOP 切面拦截
