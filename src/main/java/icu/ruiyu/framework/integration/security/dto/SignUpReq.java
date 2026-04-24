package icu.ruiyu.framework.integration.security.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 注册请求体
 */
@Data
@NoArgsConstructor
public class SignUpReq {
    private String username;
    private String password;
}