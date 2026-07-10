package com.example.authservice.config;

import java.time.Duration;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {

    /** Base64-encoded HMAC secret (at least 32 bytes when decoded). */
    private String secret;

    private String issuer = "simple-id";

    private Duration accessTokenTtl = Duration.ofMinutes(15);

    private Duration refreshTokenTtl = Duration.ofDays(7);
}
