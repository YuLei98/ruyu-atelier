package com.ruiyu.framework.core.service;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.ruiyu.framework.core.model.User;
import icu.ruiyu.framework.integration.cache.CacheService;
import icu.ruiyu.framework.integration.cache.ExpireEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
@Slf4j
public class UserService {
    @Autowired
    CacheService cacheService;

    private User createUser(String username, Integer age, String nickname) {
        User user = new User();
        user.setName(username);
        user.setAge(age);
        user.setNickname(nickname);
        return user;
    }

    private void putUserIntoCache(User user) {
        cacheService.set(user.getName(), JSON.toJSONString(user), ExpireEnum.THIRTY_SECONDS);
    }

    public Optional<User> queryUserByUsername(String username) {
        String user = cacheService.get(username);
        if (StrUtil.isBlank(user)) {
            log.error("get user from cache, username:{}", username);
            return Optional.empty();
        }
        return Optional.ofNullable(JSON.parseObject(user, User.class));
    }

    public User addUser(String username, Integer age, String nickname) {
        User user = createUser(username, age, nickname);
        putUserIntoCache(user);
        return user;
    }
}
