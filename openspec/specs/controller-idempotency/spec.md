# controller-idempotency Specification

## Purpose
TBD - created by archiving change controller-idempotent. Update Purpose after archive.
## Requirements
### Requirement: Idempotent annotation for controller methods
系统 SHALL 提供 `@Idempotent` 注解，标注在 Controller 方法上以启用幂等性检查。

#### Scenario: Successful idempotent request (first request)
- **WHEN** 客户端发起 POST /user/register 请求（username: "test", password: "123456"）
- **AND** 该请求在 Redis 中无对应幂等 key
- **THEN** 系统设置幂等 key 并执行业务逻辑
- **AND** 返回业务处理结果

#### Scenario: Duplicate request rejected
- **WHEN** 客户端在 60 秒内重复发起相同的 POST /user/register 请求
- **AND** Redis 中已存在对应幂等 key
- **THEN** 系统拒绝请求
- **AND** 返回 CommonResult.error(409, "请求已提交，请勿重复操作")

### Requirement: Configurable idempotent key prefix
`@Idempotent` 注解 SHALL 支持自定义 key 前缀，默认使用方法名。

#### Scenario: Custom key prefix
- **WHEN** 方法标注 `@Idempotent(keyPrefix = "customRegister")`
- **AND** 客户端发起请求
- **THEN** 幂等 key 为 "idempotent:customRegister"

#### Scenario: Default key prefix
- **WHEN** 方法标注 `@Idempotent`（无 keyPrefix）
- **AND** 方法为 AuthController.register
- **THEN** 幂等 key 为 "idempotent:register"

### Requirement: Configurable expiration time
`@Idempotent` 注解 SHALL 支持自定义过期时间，默认 60 秒。

#### Scenario: Custom expiration time
- **WHEN** 方法标注 `@Idempotent(expireSeconds = 120)`
- **AND** 客户端发起请求
- **AND** 请求被首次处理
- **THEN** 幂等 key 在 120 秒后过期

### Requirement: Redis setIfAbsent operation
CacheClient SHALL 提供 setIfAbsent 方法，支持分布式锁语义。

#### Scenario: setIfAbsent returns true on success
- **WHEN** 调用 cacheClient.setIfAbsent("key1", "value1", ExpireEnum.ONE_MINUTE)
- **AND** key1 不存在
- **THEN** 返回 true
- **AND** Redis 中 key1 被设置

#### Scenario: setIfAbsent returns false when key exists
- **WHEN** 调用 cacheClient.setIfAbsent("key1", "value1", ExpireEnum.ONE_MINUTE)
- **AND** key1 已存在
- **THEN** 返回 false
- **AND** Redis 中 key1 值不变

