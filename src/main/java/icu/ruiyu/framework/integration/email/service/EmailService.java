package icu.ruiyu.framework.integration.email.service;

import java.util.Map;

/**
 * 邮件服务接口
 */
public interface EmailService {

    /**
     * 发送简单文本邮件
     * @param to 收件人邮箱
     * @param subject 邮件主题
     * @param content 邮件内容
     */
    void sendTextEmail(String to, String subject, String content);

    /**
     * 发送 HTML 邮件
     * @param to 收件人邮箱
     * @param subject 邮件主题
     * @param htmlContent HTML 格式的邮件内容
     */
    void sendHtmlEmail(String to, String subject, String htmlContent);

    /**
     * 发送模板邮件（支持占位符替换）
     * @param to 收件人邮箱
     * @param subject 邮件主题
     * @param templatePath 模板路径（如 "email/notification"）
     * @param placeholders 占位符替换键值对
     */
    void sendTemplateEmail(String to, String subject, String templatePath, Map<String, String> placeholders);

    /**
     * 发送带附件的邮件
     * @param to 收件人邮箱
     * @param subject 邮件主题
     * @param content 邮件内容
     * @param attachmentPath 附件文件路径
     */
    void sendEmailWithAttachment(String to, String subject, String content, String attachmentPath);
}
