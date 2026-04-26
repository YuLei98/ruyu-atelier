# ruiyu-atelier

个人开发工作室仓库，承载多个业务项目。

## 项目结构

```
ruiyu-atelier/
├── src/main/java/com/ruiyu/atelier/          # 主应用入口
├── src/main/java/icu/ruiyu/framework/         # 框架核心模块
├── src/main/java/com/ruiyu/outdoor/          # 户外活动业务模块
├── ios/OutdoorApp/                           # iOS 应用 (山野记)
└── ios/prototype.html                        # App 原型图
```

## 技术栈

- **后端**: Spring Boot 3.2.10, Java 17, MyBatis Plus, MySQL, Redis
- **安全**: Spring Security + JWT
- **iOS**: UIKit, MapKit, CoreLocation
- **文档**: SpringDoc OpenAPI (Swagger UI)

## 快速开始

### 后端服务

```bash
# 克隆项目
git clone https://github.com/YuLei98/ruyu-atelier.git
cd ruiyu-atelier

# 配置环境变量
cp .env.example .env

# 构建
mvn clean package

# 运行
mvn spring-boot:run
```

服务地址: http://localhost:8000

### iOS 应用

```bash
cd ios/OutdoorApp
xcodegen generate
open OutdoorApp.xcworkspace
```

## 业务模块

### Outdoor 户外活动

活动记录应用，支持：

- 活动管理（徒步、露营、登山、骑行）
- 轨迹记录和展示
- 装备管理
- 队友管理

API 文档: http://localhost:8000/swagger-ui.html

## 许可证

本项目采用 MIT 许可证，详见 [LICENSE](LICENSE) 文件。
