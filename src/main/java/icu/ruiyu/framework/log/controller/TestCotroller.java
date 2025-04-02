package icu.ruiyu.framework.log.controller;

import icu.ruiyu.framework.log.annotation.RecordRequestAndResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/testaop")
public class TestCotroller {

    @GetMapping("/1")
    @RecordRequestAndResponse
    public String test1(@RequestParam String args){
        System.out.println("TestCotroller test1, args: " + args);
        return "aop1";
    }
}
