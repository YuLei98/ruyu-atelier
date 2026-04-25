package icu.ruiyu.framework.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Framework API")
                        .description("Ruiyu Spring Boot Framework API Documentation")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Ruiyu")
                                .url("https://github.com/ruiyu"))
                        .license(new License()
                                .name("MIT License")));
    }
}