package com.simpleid.authservice.controller;

import com.simpleid.authservice.dto.UserResponse;
import com.simpleid.authservice.exception.ApiException;
import com.simpleid.authservice.model.User;
import com.simpleid.authservice.repository.UserRepository;
import com.simpleid.authservice.security.AuthPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/me")
    public UserResponse me(AuthPrincipal principal) {
        User user = userRepository.findById(principal.userId())
                .orElseThrow(() -> ApiException.notFound("User not found"));
        return UserResponse.from(user);
    }
}
