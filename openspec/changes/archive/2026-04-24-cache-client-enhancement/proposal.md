## Why

当前 `CacheClient` 只提供了基础的 `get`/`set` 方法，生产级缓存客户端还需要常用的删除、判断存在、原子写入、TTL 管理等能力。当前实现无法满足实际业务需求。

## What Changes

- 新增 `delete(key)` — 删除缓存
- 新增 `exists(key)` — 判断 key 是否存在
- 新增 `setIfAbsent(key, value, expire)` — 原子操作，key 不存在才写入
- 新增 `expire(key, expire)` — 给已有 key 设置过期时间
- 新增 `getExpire(key)` — 获取 key 剩余 TTL
- 新增分布式锁 `lock(key, expire)` / `unlock(key)` — 基于 setIfAbsent 实现的简单分布式锁
- 新增 `tryLock(key, expire, timeout)` — 带等待超时获取锁
- 完善 `ExpireEnum` 增加更多常用过期时间选项
- 编写 `CacheClientTest` 单元测试覆盖所有方法

## Capabilities

### New Capabilities
- `cache-client-api`: 完善 CacheClient 的常用方法，包括删除、判断、原子写入、TTL 管理
- `distributed-lock`: 基于 Redis 的分布式锁实现，支持 tryLock / lock / unlock

### Modified Capabilities
- (none)

## Impact

- `CacheClient.java` 新增 5 个方法
- `ExpireEnum.java` 新增过期时间选项
- 新增 `CacheClientTest.java` 测试类
- 无需新增依赖，使用现有的 `StringRedisTemplate`