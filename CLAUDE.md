# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview
**ruiyu-atelier** 是一个个人开发工作室仓库，承载多个业务项目。
- 作为脚手架提供通用能力（安全、缓存、日志、限流等）
- 作为单体业务库，集成多个业务模块（如 outdoor 户外活动）

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
java -jar target/ruiyu-atelier-0.0.1-SNAPSHOT.jar
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
    ├── restclient/   # RestClient (HTTP 请求封装，支持复用)
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

com.ruiyu.outdoor/       # 户外活动业务
├── model/          # 实体类 (Activity, TrackPoint, Equipment, Partner 等)
├── mapper/         # MyBatis Plus Mapper 接口
├── dto/            # 数据传输对象 (CreateReq, UpdateReq)
├── service/        # 业务服务接口
│   └── impl/       # 服务实现
├── controller/     # REST API 控制器
└── sql/            # 数据库初始化脚本

com.ruiyu.atelier/
└── core/            # Transaction test code
```

### Business Package Database
每个业务包使用独立的数据库：
- `com.ruiyu.outdoor` → 业务库 `outdoor`
- SQL 脚本位于各业务包的 `sql/` 目录下
- 连接配置通过 `spring.datasource.*` 环境变量指定

### Outdoor 业务模块
户外活动记录应用后端：

**API 接口**：
- `POST /api/activities` - 创建活动
- `GET /api/activities` - 获取活动列表
- `GET /api/activities/{id}` - 获取活动详情
- `PUT /api/activities/{id}` - 更新活动
- `DELETE /api/activities/{id}` - 删除活动
- `POST /api/activities/{id}/favorite` - 切换收藏状态
- `POST /api/activities/{id}/track` - 添加轨迹点
- `GET /api/equipments` - 获取装备列表
- `POST /api/equipments` - 创建装备
- `GET /api/partners` - 获取队友列表
- `POST /api/partners` - 创建队友

**相关文档**：
- SQL 脚本：`src/main/java/com/ruiyu/outdoor/sql/outdoor.sql`
- iOS App：`ios/OutdoorApp/`

### iOS App (OutdoorApp)
基于 SwiftUI/UIKit 的户外活动记录 iOS 应用。

**项目结构**：
```
ios/OutdoorApp/
├── Sources/
│   ├── App/
│   │   ├── AppDelegate.swift
│   │   └── SceneDelegate.swift
│   ├── Models/
│   │   ├── Activity.swift
│   │   ├── TrackPoint.swift
│   │   ├── Equipment.swift
│   │   └── Partner.swift
│   ├── Services/
│   │   └── APIService.swift
│   └── ViewControllers/
│       ├── MainTabBarController.swift
│       ├── HomeViewController.swift
│       ├── ActivityCell.swift
│       ├── ActivityDetailViewController.swift
│       ├── CreateActivityViewController.swift
│       ├── TrackViewController.swift
│       ├── EquipmentViewController.swift
│       └── ProfileViewController.swift
└── Resources/
    └── Assets.xcassets/
```

**主要功能**：
- 活动管理：创建、查看、编辑、删除户外活动
- 轨迹记录：使用 MapKit 记录和展示运动轨迹
- 装备管理：管理户外装备清单
- 队友管理：记录活动参与者
- 活动收藏：标记和管理收藏的活动

**技术栈**：
- UIKit + SnapKit (Auto Layout)
- MapKit (地图和轨迹展示)
- CoreLocation (位置服务)
- URLSession (网络请求)

**构建和运行**：
```bash
# 使用 Xcode 打开
open ios/OutdoorApp/OutdoorApp.xcodeproj

# 或使用 xcodebuild 构建
xcodebuild -project ios/OutdoorApp/OutdoorApp.xcodeproj \
  -scheme OutdoorApp \
  -configuration Debug \
  -destination 'platform=iOS Simulator,name=iPhone 16' build
```

**API 对接**：
- 基础 URL：`http://localhost:8000`
- 认证方式：JWT Bearer Token
- 需要在 `APIService.swift` 中配置正确的 baseURL

### Component Scan
`@ComponentScan` covers: `icu.ruiyu.framework`, `com.ruiyu.atelier`, `com.ruiyu.outdoor`
MyBatis mappers are scanned from: `icu.ruiyu.framework.integration.mysql.mapper`, `com.ruiyu.outdoor.mapper`

### Security Architecture
- **JWT-based stateless authentication** (no sessions)
- `JwtAuthenticationTokenFilter` intercepts requests, validates JWT tokens
- `JwtAuthenticationProvider` performs authentication
- Custom `UnauthorizedResponseHandler` and `AccessDeniedResponseHandler` return JSON error responses
- Public endpoints: `/`, `/api/v1/oauth/authorize`, `/api/v1/oauth/redirect`, `/api/v1/user/login`, `/api/v1/user/register`, `/test/**`, `/swagger-ui/**`, `/v3/api-docs/**`, OPTIONS (CORS preflight)

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
- `framework.restclient.*` - REST 客户端配置 (connectTimeout, readTimeout, maxConnections)
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

### RestClient HTTP 客户端
统一封装 HTTP 请求，支持所有外部 API 调用：

**日志记录**：`httpClient` logger 记录所有请求到 `logs/http-client.log`
- 格式：`method | url | status | duration | req:body | res:body | error`
- 示例：`POST | https://api.github.com/xxx | 200 | 245ms | req:{} | res:access_token=xxx`

**连接池**：使用 Apache HttpClient5 连接池（maxTotal=100, defaultMaxPerRoute=20）

**支持方法**：GET, POST, PUT, PATCH, DELETE

**配置项** (`application.yml`)：
```yaml
framework.restclient:
  connect-timeout: 5000
  read-timeout: 10000
```

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