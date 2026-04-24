package icu.ruiyu.framework.integration.security.model;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Constants {
    @Value("${jwt.secret-key}")
    private String jwtSecretKey;

    public String getJwtSignKey() {
        return jwtSecretKey;
    }
}