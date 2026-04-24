## Context

当前 `CacheClient` 只有 `get`/`set` 两个方法，需要扩展为完整的缓存操作工具类，并新增分布式锁功能。Redis 已通过 `StringRedisTemplate` 接入项目。

## Goals / Non-Goals

**Goals:**
- 完善常用缓存操作方法（删除、判断、TTL、原子写入）
- 提供基于 Redis 的分布式锁实现

**Non-Goals:**
- 不引入 Redisson 等高级库，使用原生 `StringRedisTemplate`
- 不支持公平锁、可重入锁等复杂场景

## Decisions

### 1. 分布式锁实现
采用最简方案：使用 `setIfAbsent` 原子写入 lock value（包含唯一标识），解锁时验证 value 后删除。

```
lock(key):
  if setIfAbsent(key, uniqueId, expire):
    return true
  return false

unlock(key):
  if get(key) == uniqueId:
    delete(key)
```

### 2. 唯一锁标识
使用 UUID 生成锁值，确保只能由加锁线程释放锁。

### 3. tryLock 带超时
通过循环重试 + sleep 实现尝试获取锁的超时等待。

## Risks / Trade-offs

- [风险] 网络抖动导致锁失效 → [缓解] 设置合理过期时间
- [风险] unlock 时 value 已被其他线程修改 → [缓解] 验证通过才删除