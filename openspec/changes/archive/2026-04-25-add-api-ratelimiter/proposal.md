# API 限流功能提案

## Summary
为脚手架添加 API 限流能力，支持分布式限流（基于 Redis）和注解式限流。

## Goals
1. 支持全局限流（基于 IP/用户 ID）
2. 支持注解式限流（方法级）
3. 支持两种算法：滑动窗口、令牌桶
4. 配置可外部化

## Approach
- 使用 Redis + Lua 脚本保证原子性
- 通过 Filter 实现全局限流
- 通过 AOP 实现注解式限流
- 配置使用 application.yml（原生 UTF-8 支持）
