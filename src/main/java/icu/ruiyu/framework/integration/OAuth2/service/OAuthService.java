package icu.ruiyu.framework.integration.OAuth2.service;

import icu.ruiyu.framework.integration.OAuth2.model.OAuthUser;

/**
 * OAuth 服务抽象接口
 */
public interface OAuthService {

    /**
     * 获取授权 URL
     *
     * @return 授权跳转 URL
     */
    String getAuthorizationUrl();

    /**
     * 使用授权码获取用户信息
     *
     * @param code 授权码
     * @return OAuth 用户信息
     */
    OAuthUser getUserInfo(String code) throws Exception;
}
