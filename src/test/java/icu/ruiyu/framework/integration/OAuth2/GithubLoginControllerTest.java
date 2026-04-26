package icu.ruiyu.framework.integration.OAuth2;

import icu.ruiyu.framework.integration.OAuth2.config.GithubProperties;
import icu.ruiyu.framework.integration.OAuth2.config.OAuthProperties;
import icu.ruiyu.framework.integration.OAuth2.service.OAuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = com.ruiyu.framework.FrameworkApplication.class)
@AutoConfigureMockMvc
class OAuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GithubProperties githubProperties;

    @Autowired
    private OAuthProperties oauthProperties;

    @Autowired
    private OAuthService githubOAuthService;

    @BeforeEach
    void setUp() {
        // 设置 GitHub 配置
        githubProperties.setClientId("test_client_id");
        githubProperties.setClientSecret("test_client_secret");
        githubProperties.setAuthorizeUrl("https://github.com/login/oauth/authorize");
        githubProperties.setRedirectUrl("http://localhost:8000/api/v1/oauth/redirect");
        githubProperties.setAccessTokenUrl("https://github.com/login/oauth/access_token");
        githubProperties.setUserInfoUrl("https://api.github.com/user");
        githubProperties.setProvider("github");
        githubProperties.setGrantType("authorization_code");
        githubProperties.setUserIdField("id");
        githubProperties.setUsernameField("login");
        githubProperties.setAvatarUrlField("avatar_url");
        githubProperties.setEmailField("email");
    }

    @Test
    void testAuthorizeRedirectUrl() throws Exception {
        mockMvc.perform(get("/api/v1/oauth/authorize"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("https://github.com/login/oauth/authorize?client_id=test_client_id&redirect_uri=http%3A%2F%2Flocalhost%3A8000%2Fapi%2Fv1%2Foauth%2Fredirect"));
    }

    @Test
    void testGithubPropertiesConfigured() {
        assertEquals("test_client_id", githubProperties.getClientId());
        assertEquals("test_client_secret", githubProperties.getClientSecret());
        assertEquals("https://github.com/login/oauth/authorize", githubProperties.getAuthorizeUrl());
        assertEquals("https://github.com/login/oauth/access_token", githubProperties.getAccessTokenUrl());
        assertEquals("http://localhost:8000/api/v1/oauth/redirect", githubProperties.getRedirectUrl());
        assertEquals("https://api.github.com/user", githubProperties.getUserInfoUrl());
        assertEquals("github", githubProperties.getProvider());
    }

    @Test
    void testOAuthPropertiesConfigured() {
        assertEquals("application/json", oauthProperties.getAcceptMediaType());
        assertEquals("Bearer", oauthProperties.getAuthorizationScheme());
        assertEquals("access_token", oauthProperties.getAccessTokenField());
        assertEquals("error", oauthProperties.getErrorField());
    }

    @Test
    void testOAuthRedirectWithoutCode() throws Exception {
        mockMvc.perform(get("/api/v1/oauth/redirect"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAuthorizationUrl() {
        String url = githubOAuthService.getAuthorizationUrl();
        assertNotNull(url);
        assertTrue(url.contains("https://github.com/login/oauth/authorize"));
        assertTrue(url.contains("client_id=test_client_id"));
    }

    @Test
    void testGithubOAuthServiceBean() {
        assertNotNull(githubOAuthService);
        assertInstanceOf(icu.ruiyu.framework.integration.OAuth2.service.impl.GithubOAuthService.class, githubOAuthService);
    }
}
