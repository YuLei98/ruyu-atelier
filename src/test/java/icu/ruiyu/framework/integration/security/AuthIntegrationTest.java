package icu.ruiyu.framework.integration.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import icu.ruiyu.framework.integration.security.dto.SignInReq;
import icu.ruiyu.framework.integration.security.dto.SignUpReq;
import icu.ruiyu.framework.integration.security.service.UserService;
import com.ruiyu.framework.FrameworkApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = FrameworkApplication.class)
@AutoConfigureMockMvc
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Test
    void testRegisterSuccess() throws Exception {
        String testUsername = "u" + System.currentTimeMillis();

        SignUpReq req = new SignUpReq();
        req.setUsername(testUsername);
        req.setPassword("password123");

        mockMvc.perform(post("/api/v1/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("注册成功"));
    }

    @Test
    void testRegisterDuplicateUsername() throws Exception {
        String testUsername = "dup" + System.currentTimeMillis();

        SignUpReq req1 = new SignUpReq();
        req1.setUsername(testUsername);
        req1.setPassword("password123");
        mockMvc.perform(post("/api/v1/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        SignUpReq req2 = new SignUpReq();
        req2.setUsername(testUsername);
        req2.setPassword("password456");
        mockMvc.perform(post("/api/v1/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("用户名已存在"));
    }

    @Test
    void testLoginSuccess() throws Exception {
        String testUsername = "login" + System.currentTimeMillis();

        SignUpReq registerReq = new SignUpReq();
        registerReq.setUsername(testUsername);
        registerReq.setPassword("password123");
        mockMvc.perform(post("/api/v1/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerReq)));

        SignInReq loginReq = new SignInReq();
        loginReq.setUsername(testUsername);
        loginReq.setPassword("password123");

        mockMvc.perform(post("/api/v1/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isNotEmpty());
    }

    @Test
    void testLoginInvalidPassword() throws Exception {
        String testUsername = "wrong" + System.currentTimeMillis();

        SignUpReq registerReq = new SignUpReq();
        registerReq.setUsername(testUsername);
        registerReq.setPassword("password123");
        mockMvc.perform(post("/api/v1/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerReq)));

        SignInReq loginReq = new SignInReq();
        loginReq.setUsername(testUsername);
        loginReq.setPassword("wrongpassword");

        mockMvc.perform(post("/api/v1/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.message").value("认证失败"));
    }

    @Test
    void testLoginNonexistentUser() throws Exception {
        SignInReq loginReq = new SignInReq();
        loginReq.setUsername("nouser_" + System.currentTimeMillis());
        loginReq.setPassword("password123");

        mockMvc.perform(post("/api/v1/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.message").value("认证失败"));
    }

    @Test
    void testProtectedEndpointWithoutToken() throws Exception {
        mockMvc.perform(get("/api/v1/user/info/someuser"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testProtectedEndpointWithAuth() throws Exception {
        mockMvc.perform(get("/api/v1/user/info/admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("SUCCESS"))
                .andExpect(jsonPath("$.data").value("用户详情: admin"));
    }

    @Test
    void testLogout() throws Exception {
        mockMvc.perform(post("/api/v1/user/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("注销成功"));
    }
}