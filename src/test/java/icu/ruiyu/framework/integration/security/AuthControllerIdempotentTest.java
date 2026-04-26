package icu.ruiyu.framework.integration.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import icu.ruiyu.framework.integration.security.dto.SignInReq;
import icu.ruiyu.framework.integration.security.dto.SignUpReq;
import com.ruiyu.framework.FrameworkApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 幂等性测试
 * 验证 AuthController 的 register 和 login 方法在 60 秒内对相同请求的幂等保护
 */
@SpringBootTest(classes = FrameworkApplication.class)
@AutoConfigureMockMvc
class AuthControllerIdempotentTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    void testRegisterIdempotent() throws Exception {
        String testUsername = "idem" + System.currentTimeMillis();

        SignUpReq req = new SignUpReq();
        req.setUsername(testUsername);
        req.setPassword("password123");

        // First request - should succeed
        mockMvc.perform(post("/api/v1/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("注册成功"));

        // Second request with same params - should be rejected with 409
        mockMvc.perform(post("/api/v1/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(409))
                .andExpect(jsonPath("$.message").value("请求已提交，请勿重复操作"));

        // Clean up - delete the idempotent key
        String keyPattern = "idempotent:*" + testUsername + "*";
        stringRedisTemplate.delete(stringRedisTemplate.keys(keyPattern));
    }

    @Test
    void testLoginIdempotent() throws Exception {
        String testUsername = "lidm" + System.currentTimeMillis();

        // First register a user
        SignUpReq registerReq = new SignUpReq();
        registerReq.setUsername(testUsername);
        registerReq.setPassword("password123");
        mockMvc.perform(post("/api/v1/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerReq)));

        // First login - should succeed
        SignInReq loginReq = new SignInReq();
        loginReq.setUsername(testUsername);
        loginReq.setPassword("password123");

        mockMvc.perform(post("/api/v1/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isNotEmpty());

        // Second login with same credentials - should be rejected with 409
        mockMvc.perform(post("/api/v1/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(409))
                .andExpect(jsonPath("$.message").value("请求已提交，请勿重复操作"));

        // Clean up - delete the idempotent key
        String keyPattern = "idempotent:*" + testUsername + "*";
        stringRedisTemplate.delete(stringRedisTemplate.keys(keyPattern));
    }

    @Test
    void testDifferentUsersNotIdempotent() throws Exception {
        // This test verifies that different usernames create different idempotent keys
        String timestamp = String.valueOf(System.currentTimeMillis());
        String username1 = "u1" + timestamp;
        String username2 = "u2" + timestamp;

        SignUpReq req1 = new SignUpReq();
        req1.setUsername(username1);
        req1.setPassword("password123");

        SignUpReq req2 = new SignUpReq();
        req2.setUsername(username2);
        req2.setPassword("password123");

        // First request for user1 - should succeed
        mockMvc.perform(post("/api/v1/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // Same request for user2 - should also succeed (different user)
        mockMvc.perform(post("/api/v1/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // Clean up
        String keyPattern = "idempotent:*" + timestamp + "*";
        stringRedisTemplate.delete(stringRedisTemplate.keys(keyPattern));
    }
}
