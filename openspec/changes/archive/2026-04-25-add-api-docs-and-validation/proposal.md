## Why

当前脚手架缺少标准 API 文档能力和参数校验机制。开发人员在 DTO 中需要手动编写校验逻辑（如 `if (username == null || username.isBlank())`），代码冗余且不一致。添加 SpringDoc OpenAPI 和 Bean Validation 可以统一 API 文档输出和参数校验标准，提升开发体验和代码质量。

## What Changes

- 添加 `spring-boot-starter-validation` 依赖，启用 Bean Validation
- 添加 `springdoc-openapi-starter-webmvc-ui` 依赖，启用 Swagger UI
- 在 `SignUpReq`、`SignInReq` DTO 上添加 `@NotBlank`、`@Size` 等校验注解
- 在 `AuthController` 的 `@RequestBody` 参数上添加 `@Valid`
- 移除 `AuthController` 中的手动校验代码
- 在 `GlobalExceptionHandler` 中新增 `MethodArgumentNotValidException` 和 `ConstraintViolationException` 异常处理
- 新建 `OpenApiConfig` 配置 OpenAPI 元数据
- 在 `WebSecurityConfig` 中放行 Swagger UI 路径

## Capabilities

### New Capabilities
- `api-documentation`: Swagger/OpenAPI 集成，提供交互式 API 文档
- `bean-validation`: 基于 JSR-303 的参数校验能力，统一校验错误响应

### Modified Capabilities
- （无现有 capability 需要修改）

## Impact

- **新增依赖**: `spring-boot-starter-validation`, `springdoc-openapi-starter-webmvc-ui`
- **修改文件**: `pom.xml`, `SignUpReq.java`, `SignInReq.java`, `AuthController.java`, `GlobalExceptionHandler.java`, `WebSecurityConfig.java`
- **新增文件**: `OpenApiConfig.java`
- **API 变更**: 无破坏性变更，校验失败时返回 `CommonResult.fail()` 而非业务异常
- **安全**: Swagger UI 路径在开发环境放行，生产环境建议移除
