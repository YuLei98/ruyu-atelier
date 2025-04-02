package com.ruiyu.framework;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"icu.ruiyu.framework", "com.ruiyu.framework"})
@MapperScan("icu.ruiyu.framework.integration.mysql.mapper")
public class FrameworkApplication {
	public static void main(String[] args) {
		SpringApplication.run(FrameworkApplication.class, args);
	}

}
