## 1. CacheClient 基础方法完善

- [x] 1.1 添加 `delete(key)` 方法
- [x] 1.2 添加 `exists(key)` 方法
- [x] 1.3 添加 `expire(key, expire)` 方法
- [x] 1.4 添加 `getExpire(key)` 方法
- [x] 1.5 添加 `setIfAbsent(key, value, expire)` 方法

## 2. ExpireEnum 过期时间扩展

- [x] 2.1 添加 FIVE_MINUTES, TEN_MINUTES, THIRTY_MINUTES, ONE_WEEK 等选项

## 3. 分布式锁实现

- [x] 3.1 添加 `lock(key, expire)` 方法
- [x] 3.2 添加 `unlock(key)` 方法
- [x] 3.3 添加 `tryLock(key, expire, timeout)` 方法

## 4. 单元测试

- [x] 4.1 编写 CacheClientTest 覆盖所有方法