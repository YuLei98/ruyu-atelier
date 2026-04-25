package icu.ruiyu.framework.integration.OAuth2.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 通用 OAuth 配置
 */
@Component
@ConfigurationProperties(prefix = "oauth")
@Data
public class OAuthProperties {
    /**
     * 请求 accept 类型
     */
    private String acceptMediaType = "application/json";

    /**
     * Authorization 请求头格式（如 Bearer）
     */
    private String authorizationScheme = "Bearer";

    /**
     * token 字段名
     */
    private String accessTokenField = "access_token";

    /**
     * 错误字段名
     */
    private String errorField = "error";

    /**
     * 错误描述字段名
     */
    private String errorDescriptionField = "error_description";
}
