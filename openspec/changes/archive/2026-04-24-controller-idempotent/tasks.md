## 1. 基础组件实现

- [x] 1.1 创建 `@Idempotent` 注解（src/main/java/icu/ruiyu/framework/common/annotation/Idempotent.java）
- [x] 1.2 扩展 `CacheClient.setIfAbsent` 方法（Boolean setIfAbsent(String key, String value, ExpireEnum expire)）

## 2. AOP 切面实现

- [x] 2.1 创建 `IdempotentAspect` 切面类
- [x] 2.2 实现幂等 key 生成逻辑（className:methodName:argsHash）
- [x] 2.3 实现 SET NX EX 幂等检查
- [x] 2.4 处理重复提交时返回 CommonResult.error(409, message)

## 3. 业务集成

- [x] 3.1 在 `AuthController.register` 方法添加 `@Idempotent` 注解
- [x] 3.2 在 `AuthController.login` 方法添加 `@Idempotent` 注解

## 4. 测试验证

- [x] 4.1 在 `AuthControllerIdempotentTest` 中添加注册幂等测试：60秒内相同 SignUpReq 重复提交返回 409
- [x] 4.2 在 `AuthControllerIdempotentTest` 中添加登录幂等测试：60秒内相同 SignInReq 重复提交返回 409
- [x] 4.3 验证幂等 key 正确生成（格式：`idempotent:{methodName}:{argsHash}`）
- [x] 4.4 运行 `mvn test` 确保所有测试通过
