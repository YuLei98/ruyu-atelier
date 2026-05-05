package icu.ruiyu.framework.integration.email.service.impl;

import icu.ruiyu.framework.integration.email.config.EmailProperties;
import icu.ruiyu.framework.integration.email.service.EmailService;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.util.Map;

/**
 * SMTP 邮件服务实现
 */
@Service
public class SmtpEmailServiceImpl implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(SmtpEmailServiceImpl.class);

    @Resource
    private JavaMailSender mailSender;

    @Resource
    private TemplateEngine templateEngine;

    @Resource
    private EmailProperties emailProperties;

    @Override
    public void sendTextEmail(String to, String subject, String content) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(emailProperties.getFrom());
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);
            mailSender.send(message);
            log.info("文本邮件发送成功: to={}, subject={}", maskEmail(to), subject);
        } catch (Exception e) {
            log.error("文本邮件发送失败: to={}, subject={}, error={}", maskEmail(to), subject, e.getMessage());
        }
    }

    @Override
    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            var mimeMessage = mailSender.createMimeMessage();
            var helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom(emailProperties.getFrom());
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            mailSender.send(mimeMessage);
            log.info("HTML邮件发送成功: to={}, subject={}", maskEmail(to), subject);
        } catch (Exception e) {
            log.error("HTML邮件发送失败: to={}, subject={}, error={}", maskEmail(to), subject, e.getMessage());
        }
    }

    @Override
    public void sendTemplateEmail(String to, String subject, String templatePath, Map<String, String> placeholders) {
        try {
            Context context = new Context();
            if (placeholders != null) {
                placeholders.forEach(context::setVariable);
            }
            String htmlContent = templateEngine.process(templatePath, context);
            sendHtmlEmail(to, subject, htmlContent);
            log.info("模板邮件发送成功: to={}, template={}", maskEmail(to), templatePath);
        } catch (Exception e) {
            log.error("模板邮件发送失败: to={}, template={}, error={}", maskEmail(to), templatePath, e.getMessage());
        }
    }

    @Override
    public void sendEmailWithAttachment(String to, String subject, String content, String attachmentPath) {
        try {
            var mimeMessage = mailSender.createMimeMessage();
            var helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom(emailProperties.getFrom());
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content);
            var attachment = new FileSystemResource(new File(attachmentPath));
            helper.addAttachment(attachment.getFilename(), attachment);
            mailSender.send(mimeMessage);
            log.info("附件邮件发送成功: to={}, attachment={}", maskEmail(to), attachmentPath);
        } catch (Exception e) {
            log.error("附件邮件发送失败: to={}, attachment={}, error={}", maskEmail(to), attachmentPath, e.getMessage());
        }
    }

    /**
     * 邮箱地址脱敏
     */
    private String maskEmail(String email) {
        if (email == null || email.length() < 4) {
            return "***";
        }
        int atIndex = email.indexOf('@');
        if (atIndex > 0) {
            String prefix = email.substring(0, atIndex);
            String suffix = email.substring(atIndex);
            if (prefix.length() <= 2) {
                return prefix.charAt(0) + "***" + suffix;
            }
            return prefix.charAt(0) + "***" + prefix.charAt(prefix.length() - 1) + suffix;
        }
        return email.substring(0, 2) + "***";
    }
}
