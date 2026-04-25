package icu.ruiyu.framework.integration.OAuth2.controller;

import icu.ruiyu.framework.exception.OAuthException;
import icu.ruiyu.framework.integration.OAuth2.model.OAuthUser;
import icu.ruiyu.framework.integration.OAuth2.service.OAuthService;
import lombok.extern.slf4j.Slf4j;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;

/**
 * OAuth 统一控制器
 */
@Slf4j
@RestController
@RequestMapping("/oauth")
public class OAuthController {

    @Resource
    private Map<String, OAuthService> oauthServiceMap;

    /**
     * 授权重定向
     *
     * @param provider OAuth 提供商类型（默认 github）
     * @return 重定向到对应平台的授权页面
     */
    @GetMapping("/authorize")
    public RedirectView authorize(@RequestParam(defaultValue = "github") String provider) {
        OAuthService oauthService = getOAuthService(provider);
        String url = oauthService.getAuthorizationUrl();
        log.info("Redirect to {} authorization URL", provider);
        return new RedirectView(url);
    }

    /**
     * 授权回调
     *
     * @param code  授权码
     * @param provider OAuth 提供商类型
     * @return 用户信息
     */
    @GetMapping("/redirect")
    public ResponseEntity<OAuthUser> redirect(
            @RequestParam("code") String code,
            @RequestParam(defaultValue = "github") String provider) throws OAuthException {
        log.info("Received OAuth callback from provider: {}", provider);
        OAuthService oauthService = getOAuthService(provider);
        OAuthUser oauthUser = oauthService.getUserInfo(code);
        return ResponseEntity.ok(oauthUser);
    }

    private OAuthService getOAuthService(String provider) {
        OAuthService oauthService = oauthServiceMap.get(provider.toLowerCase() + "OAuthService");
        if (oauthService == null) {
            throw new IllegalArgumentException("Unsupported OAuth provider: " + provider);
        }
        return oauthService;
    }
}
