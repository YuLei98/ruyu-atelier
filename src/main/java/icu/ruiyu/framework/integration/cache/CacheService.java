package icu.ruiyu.framework.integration.cache;

import java.util.concurrent.TimeUnit;

/**
 * 缓存服务接口，定义 Redis 缓存操作和分布式锁功能
 */
public interface CacheService {

    /**
     * 存储键值对到 Redis
     */
    void set(String key, String value, ExpireEnum expire);

    /**
     * 根据 key 获取值
     */
    String get(String key);

    /**
     * 删除指定 key
     * @return true if key was deleted, false if key did not exist
     */
    boolean delete(String key);

    /**
     * 判断 key 是否存在
     */
    boolean exists(String key);

    /**
     * 原子操作：仅当 key 不存在时才设置值
     * @return true if key was set, false if key already existed
     */
    boolean setIfAbsent(String key, String value, ExpireEnum expire);

    /**
     * 给已有 key 设置过期时间
     * @return true if expiration was set, false if key did not exist
     */
    boolean expire(String key, ExpireEnum expire);

    /**
     * 获取 key 的剩余过期时间
     * @return 剩余时间（秒），-2 if key does not exist, -1 if key has no expiration
     */
    long getExpire(String key);

    /**
     * 获取锁（阻塞直到获取成功或被打断）
     * @param key 锁的资源标识
     * @param expire 锁的过期时间
     * @return true if lock acquired, false otherwise
     */
    boolean lock(String key, ExpireEnum expire);

    /**
     * 释放锁
     * @param key 锁的资源标识
     * @return true if lock released, false if not owner or lock not exist
     */
    boolean unlock(String key);

    /**
     * 尝试获取锁（带超时）
     * @param key 锁的资源标识
     * @param expire 锁的过期时间
     * @param timeoutSeconds 尝试获取锁的超时时间（秒）
     * @return true if lock acquired within timeout, false otherwise
     */
    boolean tryLock(String key, ExpireEnum expire, long timeoutSeconds);
}