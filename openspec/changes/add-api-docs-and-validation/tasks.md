## 1. 添加依赖

- [ ] 1.1 在 pom.xml 中添加 `spring-boot-starter-validation` 依赖
- [ ] 1.2 在 pom.xml 中添加 `springdoc-openapi-starter-webmvc-ui` v2.3.0 依赖

## 2. DTO 添加校验注解

- [ ] 2.1 在 SignUpReq 的 username 字段添加 `@NotBlank` 和 `@Size(min=3, max=20)` 注解
- [ ] 2.2 在 SignUpReq 的 password 字段添加 `@NotBlank` 和 `@Size(min=6, max=30)` 注解
- [ ] 2.3 在 SignInReq 的 username 字段添加 `@NotBlank` 注解
- [ ] 2.4 在 SignInReq 的 password 字段添加 `@NotBlank` 注解

## 3. Controller 修改

- [ ] 3.1 在 AuthController.register 方法的 @RequestBody 参数上添加 `@Valid`
- [ ] 3.2 在 AuthController.login 方法的 @RequestBody 参数上添加 `@Valid`
- [ ] 3.3 移除 AuthController 中手动校验 username/password 的代码

## 4. 全局异常处理

- [ ] 4.1 在 GlobalExceptionHandler 中新增 `MethodArgumentNotValidException` 异常处理方法
- [ ] 4.2 在 GlobalExceptionHandler 中新增 `ConstraintViolationException` 异常处理方法

## 5. OpenAPI 配置

- [ ] 5.1 新建 `icu.ruiyu.framework.common.config.OpenApiConfig` 配置类
- [ ] 5.2 配置 OpenAPI 元数据（title: Framework API, version: 1.0.0）

## 6. 安全配置

- [ ] 6.1 在 WebSecurityConfig 中放行 `/swagger-ui/**` 路径
- [ ] 6.2 在 WebSecurityConfig 中放行 `/v3/api-docs/**` 路径
- [ ] 6.3 在 WebSecurityConfig 中放行 `/swagger-ui.html` 路径

## 7. 验证

- [ ] 7.1 执行 `mvn clean package` 构建成功
- [ ] 7.2 启动应用后访问 `http://localhost:8000/swagger-ui.html` 确认文档可访问
- [ ] 7.3 POST `/user/register` 不带参数，验证返回校验错误信息
