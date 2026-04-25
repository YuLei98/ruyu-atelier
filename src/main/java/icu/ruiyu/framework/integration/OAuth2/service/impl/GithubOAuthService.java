package icu.ruiyu.framework.integration.OAuth2.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import icu.ruiyu.framework.integration.OAuth2.config.GithubProperties;
import icu.ruiyu.framework.integration.OAuth2.config.OAuthProperties;
import icu.ruiyu.framework.integration.OAuth2.model.OAuthUser;
import icu.ruiyu.framework.integration.OAuth2.service.OAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * GitHub OAuth 服务实现
 */
@Slf4j
@Service
public class GithubOAuthService implements OAuthService {

    @Autowired
    private GithubProperties githubProperties;

    @Autowired
    private OAuthProperties oauthProperties;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String getAuthorizationUrl() {
        return githubProperties.getAuthorizeUrl() +
                "?client_id=" + githubProperties.getClientId() +
                "&redirect_uri=" + githubProperties.getRedirectUrl();
    }

    @Override
    public OAuthUser getUserInfo(String code) throws Exception {
        String accessToken = getAccessToken(code);
        return fetchUserInfo(accessToken);
    }

    private String getAccessToken(String code) throws Exception {
        String url = githubProperties.getAccessTokenUrl() +
                "?client_id=" + githubProperties.getClientId() +
                "&client_secret=" + githubProperties.getClientSecret() +
                "&code=" + code +
                "&grant_type=" + githubProperties.getGrantType();

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("accept", oauthProperties.getAcceptMediaType());
        HttpEntity<String> requestEntity = new HttpEntity<>(requestHeaders);

        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
        String responseStr = response.getBody();
        log.debug("Access token response: {}", responseStr);

        JsonNode jsonNode = objectMapper.readTree(responseStr);
        if (jsonNode.has(oauthProperties.getErrorField())) {
            String error = jsonNode.get(oauthProperties.getErrorField()).asText();
            String errorDescription = jsonNode.has(oauthProperties.getErrorDescriptionField())
                ? jsonNode.get(oauthProperties.getErrorDescriptionField()).asText()
                : "Unknown error";
            log.error("GitHub OAuth error: {} - {}", error, errorDescription);
            throw new RuntimeException("GitHub OAuth error: " + error + " - " + errorDescription);
        }

        return jsonNode.get(oauthProperties.getAccessTokenField()).asText();
    }

    private OAuthUser fetchUserInfo(String accessToken) {
        String url = githubProperties.getUserInfoUrl();

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("accept", oauthProperties.getAcceptMediaType());
        requestHeaders.add("Authorization", oauthProperties.getAuthorizationScheme() + " " + accessToken);
        HttpEntity<String> requestEntity = new HttpEntity<>(requestHeaders);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
        String userInfoStr = response.getBody();
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
            throw new RuntimeException("Failed to parse GitHub user info", e);
        }

        log.info("Successfully retrieved GitHub user: {}", oauthUser.getUsername());
        return oauthUser;
    }

    private String getTextOrNull(JsonNode node, String field) {
        return node.has(field) && !node.get(field).isNull() ? node.get(field).asText() : null;
    }
}
