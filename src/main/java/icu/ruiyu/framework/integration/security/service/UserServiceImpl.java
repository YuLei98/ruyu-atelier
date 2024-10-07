package icu.ruiyu.framework.integration.security.service;

import icu.ruiyu.framework.integration.security.model.Role;
import icu.ruiyu.framework.integration.security.model.RoleType;
import icu.ruiyu.framework.integration.security.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    PasswordEncoder passwordEncoder;

    /**
     * @param username
     * @return
     */
    @Override
    public User getUserByName(String username) {
        if ("ruiyu".equals(username)) {
            return new User(username,
                    passwordEncoder.encode("123456"),
                    List.of(new Role(RoleType.USER))
            );
        } else if ("admin".equals(username)) {
            return new User(username,
                    passwordEncoder.encode("123456"),
                    List.of(new Role(RoleType.ADMIN))
            );
        }
        throw new RuntimeException("用户不存在");
    }
}
