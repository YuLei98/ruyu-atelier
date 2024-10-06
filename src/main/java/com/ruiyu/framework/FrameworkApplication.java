package com.ruiyu.framework;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"icu.ruiyu.framework", "com.ruiyu.framework"})
public class FrameworkApplication {
	public static void main(String[] args) {
		SpringApplication.run(FrameworkApplication.class, args);
	}

}
