package com.yourcaryourway.backend.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for JWT handling.
 * Binds properties prefixed with "jwt" from application.properties.
 */
@Component
@ConfigurationProperties(prefix = "jwt")
@Data
public class JwtProperties {

    private String secretKey;
    private long accessTokenDuration;
    private long refreshTokenDuration;

}
