## Context

AuthController 当前直接返回字符串（如 "注册成功"、"认证失败"），无法返回正确的 HTTP 状态码，API 调用方难以处理。

需要实现标准的 JSON 响应格式：`{code, message, data}`

## Goals / Non-Goals

**Goals:**
- 统一 AuthController 所有接口的 JSON 响应格式
- 返回适当的 HTTP 状态码（200 成功、400 参数错误、401 认证失败、500 服务器错误）
- 利用 Spring 的全局异常处理机制

**Non-Goals:**
- 不修改 UserService/UserServiceImpl 的业务逻辑
- 不涉及其他 Controller 的修改
- 不改变数据库 schema

## Decisions

### 1. 创建 CommonResult 响应包装类（全局通用）
```java
@Data
public class CommonResult<T> {
    private int code;
    private String message;
    private T data;

    public static <T> CommonResult<T> success(T data) { ... }
    public static <T> CommonResult<T> success(String message) { ... }
    public static <T> CommonResult<T> error(int code, String message) { ... }
}
```
位置：`src/main/java/icu/ruiyu/framework/common/CommonResult.java`（或其他全局包）

### 2. AuthController 返回类型改为 CommonResult<?>
```java
@PostMapping("/register")
public CommonResult<Void> register(@RequestBody SignInReq req) {
    // 验证参数，返回 CommonResult.error(400, "用户名不能为空")
    // 成功返回 CommonResult.success("注册成功")
}
```

### 3. 异常处理通过 try-catch 或 ControllerAdvice
- 认证失败（BadCredentialsException）→ 返回 401 + "认证失败"
- 用户不存在（UsernameNotFoundException）→ 返回 401 + "用户不存在"
- 参数校验失败 → 返回 400

## Risks / Trade-offs

- [风险] 修改接口返回值可能影响前端调用 → 需要协调前后端一起修改
- [风险] AuthController 原有逻辑需要较大改动 → 变更范围可控，仅涉及 AuthController

## Migration Plan

1. 创建 `CommonResult` 类
2. 修改 `AuthController` 所有方法返回 `CommonResult<?>`
3. 添加 `@ControllerAdvice` 处理认证相关异常
4. 验证所有测试通过

## Open Questions

无