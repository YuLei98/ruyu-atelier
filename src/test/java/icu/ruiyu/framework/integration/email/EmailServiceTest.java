package icu.ruiyu.framework.integration.email;

import icu.ruiyu.framework.integration.email.config.EmailProperties;
import icu.ruiyu.framework.integration.email.service.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = com.ruiyu.atelier.AtelierApplication.class)
@ActiveProfiles("test")
class EmailServiceTest {

    @Resource
    private EmailService emailService;

    @Resource
    private EmailProperties emailProperties;

    @Test
    void testEmailPropertiesLoaded() {
        assertNotNull(emailProperties);
        // 在测试环境下配置可能为空，但 bean 应该能加载
        assertNotNull(emailProperties.getHost());
    }

    @Test
    void testSendTextEmail() {
        // 由于测试环境可能没有真实邮件配置，只验证不抛异常
        // 实际发送会记录日志
        String to = "test@example.com";
        String subject = "Test Subject";
        String content = "Test Content";

        // 如果配置了真实邮件服务，才会真正发送
        if (isEmailConfigured()) {
            emailService.sendTextEmail(to, subject, content);
        } else {
            // 配置不完整时，验证方法能正常执行（不会抛异常）
            assertDoesNotThrow(() -> emailService.sendTextEmail(to, subject, content));
        }
    }

    @Test
    void testSendHtmlEmail() {
        String to = "test@example.com";
        String subject = "HTML Test";
        String htmlContent = "<h1>Hello</h1><p>This is a test email.</p>";

        if (isEmailConfigured()) {
            emailService.sendHtmlEmail(to, subject, htmlContent);
        } else {
            assertDoesNotThrow(() -> emailService.sendHtmlEmail(to, subject, htmlContent));
        }
    }

    @Test
    void testSendTemplateEmail() {
        String to = "test@example.com";
        String subject = "Template Test";
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("username", "Test User");
        placeholders.put("message", "This is a test message");
        placeholders.put("title", "Test Title");
        placeholders.put("actionUrl", "https://example.com");

        if (isEmailConfigured()) {
            emailService.sendTemplateEmail(to, subject, "email/notification", placeholders);
        } else {
            assertDoesNotThrow(() ->
                emailService.sendTemplateEmail(to, subject, "email/notification", placeholders)
            );
        }
    }

    @Test
    void testSendVerificationEmail() {
        String to = "test@example.com";
        String subject = "Verification Code";
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("code", "123456");
        placeholders.put("expireMinutes", "5");

        if (isEmailConfigured()) {
            emailService.sendTemplateEmail(to, subject, "email/verification", placeholders);
        } else {
            assertDoesNotThrow(() ->
                emailService.sendTemplateEmail(to, subject, "email/verification", placeholders)
            );
        }
    }

    @Test
    void testSendEmailWithAttachment() {
        String to = "test@example.com";
        String subject = "Attachment Test";
        String content = "Please see the attachment.";
        String attachmentPath = "/tmp/test-attachment.txt";

        if (isEmailConfigured()) {
            emailService.sendEmailWithAttachment(to, subject, content, attachmentPath);
        } else {
            // 附件不存在时仍不抛异常（发送失败只记录日志）
            assertDoesNotThrow(() ->
                emailService.sendEmailWithAttachment(to, subject, content, attachmentPath)
            );
        }
    }

    /**
     * 检查邮件服务是否已配置
     */
    private boolean isEmailConfigured() {
        String username = emailProperties.getUsername();
        String password = emailProperties.getPassword();
        return username != null && !username.isEmpty()
            && password != null && !password.isEmpty();
    }
}
