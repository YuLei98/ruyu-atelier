package icu.ruiyu.framework.integration.security.model;

import lombok.Getter;

@Getter
public enum RoleType {
    // NOTE: 此处角色名应以 ROLE_ 为前缀，否则将一直报错。
    ADMIN("ROLE_admin"),
    USER("ROLE_user");

    final private String roleName;

    RoleType(String roleName) {
        this.roleName = roleName;
    }
}
