# 邮件发送功能集成设计

## 概述

在 `icu.ruiyu.framework` 包下集成邮件发送功能，基于 Spring Boot Mail starter + SMTP 协议，支持文本邮件、HTML 邮件和模板邮件三种模式。

## 技术方案

**依赖：** Spring Boot Mail starter
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>
</dependency>
```

**配置方式：** 通过 `.env` 文件读取邮件服务参数

## 配置项

| 环境变量 | 说明 | 示例 |
|----------|------|------|
| MAIL_HOST | SMTP 服务器地址 | smtp.qq.com |
| MAIL_PORT | SMTP 端口 | 587 (TLS) 或 465 (SSL) |
| MAIL_USERNAME | 邮箱账号 | example@qq.com |
| MAIL_PASSWORD | 邮箱密码/授权码 | xxxxxxxx |
| MAIL_FROM | 发件人地址 | example@qq.com |

## 代码结构

```
icu.ruiyu.framework/
└── integration/
    └── email/
        ├── config/
        │   └── EmailProperties.java      # 配置属性类，绑定 mail.* 配置
        └── service/
            ├── EmailService.java         # 服务接口
            └── impl/
                └── SmtpEmailServiceImpl.java  # SMTP 实现
```

## 功能设计

### 1. EmailService 接口

```java
public interface EmailService {
    // 发送简单文本邮件
    void sendTextEmail(String to, String subject, String content);

    // 发送 HTML 邮件
    void sendHtmlEmail(String to, String subject, String htmlContent);

    // 发送模板邮件（占位符替换）
    void sendTemplateEmail(String to, String subject, String templatePath, Map<String, String> placeholders);

    // 发送附件邮件
    void sendEmailWithAttachment(String to, String subject, String content, String attachmentPath);
}
```

### 2. 业务场景

| 场景 | 方法 | 说明 |
|------|------|------|
| 验证码发送 | sendTextEmail | 注册/重置密码 |
| 超支预警 | sendHtmlEmail | 配合预算模块 |
| 定期报告 | sendTemplateEmail | LLM 生成摘要 |

### 3. 模板支持

使用 Thymeleaf 模板引擎，支持动态内容替换：

```html
<!-- templates/email/notification.html -->
<p>您好，{{username}}</p>
<p>{{message}}</p>
```

## 配置示例

**application.yml:**
```yaml
spring.mail:
  host: ${MAIL_HOST}
  port: ${MAIL_PORT}
  username: ${MAIL_USERNAME}
  password: ${MAIL_PASSWORD}
  properties:
    mail.smtp.auth: true
    mail.smtp.starttls.enable: true
```

**.env:**
```bash
MAIL_HOST=smtp.qq.com
MAIL_PORT=587
MAIL_USERNAME=your-email@qq.com
MAIL_PASSWORD=your-authorization-code
MAIL_FROM=your-email@qq.com
```

## 错误处理

- 发送失败记录日志 `email` logger
- 业务异常抛出 `BusinessException`
- 不影响主业务流程（发送失败仅记录，不阻断）

## 安全考虑

- 邮箱密码使用授权码而非登录密码
- 配置信息通过环境变量读取，不硬编码
- 日志中脱敏处理邮箱地址
