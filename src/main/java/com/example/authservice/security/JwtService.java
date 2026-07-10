package com.example.authservice.security;

import com.example.authservice.config.JwtProperties;
import com.example.authservice.model.Role;
import com.example.authservice.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private static final String ROLES_CLAIM = "roles";

    private final JwtProperties properties;
    private final SecretKey signingKey;

    public JwtService(JwtProperties properties) {
        this.properties = properties;
        this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(properties.getSecret()));
    }

    public String generateAccessToken(User user) {
        Instant now = Instant.now();
        Instant expiry = now.plus(properties.getAccessTokenTtl());
        List<String> roles = user.getRoles().stream().map(Role::name).toList();

        return Jwts.builder()
                .issuer(properties.getIssuer())
                .subject(String.valueOf(user.getId()))
                .claim("username", user.getUsername())
                .claim(ROLES_CLAIM, roles)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(signingKey)
                .compact();
    }

    /**
     * Validates the token signature, issuer and expiry, returning the principal.
     *
     * @throws JwtException if the token is invalid or expired
     */
    public AuthPrincipal parse(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(signingKey)
                .requireIssuer(properties.getIssuer())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        Long userId = Long.valueOf(claims.getSubject());
        String username = claims.get("username", String.class);
        Set<Role> roles = parseRoles(claims);
        return new AuthPrincipal(userId, username, roles);
    }

    public long accessTokenTtlSeconds() {
        return properties.getAccessTokenTtl().toSeconds();
    }

    @SuppressWarnings("unchecked")
    private Set<Role> parseRoles(Claims claims) {
        Object raw = claims.get(ROLES_CLAIM);
        if (raw instanceof List<?> list) {
            return list.stream()
                    .map(Object::toString)
                    .map(Role::valueOf)
                    .collect(Collectors.toSet());
        }
        return Set.of();
    }
}
