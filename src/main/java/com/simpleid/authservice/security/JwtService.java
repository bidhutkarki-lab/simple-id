package com.simpleid.authservice.security;

import com.simpleid.authservice.config.JwtProperties;
import com.simpleid.authservice.model.Role;
import com.simpleid.authservice.model.User;
import io.jsonwebtoken.Jwts;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private static final String ROLES_CLAIM = "roles";

    private final JwtProperties properties;
    private final RsaKeyProvider keyProvider;

    public JwtService(JwtProperties properties, RsaKeyProvider keyProvider) {
        this.properties = properties;
        this.keyProvider = keyProvider;
    }

    public String generateAccessToken(User user) {
        Instant now = Instant.now();
        Instant expiry = now.plus(properties.getAccessTokenTtl());
        List<String> roles = user.getRoles().stream().map(Role::name).toList();

        return Jwts.builder()
                .header().keyId(keyProvider.getKeyId()).and()
                .issuer(properties.getIssuer())
                .subject(String.valueOf(user.getId()))
                .claim("email", user.getEmail())
                .claim(ROLES_CLAIM, roles)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(keyProvider.getPrivateKey(), Jwts.SIG.RS256)
                .compact();
    }

    public long accessTokenTtlSeconds() {
        return properties.getAccessTokenTtl().toSeconds();
    }
}
