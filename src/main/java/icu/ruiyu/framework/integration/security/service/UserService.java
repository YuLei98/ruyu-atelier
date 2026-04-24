package icu.ruiyu.framework.integration.security.service;

import icu.ruiyu.framework.integration.security.model.AuthUser;

/**
 * 用户服务接口
 */
public interface UserService {
    /**
     * 根据用户名获取用户
     */
    AuthUser getUserByName(String username);

    /**
     * 注册新用户
     */
    void register(String username, String password);

    /**
     * 检查用户名是否已存在
     */
    boolean usernameExists(String username);
}