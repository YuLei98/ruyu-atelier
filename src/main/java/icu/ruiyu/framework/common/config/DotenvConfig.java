package icu.ruiyu.framework.common.config;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

/**
 * Dotenv 配置，自动加载 .env 文件到系统环境变量
 */
@Slf4j
@Configuration
public class DotenvConfig {

    @PostConstruct
    public void loadDotenv() {
        try {
            Dotenv dotenv = Dotenv.configure()
                    .ignoreIfMissing()
                    .load();
            dotenv.entries().forEach(entry ->
                    System.setProperty(entry.getKey(), entry.getValue()));
            log.info("Dotenv loaded successfully");
        } catch (Exception e) {
            log.warn("Failed to load .env file, using system environment variables: {}", e.getMessage());
        }
    }
}
