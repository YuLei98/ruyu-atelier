## 1. 基础设施

- [x] 1.1 创建 `@RateLimiter` 注解
- [x] 1.2 创建 `RateLimitException` 异常类
- [x] 1.3 创建 `RateLimiterProperties` 配置类
- [x] 1.4 创建 Lua 脚本（滑动窗口、令牌桶）

## 2. 核心服务

- [x] 2.1 创建 `RateLimiterService` 接口
- [x] 2.2 实现 `RateLimiterServiceImpl`（Redis + Lua）
- [x] 2.3 创建 `RateLimiterAspect` AOP 切面

## 3. 全局限流

- [x] 3.1 创建 `RateLimiterFilter` 过滤器
- [x] 3.2 创建 `RateLimitResponseHandler` 响应处理
- [x] 3.3 更新 `WebSecurityConfig` 注册过滤器
- [x] 3.4 更新 `GlobalExceptionHandler` 处理限流异常

## 4. 配置

- [x] 4.1 创建 `application.yml`（替换 properties，支持中文）
- [x] 4.2 添加限流配置项

## 5. 验证

- [x] 5.1 编译测试通过
- [x] 5.2 限流功能验证通过
- [x] 5.3 响应编码正确（中文正常显示）
