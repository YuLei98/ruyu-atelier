# ruiyu-atelier

个人开发工作室仓库，承载多个业务项目。

## 项目结构

```
ruiyu-atelier/
├── src/main/java/com/ruiyu/framework/          # 主应用入口
├── src/main/java/icu/ruiyu/framework/           # 框架核心模块
├── ios/                                        # iOS 应用
└── ...
```

## 技术栈

- **后端**: Spring Boot 3.2.10, Java 17, MyBatis Plus, MySQL, Redis
- **安全**: Spring Security + JWT
- **文档**: SpringDoc OpenAPI (Swagger UI)

## 快速开始

```bash
# 配置环境变量
cp .env.example .env

# 构建
mvn clean package

# 运行
mvn spring-boot:run
```

服务地址: http://localhost:8000

## 许可证

本项目采用 MIT 许可证，详见 [LICENSE](LICENSE) 文件。
