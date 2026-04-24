## Why

项目需要记录所有 HTTP 请求日志到 `logs/api-access.log`，方便排查问题和关联用户行为。当前缺少统一的全局请求日志机制。

## What Changes

- 新增 `ApiAccessLogFilter` 拦截所有 HTTP 请求
- 使用 `|` 分隔符格式记录：timestamp、traceId、method、path、query、ip、userAgent、status、latency、requestBody、responseBody
- 从 Header 获取或生成 `X-Trace-Id`，用于串联请求链路
- 日志写入 `logs/api-access.log`，由 Log4j2 滚动管理
- requestBody/responseBody 超过 1000 字符会被截断，换行和制表符会被移除

## Capabilities

### New Capabilities
- `api-access-log`: 全局 HTTP 请求日志记录

### Modified Capabilities
- (none)

## Impact

- 新增 `integration/common/filter/ApiAccessLogFilter.java`
- Log4j2 新增 `ApiAccessLogAppender` 写入 `api-access.log`
- 无新增外部依赖