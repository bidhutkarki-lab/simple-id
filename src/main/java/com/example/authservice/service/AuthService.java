package com.example.authservice.service;

import com.example.authservice.config.JwtProperties;
import com.example.authservice.dto.LoginRequest;
import com.example.authservice.dto.RefreshRequest;
import com.example.authservice.dto.RegisterRequest;
import com.example.authservice.dto.TokenResponse;
import com.example.authservice.dto.UserResponse;
import com.example.authservice.exception.ApiException;
import com.example.authservice.model.RefreshToken;
import com.example.authservice.model.Role;
import com.example.authservice.model.User;
import com.example.authservice.repository.RefreshTokenRepository;
import com.example.authservice.repository.UserRepository;
import com.example.authservice.security.JwtService;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;

    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw ApiException.conflict("Username is already taken");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw ApiException.conflict("Email is already registered");
        }

        User user = new User(request.email(), request.username(), passwordEncoder.encode(request.password()));
        user.addRole(Role.USER);
        userRepository.save(user);
        return UserResponse.from(user);
    }

    @Transactional
    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> ApiException.unauthorized("Invalid credentials"));

        if (!user.isEnabled() || !passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw ApiException.unauthorized("Invalid credentials");
        }

        return issueTokens(user);
    }

    @Transactional
    public TokenResponse refresh(RefreshRequest request) {
        RefreshToken stored = refreshTokenRepository.findByToken(request.refreshToken())
                .orElseThrow(() -> ApiException.unauthorized("Invalid refresh token"));

        if (!stored.isActive()) {
            throw ApiException.unauthorized("Refresh token expired or revoked");
        }

        User user = userRepository.findById(stored.getUserId())
                .orElseThrow(() -> ApiException.unauthorized("Invalid refresh token"));

        // Rotate: revoke the presented token, then issue a fresh pair.
        stored.setRevoked(true);
        refreshTokenRepository.save(stored);
        return issueTokens(user);
    }

    private TokenResponse issueTokens(User user) {
        String accessToken = jwtService.generateAccessToken(user);
        String refreshValue = UUID.randomUUID().toString();
        Instant expiresAt = Instant.now().plus(jwtProperties.getRefreshTokenTtl());
        refreshTokenRepository.save(new RefreshToken(refreshValue, user.getId(), expiresAt));
        return TokenResponse.bearer(accessToken, refreshValue, jwtService.accessTokenTtlSeconds());
    }
}
