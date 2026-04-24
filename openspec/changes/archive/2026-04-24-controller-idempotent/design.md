## Context

当前项目使用 Spring Boot + Spring AOP + Redis，Controller 层没有幂等性保护。客户端重复提交会导致业务被重复执行（如重复注册、重复登录处理）。

## Goals / Non-Goals

**Goals:**
- 在 Controller 方法层面添加幂等性保护
- 基于 Redis 实现分布式环境下的幂等检查
- 通过 AOP 切面实现，对业务代码无侵入

**Non-Goals:**
- 不处理消息队列场景的幂等（那是消费端的事）
- 不提供 API 级别的幂等 Token（那是接口设计的事）

## Decisions

**1. 使用 AOP + Redis 实现幂等性检查**

方案选择：拦截器 vs AOP 切面
- 拦截器：需要手动在每个方法中调用幂等检查逻辑
- AOP 切面：只需在方法上加注解，侵入性更低

最终选择：AOP 切面 + `@Idempotent` 注解

**2. 幂等 Key 生成策略**

```
idempotent:{className}:{methodName}:{argsHash}
```

- 前缀可通过注解配置，默认使用方法名
- 参数哈希解决同接口不同参数的区分

**3. 幂等检查语义：SET NX EX**

```
SET key value NX EX 60
```

- NX：key 不存在才设置（保证原子性）
- EX 60：60 秒过期，防止锁泄漏

**4. CacheClient 扩展**

添加 `setIfAbsent` 方法：
```java
Boolean setIfAbsent(String key, String value, ExpireEnum expire);
```

## Risks / Trade-offs

- [Risk] Redis 不可用时会导致请求失败
  →  Mitigation：幂等检查失败时不应阻塞请求，需设计降级策略（暂不实现，依赖 Redis 高可用）
- [Trade-off] 60 秒默认过期时间可能不适合所有场景
  → 可通过 `@Idempotent(expireSeconds = ...)` 自定义
