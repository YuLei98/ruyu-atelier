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
- **Logging**: Log4j2 (replaces default Logback)
- **Utilities**: Hutool 5.7.13, Fastjson 2.0.16
- **Logging**: Log4j2 (replaces default Logback) with API access logging

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
