package icu.ruiyu.framework.integration.security.service;

import icu.ruiyu.framework.integration.mysql.mapper.UserMapper;
import icu.ruiyu.framework.integration.mysql.model.User;
import icu.ruiyu.framework.integration.security.model.AuthUser;
import icu.ruiyu.framework.integration.security.model.Role;
import icu.ruiyu.framework.integration.security.model.RoleType;
import jakarta.annotation.Resource;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements UserService {
    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private UserMapper userMapper;

    @Override
    public AuthUser getUserByName(String username) {
        User dbUser = userMapper.selectByUsername(username);
        if (dbUser == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }
        return new AuthUser(
                dbUser.getUsername(),
                dbUser.getPasswordHash(),
                List.of(new Role(RoleType.fromRoleName(dbUser.getRoles())))
        );
    }

    @Override
    public void register(String username, String password) {
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPasswordHash(passwordEncoder.encode(password));
        newUser.setRoles("ROLE_USER");
        newUser.setEnabled(1);
        userMapper.insert(newUser);
    }

    @Override
    public boolean usernameExists(String username) {
        return userMapper.existsByUsername(username);
    }
}