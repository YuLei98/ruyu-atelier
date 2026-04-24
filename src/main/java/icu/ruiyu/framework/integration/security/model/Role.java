package icu.ruiyu.framework.integration.security.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 角色实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    private RoleType roleType;
}