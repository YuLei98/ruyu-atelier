# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run Commands

```bash
# Build
mvn clean package

# 配置环境变量 (使用 .env 文件或手动导出)
# 复制 .env.example 为 .env 并填入实际值
cp .env.example .env

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
- **Utilities**: Hutool 5.7.13, Fastjson 2.0.16

## Architecture

### Package Structure
```
icu.ruiyu.framework/
├── common/          # CommonResult, ResponseEnum
├── exception/        # BusinessException, GlobalExceptionHandler
├── log/
│   └── filter/      # ApiAccessLogFilter (HTTP access logging)
└── integration/
    ├── security/     # JWT auth, WebSecurityConfig, AuthController
    ├── mysql/        # User model, UserMapper, TestController
    ├── cache/        # CacheService (Redis wrapper)
    └── OAuth2/       # GithubLoginController (GitHub OAuth2)

com.ruiyu.framework/
└── core/            # Core business logic (UserService)
```

### Component Scan
`@ComponentScan` covers both `icu.ruiyu.framework` and `com.ruiyu.framework`.
MyBatis mappers are scanned from `icu.ruiyu.framework.integration.mysql.mapper`.

### Security Architecture
- **JWT-based stateless authentication** (no sessions)
- `JwtAuthenticationTokenFilter` intercepts requests, validates JWT tokens
- `JwtAuthenticationProvider` performs authentication
- Custom `UnauthorizedResponseHandler` and `AccessDeniedResponseHandler` return JSON error responses
- Public endpoints: `/`, `/authorize`, `/oauth/redirect`, `/user/login`, `/user/register`, `/test/**`, OPTIONS (CORS preflight)

### Database
- MyBatis Plus with auto-generated CRUD
- Entity: `icu.ruiyu.framework.integration.mysql.model.User` maps to `users` table
- Uses `@TableId(type = IdType.AUTO)` for auto-increment ID

### Global Exception Handler
`@ControllerAdvice` + `@ResponseBody` returns `CommonResult<T>` for all exceptions.
Handles: `MissingServletRequestParameterException`, `NullPointerException`, `BusinessException`.

### Application Properties
敏感配置通过 `.env` 文件 + 环境变量读取，默认值为本地开发配置：
- `jwt.secret-key` - JWT signing key (`${JWT_SECRET_KEY}`)
- `spring.datasource.*` - MySQL connection (`${DATASOURCE_URL/USERNAME/PASSWORD}`)
- `spring.data.redis.*` - Redis connection (`${REDIS_PORT/PASSWORD}`)
- `github.client.*` - GitHub OAuth2 app credentials (`${GITHUB_CLIENT_ID/SECRET}`)
- `api-access-log.*` - API access log configuration (trace-id-header, max-body-length, logger-name, exclude-methods)

### Environment Configuration
- `.env.example` - 环境变量模板（推送到 GitHub）
- `.env` - 本地环境变量（已加入 `.gitignore`，不推送）
- 使用 `dotenv-spring-boot-starter` 自动加载 `.env` 文件

## Development Guidelines

### Requirement Management
- **Complex requirements**: Use OpenSpec to manage changes (`/opsx:propose`, `/opsx:apply`, `/opsx:archive`)
- All requirements must include test cases
- **All tests must pass before delivery** — run `mvn test` to verify
