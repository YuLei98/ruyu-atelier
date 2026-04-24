## Why

客户端重复提交（网络抖动、用户快速点击、前端 Bug 等）会导致后端业务被重复执行，例如用户被重复注册、订单被重复创建。需要在 Controller 层添加幂等性保护，防止重复请求影响业务数据一致性。

## What Changes

- 新增 `@Idempotent` 注解，支持方法级幂等性声明
- 新增 `IdempotentAspect` AOP 切面，基于 Redis 实现幂等检查
- 扩展 `CacheClient`，添加 `setIfAbsent` 方法支持分布式锁语义
- 在 `AuthController` 的 `/user/register` 和 `/user/login` 方法上应用幂等注解

## Capabilities

### New Capabilities
- `controller-idempotency`: Controller 方法幂等性保护能力，基于 Redis + AOP 实现，防止客户端重复提交

## Impact

- 新增：`icu.ruiyu.framework.common.annotation.Idempotent`
- 新增：`icu.ruiyu.framework.common.aspect.IdempotentAspect`
- 修改：`icu.ruiyu.framework.integration.cache.CacheClient`（添加 setIfAbsent 方法）
- 修改：`icu.ruiyu.framework.integration.security.controller.AuthController`
