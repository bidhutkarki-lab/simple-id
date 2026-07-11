package com.example.authservice.controller;

import com.example.authservice.dto.UserResponse;
import com.example.authservice.exception.ApiException;
import com.example.authservice.model.Role;
import com.example.authservice.repository.UserRepository;
import com.example.authservice.security.AuthPrincipal;
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
