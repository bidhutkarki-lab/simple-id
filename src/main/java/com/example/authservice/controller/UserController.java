package com.example.authservice.controller;

import com.example.authservice.dto.UserResponse;
import com.example.authservice.exception.ApiException;
import com.example.authservice.model.User;
import com.example.authservice.repository.UserRepository;
import com.example.authservice.security.AuthPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/me")
    public UserResponse me(AuthPrincipal principal) {
        User user = userRepository.findById(principal.userId())
                .orElseThrow(() -> ApiException.notFound("User not found"));
        return UserResponse.from(user);
    }
}
