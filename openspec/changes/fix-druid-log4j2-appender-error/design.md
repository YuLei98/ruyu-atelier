## Context

`log4j2.xml` 中第 97-99 行定义了 `druid.sql.Statement` 的 logger，引用了 `druidSqlRollingFile` appender。但 `druidSqlRollingFile` appender（第 72-78 行）已被注释掉，导致 Log4j2 初始化时报错。

## Goals / Non-Goals

**Goals:**
- 消除 Log4j2 启动时的 "Unable to locate appender" 错误

**Non-Goals:**
- 不重新启用 Druid SQL 日志记录（原本意图是禁用）
- 不修改 Druid 监控功能

## Decisions

### Decision 1: 移除 druid.sql.Statement logger

**Choice:** 注释掉第 96-99 行的 Druid logger 配置

**Rationale:**
- `druidSqlRollingFile` appender 已被注释掉，说明原本就打算禁用 Druid SQL 日志
- 直接移除 logger 配置即可，无需取消注释 appender
- 相比修改 appender 引用，移除 logger 更干净，避免将来再次引用不存在的 appender

**Alternatives considered:**
1. 取消注释 `druidSqlRollingFile` appender → 不采用，因为不想启用 Druid SQL 日志
2. 将 `druidSqlRollingFile` 引用改为现有 appender（如 `RollingFileInfo`）→ 不采用，只是掩盖问题而非解决根本原因

## Risks / Trade-offs

- **风险:** 无明显风险，这是一个只读的配置修改
- **权衡:** 失去 Druid SQL 日志记录，但这是原本的设计意图
