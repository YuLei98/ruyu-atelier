## Context

需要在 Spring Boot 应用中添加全局 HTTP 请求日志记录，记录到 `logs/api-access.log`。日志需包含 traceId 串联请求、userId 关联用户、分隔符格式便于查询。

## Goals / Non-Goals

**Goals:**
- 拦截所有 HTTP 请求（通过 Filter）
- 记录完整请求信息（method、path、query、userId、ip、userAgent、status、latency）
- 分隔符格式写入单独日志文件

**Non-Goals:**
- 不做敏感字段脱敏
- 不接入 ELK/Splunk 等日志平台
- 不记录请求 body 内容（只记录大小）

## Decisions

### 1. 使用 Filter 而非 Interceptor

`OncePerRequestFilter` 可拦截所有请求，包括 Spring Security 处理之前的部分。`HandlerInterceptor` 只能拦截 Spring MVC 路由。

### 2. traceId 处理

- 从 Header `X-Trace-Id` 获取（已存在则透传）
- 无则生成 UUID，在 Filter 链开头设置到 `ThreadLocal`，响应后清理

### 3. userId 获取

从 `SecurityContextHolder.getContext().getAuthentication()` 获取当前用户名，未登录则为 `null`。

### 4. 分隔符格式

```
timestamp|traceId|method|path|query|userId|ip|userAgent|status|latency|bodySize
2026-04-25T01:00:00.123Z|abc-123|POST|/user/register|null|192.168.1.100|Postman/...|200|45|123
```

分隔符 `|` 兼容 Log4j2 PatternLayout。

### 5. Log4j2 集成

不直接写文件，通过 Log4j2 的 Logger 写入，由 RollingFile 管理滚动和压缩。

## Risks / Trade-offs

- [风险] 高并发下 ThreadLocal 清理不及时 → [缓解] 使用 `try-finally` 确保清理
- [风险] 日志量大导致磁盘占满 → [缓解] Log4j2 滚动策略 + max size 控制