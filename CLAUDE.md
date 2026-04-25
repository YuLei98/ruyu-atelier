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
- **API Documentation**: SpringDoc OpenAPI (Swagger UI at `/swagger-ui.html`)
- **Validation**: Bean Validation (jakarta.validation) for request DTOs
- **Utilities**: Hutool 5.7.13, Fastjson 2.0.16
- **Env Config**: dotenv-java 3.0.0 (`.env` file support)
- **AOP**: Spring AOP for request/response logging
- **LLM**: LangChain4j 1.13.1 (OpenAI GPT 模型集成)

## Architecture

### Package Structure
```
icu.ruiyu.framework/
├── common/
│   ├── config/        # OpenApiConfig, DotenvConfig, RateLimiterProperties, RestTemplateConfig
│   ├── annotation/    # @Idempotent, @RecordRequestAndResponse, @RateLimiter
│   ├── aspect/        # IdempotentAspect, RecordRequestAndResponseAspect, RateLimiterAspect
│   └── CommonResult, ResponseEnum
├── exception/         # BusinessException, OAuthException, GlobalExceptionHandler
├── log/
│   ├── annotation/  # @RecordRequestAndResponse
│   ├── aspect/       # RecordRequestAndResponseAspect (AOP logging)
│   └── filter/       # ApiAccessLogFilter (HTTP access logging)
└── integration/
    ├── security/      # JWT auth, WebSecurityConfig, AuthController
    ├── mysql/        # User model, UserMapper, TestController
    ├── cache/        # CacheService (Redis wrapper)
    ├── ratelimit/    # API 限流（滑动窗口/令牌桶算法，Redis + Lua）
    │   ├── RateLimiterFilter.java       # 全局限流过滤器
    │   ├── RateLimiterService.java      # 限流服务接口
    │   └── handler/RateLimitResponseHandler.java
    ├── OAuth2/       # OAuth2 统一登录（支持多 Provider）
    │   ├── config/   # OAuthProperties (通用), GithubProperties (GitHub 专用)
    │   ├── controller/ # OAuthController (统一入口，provider 路由)
    │   ├── service/  # OAuthService 接口 + 实现
    │   └── model/    # OAuthUser (通用用户模型)
    └── llm/          # LangChain4j LLM 集成
        ├── config/   # LlmConfig (LangChain4j Bean 配置)
        ├── service/  # LlmService 接口 + OpenAiLlmService 实现
        └── controller/ # LlmController (REST API)

com.ruiyu.framework/
└── core/            # Transaction test code
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
- `RateLimitException` (429) - Rate limit exceeded
- `OAuthException` (401) - OAuth authentication errors

### Application Properties
敏感配置通过 `.env` 文件 + 环境变量读取，默认值为本地开发配置：
- `jwt.secret-key` - JWT signing key (`${JWT_SECRET_KEY}`)
- `spring.datasource.*` - MySQL connection (`${DATASOURCE_URL/USERNAME/PASSWORD}`)
- `spring.data.redis.*` - Redis connection (`${REDIS_PORT/PASSWORD}`)
- `github.client.*` - GitHub OAuth2 app credentials (`${GITHUB_CLIENT_ID/SECRET}`)
- `api-access-log.*` - API access log configuration (trace-id-header, max-body-length, logger-name, exclude-methods)
- `ratelimit.*` - Rate limiter configuration (global enabled, algorithm, window-seconds, max-requests, key-type, exclude-paths)
- `langchain4j.open-ai.*` - OpenAI API 配置 (`${OPENAI_API_KEY}`, `${OPENAI_BASE_URL}`, `${OPENAI_MODEL_NAME}`)

### Environment Configuration
- `.env.example` - 环境变量模板（推送到 GitHub）
- `.env` - 本地环境变量（已加入 `.gitignore`，不推送）
- 使用 `dotenv-spring-boot-starter` 自动加载 `.env` 文件

### API Rate Limiter
支持两种限流模式：
- **全局限流**：基于 IP 或用户 ID 的全局请求限流（`RateLimiterFilter`）
- **注解限流**：`@RateLimiter` 注解实现方法级限流

支持两种算法：
- **滑动窗口**：基于 Redis ZSET，精确控制时间窗口内的请求数
- **令牌桶**：基于 Redis Hash，支持突发流量

配置文件：`application.yml` 中 `ratelimit.*` 配置项

### OAuth2 安全特性
- **CSRF 防护**：`/oauth/redirect` 接口验证 `state` 参数防止 CSRF 攻击
- **限流保护**：OAuth 回调接口默认限流 30 次/分钟
- **统一异常处理**：`OAuthException` 异常由 `GlobalExceptionHandler` 处理

### LLM 大模型集成
基于 LangChain4j 1.13.1 的 OpenAI GPT 模型集成：

**API 接口**：
- `POST /llm/chat` - 对话（body: `{"message": "你好"}`）
- `GET /llm/models` - 获取可用模型列表

**环境变量**（`.env`）：
```bash
OPENAI_API_KEY=your-api-key
OPENAI_BASE_URL=https://api.openai.com/v1
OPENAI_MODEL_NAME=gpt-4o
```

**配置**（`application.yml`）：
```yaml
langchain4j:
  open-ai:
    api-key: ${OPENAI_API_KEY:}
    model-name: ${OPENAI_MODEL_NAME:gpt-4o}
    base-url: ${OPENAI_BASE_URL:https://api.openai.com/v1}
```

**使用示例**：
```bash
# 配置 API Key
echo "OPENAI_API_KEY=sk-xxx" >> .env

# 重启应用后调用
curl -X POST http://localhost:8000/llm/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "解释什么是微服务架构"}'
```

## Development Guidelines

### Requirement Management
- **Complex requirements**: Use OpenSpec to manage changes (`/opsx:propose`, `/opsx:apply`, `/opsx:archive`)
- All requirements must include test cases
- **All tests must pass before delivery** — run `mvn test` to verify
- 包模块组织合理
- 代码风格满足阿里 Java 研发规范

### Requirement Completion Workflow
1. 需求验收通过后
2. 更新 CLAUDE.md 文档
3. 执行 `/opsx:archive` 归档 change
4. 提交代码到仓库