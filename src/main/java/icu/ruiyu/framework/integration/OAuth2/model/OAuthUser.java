package icu.ruiyu.framework.integration.OAuth2.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * 通用 OAuth 用户模型
 */
@Data
public class OAuthUser {
    /**
     * 第三方平台用户 ID
     */
    private String openId;

    /**
     * 用户名/昵称
     */
    private String username;

    /**
     * 头像 URL
     */
    private String avatarUrl;

    /**
     * 邮箱（如果有）
     */
    private String email;

    /**
     * OAuth 提供商类型
     */
    private String provider;

    /**
     * 原始返回的完整数据（JSON 字符串）
     * 用于保存不同平台特有的字段，便于后续扩展业务场景
     */
    @JsonIgnore
    private String rawData;
}
