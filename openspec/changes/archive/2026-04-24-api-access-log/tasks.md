## 1. ApiAccessLogFilter 实现

- [x] 1.1 创建 `log/filter/ApiAccessLogFilter.java`，继承 OncePerRequestFilter
- [x] 1.2 从 Header 获取/生成 traceId，设置到 ThreadLocal
- [x] 1.3 从 SecurityContextHolder 获取 userId
- [x] 1.4 记录 request 信息（method、path、query、ip、userAgent）
- [x] 1.5 在 response 完成后记录 status、latency
- [x] 1.6 try-finally 确保 ThreadLocal 清理

## 2. Log4j2 配置

- [x] 2.1 修改 `log4j2.xml`，新增 ApiAccessLog RollingFile appender
- [x] 2.2 配置 `logs/api-access.log`，滚动策略（每天 + 10MB）

## 3. 单元测试

- [x] 3.1 编写 `ApiAccessLogFilterTest` 测试

## 4. 功能优化

- [x] 4.1 移除 userId 字段（SecurityContext 无法在 Security filter 之前获取）
- [x] 4.2 新增 requestBody/responseBody 记录，超长截断（1000字符）
- [x] 4.3 删除 body 和 query 中的换行、制表符等空白字符

## 5. 规范优化

- [x] 5.1 硬编码路径改为 `${sys:log.path:-logs}`
- [x] 5.2 魔法字符抽取为 application.properties 配置