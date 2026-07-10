package com.example.authservice.security;

import com.example.authservice.model.Role;
import java.util.Set;

/** Lightweight authenticated principal extracted from a validated access token. */
public record AuthPrincipal(Long userId, String email, Set<Role> roles) {

    public boolean hasRole(Role role) {
        return roles.contains(role);
    }
}
