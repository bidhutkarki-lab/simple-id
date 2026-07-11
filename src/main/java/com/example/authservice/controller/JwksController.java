package com.example.authservice.controller;

import com.example.authservice.security.RsaKeyProvider;
import io.jsonwebtoken.security.Jwk;
import io.jsonwebtoken.security.Jwks;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Publishes the RSA public key as a JSON Web Key Set so the upstream middleware
 * can verify access tokens. Only public key material is exposed.
 */
@RestController
public class JwksController {

    private final RsaKeyProvider keyProvider;

    public JwksController(RsaKeyProvider keyProvider) {
        this.keyProvider = keyProvider;
    }

    @GetMapping("/.well-known/jwks.json")
    public Map<String, Object> jwks() {
        Jwk<?> jwk = Jwks.builder()
                .key(keyProvider.getPublicKey())
                .id(keyProvider.getKeyId())
                .build();
        return Map.of("keys", List.of(jwk));
    }
}
