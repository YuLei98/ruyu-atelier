## Context

项目当前无健康检查能力，无法接入容器编排平台（Docker Compose/Kubernetes）。Spring Boot Actuator 是行业标准的应用可观测性解决方案，提供健康检查、指标暴露、探测端点等能力。

当前安全配置（`WebSecurityConfig.java`）已将 `/swagger-ui/**`、`/user/login`、`/user/register` 等加入白名单，`/actuator/**` 需要同样处理。

## Goals / Non-Goals

**Goals:**
- 引入 Spring Boot Actuator，提供标准化的健康检查端点
- 支持 Kubernetes liveness 和 readiness probes
- 暴露 Prometheus 格式指标，供监控平台抓取
- 健康检查包含 Redis、MySQL 连接状态
- `/actuator/**` 端点允许匿名访问（健康检查本身不应需要认证）

**Non-Goals:**
- 不实现自定义业务指标（后续可扩展）
- 不接入外部监控平台（Prometheus 端点暴露后由运维配置）
- 不修改现有 Controller 或 API 路径

## Decisions

### Decision 1: 引入 micrometer-registry-prometheus 而非仅用 actuator

**选择**：同时引入 `spring-boot-starter-actuator` + `micrometer-registry-prometheus`

**理由**：
- actuator 提供健康检查端点，`micrometer-registry-prometheus` 提供 Prometheus 格式指标
- 两者配合是 Spring Boot 3.x 官方推荐的可观测性方案
- actuator 自身已包含基础指标（JVM、HTTP、数据库连接池），无需额外开发

**备选**：
- 仅用 actuator - 缺少 Prometheus 格式输出，监控平台难以集成
- 使用 Micrometer Tracing (OpenTelemetry) - 链路追踪能力，本次范围外

### Decision 2: 使用默认 HealthIndicators，不自定义

**选择**：使用 Spring Boot 自动配置的 Redis 和 DataSource 健康检查器

**理由**：
- Spring Boot 自动检测 `StringRedisTemplate` 和 `DataSource` 并创建对应的 `HealthIndicator`
- Druid 连接池自带健康检查，开箱即用
- 自定义 `RedisHealthIndicator` 会覆盖默认行为，增加维护成本

### Decision 3: actuator 端点全部 permitAll

**选择**：`/actuator/**` 全部允许匿名访问

**理由**：
- Kubernetes probes 从集群内部访问，无认证上下文
- `/actuator/health` 只暴露组件状态，不含业务数据
- `/actuator/prometheus` 指标数据为运维数据，非敏感信息
- `show-details: when_authorized` 确保详细信息仅对认证用户展示，防止内部组件信息泄露

**潜在风险** → 已通过 `show-details: when_authorized` 缓解。

### Decision 4: 在 application.yml 而非 Java Config 中配置

**选择**： actuator 配置放在 `application.yml` 的 `management` 节

**理由**：
- 符合项目现有配置风格（大量使用 `management.*`、`spring.*` 等 YAML 配置）
- 与 `ratelimit.*`、`api-access-log.*` 等自定义配置的风格统一
- 代码改动量最少

## Risks / Trade-offs

[Risk] actuator 端点暴露内部组件信息（如 Redis 版本、数据库类型）
→ [Mitigation] `show-details: when_authorized` 确保详细信息仅对认证用户可见

[Risk] `/actuator/prometheus` 指标数据可能被用于系统指纹识别
→ [Mitigation] 生产环境可配合反向代理（如 Nginx）限制访问来源

[Risk] K8s probes 配置不当导致应用被频繁重启
→ [Mitigation] 默认 `liveness` 和 `readiness` 检查延迟启动（grace period），避免启动过程中误判

## Migration Plan

**实施步骤：**
1. 在 `pom.xml` 添加 actuator + micrometer-prometheus 依赖
2. 在 `application.yml` 添加 `management` 配置段
3. 在 `WebSecurityConfig.java` 将 `/actuator/**` 加入白名单
4. 本地启动验证：`curl http://localhost:8000/actuator/health`
5. 验证 K8s probes：`curl http://localhost:8000/actuator/health/liveness`
6. 提交代码

**回滚方案**：移除 pom.xml 依赖、application.yml 配置、WebSecurityConfig 中的 `/actuator/**` 白名单即可。
