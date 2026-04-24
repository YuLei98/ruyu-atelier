package icu.ruiyu.framework.integration.security.controller;

import cn.hutool.jwt.JWT;
import icu.ruiyu.framework.common.CommonResult;
import icu.ruiyu.framework.integration.security.dto.SignInReq;
import icu.ruiyu.framework.integration.security.dto.SignUpReq;
import icu.ruiyu.framework.integration.security.model.Constants;
import icu.ruiyu.framework.integration.security.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

/**
 * 用户认证 Controller，处理注册、登录、注销
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class AuthController {

    @Autowired
    private Constants constants;

    @Autowired
    private UserService userService;

    @Autowired
    AuthenticationManager authenticationManager;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public CommonResult<Void> register(@RequestBody SignUpReq req) {
        if (req.getUsername() == null || req.getUsername().isBlank()) {
            return CommonResult.error(400, "用户名不能为空");
        }
        if (req.getPassword() == null || req.getPassword().isBlank()) {
            return CommonResult.error(400, "密码不能为空");
        }
        if (userService.usernameExists(req.getUsername())) {
            return CommonResult.error(400, "用户名已存在");
        }
        userService.register(req.getUsername(), req.getPassword());
        log.info("User registered: {}", req.getUsername());
        return CommonResult.successMessage("注册成功");
    }

    /**
     * 用户登录，返回 JWT Token
     */
    @PostMapping("/login")
    public CommonResult<String> login(@RequestBody SignInReq req) {
        try {
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword());
            authenticationManager.authenticate(authenticationToken);

            String token = JWT.create()
                    .setPayload("username", req.getUsername())
                    .setKey(constants.getJwtSignKey().getBytes(StandardCharsets.UTF_8))
                    .sign();

            return CommonResult.<String>success(token);
        } catch (BadCredentialsException e) {
            return CommonResult.error(401, "认证失败");
        }
    }

    /**
     * 用户注销（JWT 无状态，客户端丢弃 Token 即可）
     */
    @PostMapping("/logout")
    public CommonResult<Void> logout() {
        return CommonResult.successMessage("注销成功");
    }

    /**
     * 获取用户详情（需 ADMIN 角色）
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/info/{username}")
    public CommonResult<String> getUserDetail(@PathVariable String username) {
        return CommonResult.<String>success("用户详情: " + username);
    }
}