package icu.ruiyu.framework.integration.security.model;

import lombok.Getter;

@Getter
public enum RoleType {
    // NOTE: 角色名统一使用 ROLE_ 前缀
    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER");

    final private String roleName;

    RoleType(String roleName) {
        this.roleName = roleName;
    }
}
