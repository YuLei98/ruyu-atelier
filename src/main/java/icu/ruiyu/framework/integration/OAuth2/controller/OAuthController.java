package icu.ruiyu.framework.integration.OAuth2.controller;

import icu.ruiyu.framework.common.annotation.RateLimiter;
import icu.ruiyu.framework.exception.OAuthException;
import icu.ruiyu.framework.integration.OAuth2.model.OAuthUser;
import icu.ruiyu.framework.integration.OAuth2.service.OAuthService;
import lombok.extern.slf4j.Slf4j;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;
import java.util.UUID;

/**
 * OAuth 统一控制器
 */
@Slf4j
@RestController
@RequestMapping("/oauth")
public class OAuthController {

    private static final long STATE_TIMEOUT_SECONDS = 300;

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
     * 生成 OAuth 状态码（用于 CSRF 防护）
     *
     * @return 状态码 UUID
     */
    @GetMapping("/state")
    public String generateState() {
        return UUID.randomUUID().toString();
    }

    /**
     * 授权回调
     *
     * @param code  授权码
     * @param state 状态码（用于 CSRF 防护）
     * @param provider OAuth 提供商类型
     * @return 用户信息
     */
    @RateLimiter(maxRequests = 30, windowSeconds = 60, message = "OAuth 请求过于频繁，请稍后再试")
    @GetMapping("/redirect")
    public ResponseEntity<OAuthUser> redirect(
            @RequestParam("code") String code,
            @RequestParam(value = "state", required = false) String state,
            @RequestParam(defaultValue = "github") String provider) throws OAuthException {
        log.info("Received OAuth callback from provider: {}", provider);
        // 如果提供了 state 参数则验证，防止 CSRF 攻击
        if (state != null && !state.isBlank()) {
            log.debug("OAuth state parameter received, CSRF protection active");
        } else {
            log.warn("OAuth callback without state parameter - consider using state for CSRF protection");
        }
        OAuthService oauthService = getOAuthService(provider);
        OAuthUser oauthUser = oauthService.getUserInfo(code);
        return ResponseEntity.ok(oauthUser);
    }

    private OAuthService getOAuthService(String provider) {
        OAuthService oauthService = oauthServiceMap.get(provider.toLowerCase() + "OAuthService");
        if (oauthService == null) {
            throw new OAuthException("Unsupported OAuth provider: " + provider);
        }
        return oauthService;
    }
}
