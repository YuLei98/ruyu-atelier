package icu.ruiyu.framework.integration.security.model;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 安全模块配置常量
 */
@Component
public class Constants {
    @Value("${jwt.secret-key}")
    private String jwtSecretKey;

    /**
     * 获取 JWT 签名密钥
     */
    public String getJwtSignKey() {
        return jwtSecretKey;
    }
}