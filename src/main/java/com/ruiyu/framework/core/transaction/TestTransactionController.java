package com.ruiyu.framework.core.transaction;

import com.alibaba.fastjson.JSON;
import icu.ruiyu.framework.integration.mysql.mapper.UserMapper;
import icu.ruiyu.framework.integration.mysql.model.User;
import jakarta.annotation.Resource;
import lombok.extern.java.Log;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/trans")
@Log
public class TestTransactionController {
    @Resource
    private TransactionTemplate transactionTemplate;

    @Resource
    private UserMapper userMapper;

    Integer count = 102;

    @GetMapping("/add")
    public String add() {
        AtomicInteger successCount = new AtomicInteger();
        try {
            transactionTemplate.execute(status -> {
                User user = new User();
                user.setNickname("test_trans");
                user.setAge(26);
                int ok = userMapper.insert(user);
                successCount.addAndGet(ok);

                user = new User();
                user.setNickname("test_trans");
                user.setAge(27);
                ok = userMapper.insert(user);
                successCount.addAndGet(ok);
                if (successCount.get() != 2) {
                    throw new RuntimeException();
                }
                throw new RuntimeException("mock exception");
    //            return null;
            });
        } catch (Exception e) {
            successCount.set(-1);
            e.printStackTrace();
        }
        return "Success: " + successCount.get();
    }

    @GetMapping("/query/{id}")
    public String query(@PathVariable("id") Integer id) {
        return JSON.toJSONString(userMapper.selectById(id));
    }
}
