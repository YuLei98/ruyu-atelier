## Why

容器化部署（Docker Compose/Kubernetes）需要健康检查探针来判断应用是否存活、是否可接收流量。当前项目缺少标准化的健康检查端点，无法接入容器编排平台，也不具备基础的运行状态监控能力。

## What Changes

- 引入 `spring-boot-starter-actuator` + `micrometer-registry-prometheus`
- 暴露 `/actuator/health` 主端点，支持 K8s liveness/readiness probes（`/actuator/health/liveness`、`/actuator/health/readiness`）
- 暴露 `/actuator/prometheus` 用于 Prometheus 指标抓取
- 将 `/actuator/**` 加入 Security 白名单，允许匿名访问
- Spring Boot 自动提供 Redis、MySQL(Druid) 的健康检查器

## Capabilities

### New Capabilities

- `actuator-health-check`: 提供应用健康状态、Redis 连接、数据库连接的可观测性端点，支持 K8s probes 和 Prometheus 抓取

### Modified Capabilities

-（无现有能力修改）

## Impact

- **新增依赖**：`spring-boot-starter-actuator`、`micrometer-registry-prometheus`
- **安全配置**：`WebSecurityConfig` 需将 `/actuator/**` 加入白名单
- **运维配置**：K8s Deployment 或 Docker Compose 可配置健康检查探针指向 `/actuator/health/liveness` 和 `/actuator/health/readiness`
