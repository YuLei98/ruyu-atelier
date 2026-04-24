## Why

AuthController 当前返回值格式不标准：直接返回字符串而非 JSON，无法返回正确的 HTTP 状态码，错误处理不规范。这导致 API 调用方难以处理响应，也无法与标准 REST API 规范兼容。

## What Changes

- 创建统一的 JSON 响应格式 `{code, message, data}`
- 创建 `CommonResult` 响应包装类
- 修改 AuthController 所有接口返回 `CommonResult`
- 错误情况通过抛出异常，由全局异常处理器处理并返回标准 JSON
- HTTP 状态码根据结果类型返回 200/400/401/500

## Capabilities

### New Capabilities
- `auth-response-format`: 定义 AuthController 所有接口的标准 JSON 响应格式，包括成功/失败/错误等场景

## Impact

- `AuthController.java` - 修改所有接口返回类型
- `CommonResult.java` - 统一响应包装类（放在通用包下，非 auth 专属）
- 全局异常处理器处理认证失败等场景