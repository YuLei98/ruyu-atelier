## Why

Log4j2 报错 "Unable to locate appender 'druidSqlRollingFile' for logger config 'druid.sql.Statement'"。`log4j2.xml` 中定义了 `druid.sql.Statement` 的 logger 引用了 `druidSqlRollingFile` appender，但该 appender 已被注释掉，导致启动时 Log4j2 无法找到对应的 appender。

## What Changes

- 移除 `druid.sql.Statement` logger 配置（引用了不存在的 `druidSqlRollingFile` appender）
- 保持 `druidSqlRollingFile` appender 定义为注释状态（原本意图是禁用 Druid SQL 日志）

## Capabilities

### New Capabilities
- `druid-sql-logging-disable`: 禁用 Druid SQL 语句的独立日志输出

### Modified Capabilities
- 无

## Impact

- 仅修改 `src/main/resources/log4j2.xml` 配置文件
- 无 API、代码逻辑或依赖变更
