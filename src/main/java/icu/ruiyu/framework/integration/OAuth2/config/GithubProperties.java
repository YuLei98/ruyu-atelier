package icu.ruiyu.framework.integration.OAuth2.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * GitHub OAuth 配置
 */
@Component
@ConfigurationProperties(prefix = "github.client")
@Data
public class GithubProperties {
    /**
     * OAuth 提供商名称
     */
    private String provider = "github";

    private String clientId;
    private String clientSecret;
    private String authorizeUrl;
    private String redirectUrl;
    private String accessTokenUrl;
    private String userInfoUrl;

    // 请求体参数
    private String grantType = "authorization_code";

    // 响应 JSON 字段映射（平台特有字段）
    private String userIdField = "id";
    private String usernameField = "login";
    private String avatarUrlField = "avatar_url";
    private String emailField = "email";
}
