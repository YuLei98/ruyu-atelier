# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run Commands

```bash
# Build
mvn clean package

# Run
mvn spring-boot:run

# Run tests
mvn test

# Package and run JAR
mvn clean install
java -jar target/framework-0.0.1-SNAPSHOT.jar
```

Server runs on port **8000**.

## Tech Stack

- **Framework**: Spring Boot 3.2.10, Java 17
- **Security**: Spring Security with JWT (stateless sessions, BCrypt passwords)
- **Database**: MyBatis Plus 3.5.11 + MySQL
- **Cache**: Spring Data Redis (StringRedisTemplate)
- **Logging**: Log4j2 (replaces default Logback) with API access logging
- **API Documentation**: SpringDoc OpenAPI (Swagger UI at `/swagger-ui.html`)
- **Validation**: Bean Validation (jakarta.validation) for request DTOs
- **Utilities**: Hutool 5.7.13, Fastjson 2.0.16, Dotenv 3.0.0
- **AOP**: Spring AOP for request/response logging

## Architecture

### Package Structure
```
icu.ruiyu.framework/
├── common/
│   ├── config/        # OpenApiConfig (Swagger)
│   ├── annotation/    # @Idempotent, @RecordRequestAndResponse
│   ├── aspect/        # IdempotentAspect, RecordRequestAndResponseAspect
│   └── CommonResult, ResponseEnum
├── exception/         # BusinessException, GlobalExceptionHandler
├── log/
│   └── filter/        # ApiAccessLogFilter (HTTP access logging)
└── integration/
    ├── security/      # JWT auth, WebSecurityConfig, AuthController
    ├── mysql/         # User model, UserMapper, TestController
    ├── cache/         # CacheService (Redis wrapper)
    └── OAuth2/        # GithubLoginController (GitHub OAuth2)

com.ruiyu.framework/
└── core/              # Core business logic (UserService)
```

### Component Scan
`@ComponentScan` covers both `icu.ruiyu.framework` and `com.ruiyu.framework`.
MyBatis mappers are scanned from `icu.ruiyu.framework.integration.mysql.mapper`.

### Security Architecture
- **JWT-based stateless authentication** (no sessions)
- `JwtAuthenticationTokenFilter` intercepts requests, validates JWT tokens
- `JwtAuthenticationProvider` performs authentication
- Custom `UnauthorizedResponseHandler` and `AccessDeniedResponseHandler` return JSON error responses
- Public endpoints: `/`, `/authorize`, `/oauth/redirect`, `/user/login`, `/user/register`, `/test/**`, `/swagger-ui/**`, `/v3/api-docs/**`, OPTIONS (CORS preflight)

### API Documentation
- Swagger UI: `http://localhost:8000/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8000/v3/api-docs`
- Configured via `icu.ruiyu.framework.common.config.OpenApiConfig`

### Bean Validation
- DTOs use `@NotBlank`, `@Size` annotations for field validation
- Controller methods use `@Valid` to trigger validation
- Validation errors return `CommonResult.fail()` via `GlobalExceptionHandler`

### Database
- MyBatis Plus with auto-generated CRUD
- Entity: `icu.ruiyu.framework.integration.mysql.model.User` maps to `users` table
- Uses `@TableId(type = IdType.AUTO)` for auto-increment ID

### Global Exception Handler
`@ControllerAdvice` + `@ResponseBody` returns `CommonResult<T>` for all exceptions.
Handles:
- `MissingServletRequestParameterException` (400)
- `NullPointerException` (500)
- `BusinessException` (500)
- `MethodArgumentNotValidException` (400) - Bean Validation errors
- `ConstraintViolationException` (400) - Method-level validation errors

### Application Properties
- `jwt.secret-key` - JWT signing key (must change in production)
- `spring.datasource.*` - MySQL connection (localhost:3306/springboot_demo)
- `spring.data.redis.*` - Redis connection (localhost:6379)
- `github.client.*` - GitHub OAuth2 app credentials
- `api-access-log.*` - API access log configuration (trace-id-header, max-body-length, logger-name)

## Development Guidelines

### Requirement Management
- **Complex requirements**: Use OpenSpec to manage changes (`/opsx:propose`, `/opsx:apply`, `/opsx:archive`)
- All requirements must include test cases
- **All tests must pass before delivery** — run `mvn test` to verify

### Requirement Completion Workflow
1. 需求验收通过后
2. 更新 CLAUDE.md 文档
3. 执行 `/opsx:archive` 归档 change
4. 提交代码到仓库