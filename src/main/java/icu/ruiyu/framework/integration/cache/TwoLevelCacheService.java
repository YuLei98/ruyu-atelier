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