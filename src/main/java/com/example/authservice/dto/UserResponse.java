package com.example.authservice.dto;

import com.example.authservice.model.Role;
import com.example.authservice.model.User;
import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

public record UserResponse(
        Long id,
        String email,
        String username,
        Set<String> roles,
        Instant createdAt) {

    public static UserResponse from(User user) {
        Set<String> roleNames = user.getRoles().stream()
                .map(Role::name)
                .collect(Collectors.toSet());
        return new UserResponse(user.getId(), user.getEmail(), user.getUsername(), roleNames, user.getCreatedAt());
    }
}
