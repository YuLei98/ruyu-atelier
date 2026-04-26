-- 释放分布式锁 Lua 脚本
-- KEYS[1]: 锁的 key
-- ARGV[1]: 锁的持有者 value (UUID)
-- 返回: 1=成功释放, 0=锁不存在或不是持有者

local key = KEYS[1]
local expectedValue = ARGV[1]

local currentValue = redis.call('GET', key)

if currentValue == expectedValue then
    redis.call('DEL', key)
    return 1
else
    return 0
end
