package icu.ruiyu.framework.integration.security.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录请求体
 */
@Data
@NoArgsConstructor
public class SignInReq {
    private String username;
    private String password;
}