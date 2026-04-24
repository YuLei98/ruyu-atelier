package icu.ruiyu.framework.integration.security.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 安全模块用户实体（仅包含认证相关字段）
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthUser {
    private String username;
    private String password;
    private List<Role> roles;
}