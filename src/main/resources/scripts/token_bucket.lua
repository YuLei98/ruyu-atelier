-- 令牌桶限流 Lua 脚本
-- KEYS[1]: 限流 key (Hash)
-- ARGV[1]: 当前时间戳（毫秒）
-- ARGV[2]: 桶容量
-- ARGV[3]: 令牌补充速率（个/秒）
-- ARGV[4]: 本次请求的令牌数

local key = KEYS[1]
local now = tonumber(ARGV[1])
local capacity = tonumber(ARGV[2])
local refill_rate = tonumber(ARGV[3])
local requested = tonumber(ARGV[4])

-- 获取当前令牌数和上次补充时间
local bucket = redis.call('HMGET', key, 'tokens', 'last_refill')
local tokens = tonumber(bucket[1])
local last_refill = tonumber(bucket[2])

-- 初始化桶
if tokens == nil then
    tokens = capacity
    last_refill = now
end

-- 计算应补充的令牌数
local elapsed = (now - last_refill) / 1000.0
local refill = elapsed * refill_rate
tokens = math.min(capacity, tokens + refill)

-- 检查是否有足够令牌
if tokens >= requested then
    tokens = tokens - requested
    redis.call('HMSET', key, 'tokens', tokens, 'last_refill', now)
    redis.call('PEXPIRE', key, 3600000)  -- 1小时过期
    return 1  -- 允许通过
else
    return 0  -- 限流拒绝
end
