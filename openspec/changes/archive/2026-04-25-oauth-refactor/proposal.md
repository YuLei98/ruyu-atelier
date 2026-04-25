# OAuth 重构归档

## 变更概述
- **变更名称**: oauth-refactor
- **归档日期**: 2026-04-25
- **状态**: 已完成验收

## 完成内容
1. 重构 OAuth 模块为可扩展的策略模式架构
2. 支持多 Provider（GitHub/微信/抖音等）
3. 提取通用配置 `OAuthProperties`
4. 将硬编码字段改为配置注入
5. 添加 `rawData` 保存原始数据
6. 添加单元测试验证（6 tests, 0 failures）

## 最终包结构
```
OAuth2/
├── config/
│   ├── OAuthProperties.java        # 通用配置（平台无关）
│   └── GithubProperties.java      # GitHub 特有配置
├── controller/
│   └── OAuthController.java       # 统一入口，provider 路由
├── service/
│   ├── OAuthService.java          # 抽象接口
│   └── impl/
│       └── GithubOAuthService.java
└── model/
    └── OAuthUser.java             # 通用用户模型
```

## 扩展方式
新增 Provider（微信/抖音）：
1. 新建 `WechatProperties`（配置特有字段映射）
2. 新建 `WechatOAuthService implements OAuthService`
3. 在 `OAuthController.getOAuthService()` 注册
4. 无需改业务代码，只改配置
