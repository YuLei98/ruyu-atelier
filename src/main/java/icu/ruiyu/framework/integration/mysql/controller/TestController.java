package icu.ruiyu.framework.integration.mysql.controller;

import icu.ruiyu.framework.integration.mysql.mapper.UserMapper;
import icu.ruiyu.framework.integration.mysql.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/mysql")
public class TestController {
    private static final Logger log = LoggerFactory.getLogger(TestController.class);
    @Autowired
    UserMapper userMapper;

    @GetMapping("/1")
    public User defaultPage() {
        User user = new User();
        user.setName("mysql-name");
        user.setAge(111);
        return user;
    }

    @GetMapping("/get/{id}")
    public User findAll(@PathVariable("id") int id ){
        log.info("findAll id:{}",id);
//        return userMapper.selectById(id);

        User user = userMapper.selectById(id);
        if (Objects.isNull(user)) {
            log.error("findAll id:{}, result: null",id);
            return new User();
        }
        return user;

//        return new User();
    }


}
