package com.simpleid.authservice.dto;

import com.simpleid.authservice.model.Role;
import com.simpleid.authservice.model.User;
import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

public record UserResponse(
        Long id,
        String email,
        Set<String> roles,
        Instant createdAt) {

    public static UserResponse from(User user) {
        Set<String> roleNames = user.getRoles().stream()
                .map(Role::name)
                .collect(Collectors.toSet());
        return new UserResponse(user.getId(), user.getEmail(), roleNames, user.getCreatedAt());
    }
}
