package icu.ruiyu.framework.integration.email.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 邮件服务配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "spring.mail")
public class EmailProperties {

    private String host;
    private int port;
    private String username;
    private String password;
    private String from;
}
