package icu.ruiyu.framework.integration.OAuth2.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import icu.ruiyu.framework.exception.OAuthException;
import icu.ruiyu.framework.integration.OAuth2.config.GithubProperties;
import icu.ruiyu.framework.integration.OAuth2.config.OAuthProperties;
import icu.ruiyu.framework.integration.OAuth2.model.OAuthUser;
import icu.ruiyu.framework.integration.OAuth2.service.OAuthService;
import icu.ruiyu.framework.integration.restclient.RestClient;
import lombok.extern.slf4j.Slf4j;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * GitHub OAuth 服务实现
 */
@Slf4j
@Service
public class GithubOAuthService implements OAuthService {

    @Resource
    private GithubProperties githubProperties;

    @Resource
    private OAuthProperties oauthProperties;

    @Resource
    private RestClient restClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String getAuthorizationUrl() {
        try {
            return githubProperties.getAuthorizeUrl() +
                    "?client_id=" + githubProperties.getClientId() +
                    "&redirect_uri=" + URLEncoder.encode(githubProperties.getRedirectUrl(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("Failed to encode redirect_uri", e);
            throw new OAuthException("Failed to encode redirect_uri", e);
        }
    }

    @Override
    public OAuthUser getUserInfo(String code) throws OAuthException {
        String accessToken = getAccessToken(code);
        return fetchUserInfo(accessToken);
    }

    private String getAccessToken(String code) throws OAuthException {
        String url = githubProperties.getAccessTokenUrl() +
                "?client_id=" + githubProperties.getClientId() +
                "&client_secret=" + githubProperties.getClientSecret() +
                "&code=" + code +
                "&grant_type=" + githubProperties.getGrantType();

        Map<String, String> headers = new HashMap<>();
        headers.put("accept", oauthProperties.getAcceptMediaType());

        String responseStr = restClient.post(url, "", headers);
        log.debug("Access token response: {}", responseStr);

        JsonNode jsonNode;
        try {
            jsonNode = objectMapper.readTree(responseStr);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new OAuthException("Failed to parse access token response", e);
        }
        if (jsonNode.has(oauthProperties.getErrorField())) {
            String error = jsonNode.get(oauthProperties.getErrorField()).asText();
            String errorDescription = jsonNode.has(oauthProperties.getErrorDescriptionField())
                ? jsonNode.get(oauthProperties.getErrorDescriptionField()).asText()
                : "Unknown error";
            log.error("GitHub OAuth error: {} - {}", error, errorDescription);
            throw new OAuthException("GitHub OAuth error: " + error + " - " + errorDescription);
        }

        return jsonNode.get(oauthProperties.getAccessTokenField()).asText();
    }

    private OAuthUser fetchUserInfo(String accessToken) {
        String url = githubProperties.getUserInfoUrl();

        Map<String, String> headers = new HashMap<>();
        headers.put("accept", oauthProperties.getAcceptMediaType());
        headers.put("Authorization", oauthProperties.getAuthorizationScheme() + " " + accessToken);

        String userInfoStr = restClient.get(url, headers);
        log.debug("User info response: {}", userInfoStr);

        OAuthUser oauthUser = new OAuthUser();
        oauthUser.setProvider(githubProperties.getProvider());

        try {
            JsonNode jsonNode = objectMapper.readTree(userInfoStr);
            oauthUser.setOpenId(getTextOrNull(jsonNode, githubProperties.getUserIdField()));
            oauthUser.setUsername(getTextOrNull(jsonNode, githubProperties.getUsernameField()));
            oauthUser.setAvatarUrl(getTextOrNull(jsonNode, githubProperties.getAvatarUrlField()));
            oauthUser.setEmail(getTextOrNull(jsonNode, githubProperties.getEmailField()));
            // 保存原始数据，便于后续扩展业务场景
            oauthUser.setRawData(userInfoStr);
        } catch (Exception e) {
            log.error("Failed to parse GitHub user info", e);
            throw new OAuthException("Failed to parse GitHub user info", e);
        }

        log.info("Successfully retrieved GitHub user: {}", oauthUser.getUsername());
        return oauthUser;
    }

    private String getTextOrNull(JsonNode node, String field) {
        return node.has(field) && !node.get(field).isNull() ? node.get(field).asText() : null;
    }
}
