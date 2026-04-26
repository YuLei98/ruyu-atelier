package com.ruiyu.atelier;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"icu.ruiyu.framework", "com.ruiyu.atelier", "com.ruiyu.outdoor"})
@MapperScan({"icu.ruiyu.framework.integration.mysql.mapper", "com.ruiyu.outdoor.mapper"})
public class AtelierApplication {
	public static void main(String[] args) {
		SpringApplication.run(AtelierApplication.class, args);
	}

}
