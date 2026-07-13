package com.simpleid.authservice.controller;

import com.simpleid.authservice.dto.UserResponse;
import com.simpleid.authservice.exception.ApiException;
import com.simpleid.authservice.model.Role;
import com.simpleid.authservice.repository.UserRepository;
import com.simpleid.authservice.security.AuthPrincipal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;

    @GetMapping("/users")
    public List<UserResponse> listUsers(AuthPrincipal principal) {
        if (!principal.hasRole(Role.ADMIN)) {
            throw ApiException.forbidden("Requires ADMIN role");
        }
        return userRepository.findAll().stream()
                .map(UserResponse::from)
                .toList();
    }
}
