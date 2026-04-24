package icu.ruiyu.framework.integration.security.model;

import lombok.Getter;

/**
 * 角色类型枚举
 */
@Getter
public enum RoleType {
    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER");

    private final String roleName;

    RoleType(String roleName) {
        this.roleName = roleName;
    }

    /**
     * 根据角色名获取 RoleType
     */
    public static RoleType fromRoleName(String roleName) {
        for (RoleType type : values()) {
            if (type.roleName.equals(roleName)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown role: " + roleName);
    }
}