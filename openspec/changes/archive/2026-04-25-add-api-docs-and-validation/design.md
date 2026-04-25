## Context

当前脚手架缺少 API 文档和标准参数校验能力。现有 DTO（SignUpReq、SignInReq）无校验注解，AuthController 中存在手动校验代码：
```java
if (req.getUsername() == null || req.getUsername().isBlank()) {
    return CommonResult.error(400, "用户名不能为空");
}
```

## Goals / Non-Goals

**Goals:**
- 提供 Swagger UI 在线 API 文档
- 使用 Bean Validation 注解替代手动校验
- 统一校验失败时的错误响应格式

**Non-Goals:**
- 不修改现有 API 接口的返回结构
- 不添加数据库校验
- 不涉及 RPC/API 版本管理

## Decisions

1. **SpringDoc vs Springfox**
   - 选择 `springdoc-openapi-starter-webmvc-ui` v2.3.0（Spring Boot 3.x 兼容）
   - Springfox 已停止维护，不支持 Spring Boot 3.x

2. **校验注解选择**
   - `@NotBlank` 用于必填字段校验
   - `@Size` 用于字符串长度限制
   - SignInReq 密码字段仅用 `@NotBlank`，避免枚举攻击风险

3. **错误响应格式**
   - 使用现有 `CommonResult.fail()` 返回校验错误
   - 多个校验错误消息用中文逗号连接

4. **全局异常处理**
   - `MethodArgumentNotValidException`: 处理 `@RequestBody @Valid` 校验失败
   - `ConstraintViolationException`: 处理方法级校验（如 `@RequestParam @Min`）

## Risks / Trade-offs

- [Risk] Swagger UI 在生产环境暴露 → **Mitigation**: 放行路径仅用于开发，生产环境需移除
- [Risk] 校验消息国际化 → **Mitigation**: 当前使用中文 message，后续可扩展 i18n

## Open Questions

- 无
