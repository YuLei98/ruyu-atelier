# API 限流功能设计

## Overview
为脚手架添加 API 限流能力，防止恶意请求和 API 滥用。

## Architecture

### 组件结构
```
icu.ruiyu.framework/
├── common/
│   ├── annotation/RateLimiter.java           # 限流注解
│   ├── config/RateLimiterProperties.java     # 配置属性类
│   └── aspect/RateLimiterAspect.java         # 注解式限流切面
├── exception/RateLimitException.java         # 限流异常
└── integration/ratelimit/
    ├── RateLimiterService.java               # 服务接口
    ├── impl/RateLimiterServiceImpl.java      # Redis+Lua实现
    ├── RateLimiterFilter.java               # 全局限流过滤器
    └── handler/RateLimitResponseHandler.java # 响应处理
```

### 限流算法

#### 滑动窗口算法
- 基于 Redis ZSET 存储请求时间戳
- ZREMRANGEBYSCORE 移除过期记录
- ZCARD 检查当前请求数

#### 令牌桶算法
- 基于 Redis Hash 存储 tokens 和 last_refill
- 按时间补充令牌
- 每次请求消耗令牌

### 限流维度
- **基于 IP**: 适用于未登录用户，读取 X-Forwarded-For 或 RemoteAddr
- **基于用户 ID**: 适用于已登录用户，从 SecurityContext 获取

## Usage

### 注解式限流
```java
@RateLimiter(maxRequests = 5, windowSeconds = 60)
@PostMapping("/login")
public CommonResult<String> login() { ... }
```

### 全局限流
配置 `ratelimit.global.enabled=true` 即可生效
