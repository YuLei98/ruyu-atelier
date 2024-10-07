package icu.ruiyu.framework.integration.security.service;

import icu.ruiyu.framework.integration.security.model.User;

public interface UserService {
    User getUserByName(String username);
}
