package icu.ruiyu.framework.integration.security.controller;


import cn.hutool.jwt.JWT;
import icu.ruiyu.framework.integration.security.model.Constants;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/user")
public class AuthController {

    @Data
    @NoArgsConstructor
    static public class SignInReq {
        private String username;
        private String password;
    }


    @Autowired
    AuthenticationManager authenticationManager;

    @GetMapping("/register")
    public String register() {
        return "注册成功";
    }

    @PostMapping("/login")
    public String login(@RequestBody SignInReq req) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword());
        authenticationManager.authenticate(authenticationToken);


        String token = JWT.create()
//                .setExpiresAt(new Date(System.currentTimeMillis() + (1000 * 30)))
                .setPayload("username", req.getUsername())
                .setKey(Constants.JWT_SIGN_KEY.getBytes(StandardCharsets.UTF_8))
                .sign();

        return token;
    }

    @PreAuthorize("hasRole('admin')")
    @GetMapping("/info/{username}")
    public String getUserDetail(@PathVariable String username) {
        return "用户详情: " + username;
    }
}
