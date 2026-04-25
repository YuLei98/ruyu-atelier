-- 滑动窗口限流 Lua 脚本
-- KEYS[1]: 限流 key (ZSET)
-- ARGV[1]: 当前时间戳（毫秒）
-- ARGV[2]: 窗口大小（毫秒）
-- ARGV[3]: 最大请求数
-- ARGV[4]: 初始时间戳（窗口开始）

local key = KEYS[1]
local now = tonumber(ARGV[1])
local window = tonumber(ARGV[2])
local max_requests = tonumber(ARGV[3])
local window_start = now - window

-- 移除窗口外的旧记录
redis.call('ZREMRANGEBYSCORE', key, '-inf', window_start)

-- 获取当前窗口内的请求数
local current = redis.call('ZCARD', key)

if current < max_requests then
    -- 未超过限流阈值，添加新请求
    redis.call('ZADD', key, now, now .. ':' .. math.random())
    redis.call('PEXPIRE', key, window)
    return 1  -- 允许通过
else
    return 0  -- 限流拒绝
end
