## 1. 依赖配置

- [x] 1.1 在 `pom.xml` 中添加 `spring-boot-starter-actuator` 依赖
- [x] 1.2 在 `pom.xml` 中添加 `micrometer-registry-prometheus` 依赖

## 2. Actuator 配置

- [x] 2.1 在 `application.yml` 中添加 `management` 配置段（暴露 health、info、prometheus、metrics 端点）
- [x] 2.2 配置 `show-details: when_authorized` 防止敏感信息泄露
- [x] 2.3 启用 K8s probes（`probes.enabled: true`）

## 3. 安全配置

- [x] 3.1 在 `WebSecurityConfig.java` 中将 `/actuator/**` 加入白名单（`permitAll`）

## 4. 验证

- [x] 4.1 启动应用，执行 `curl http://localhost:8000/actuator/health` 验证返回 `{"status":"UP"}`
- [x] 4.2 执行 `curl http://localhost:8000/actuator/health/liveness` 验证 liveness probe
- [x] 4.3 执行 `curl http://localhost:8000/actuator/health/readiness` 验证 readiness probe
- [x] 4.4 执行 `curl http://localhost:8000/actuator/prometheus` 验证 Prometheus 格式指标
- [x] 4.5 确认未登录情况下可访问 `/actuator/health`（不返回 401/403）
