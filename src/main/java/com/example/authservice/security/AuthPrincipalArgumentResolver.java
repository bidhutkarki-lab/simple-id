package com.example.authservice.security;

import com.example.authservice.exception.ApiException;
import com.example.authservice.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * Builds the {@link AuthPrincipal} from the {@code X-User-Id} header forwarded by
 * the upstream middleware (which performs JWT verification). Email and roles are
 * loaded from the database. Rejects the request with {@code 401 Unauthorized}
 * when the header is missing or malformed, or when no matching user exists.
 */
@Component
public class AuthPrincipalArgumentResolver implements HandlerMethodArgumentResolver {

    private static final String USER_ID_HEADER = "X-User-Id";

    private final UserRepository userRepository;

    public AuthPrincipalArgumentResolver(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(AuthPrincipal.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        if (request == null) {
            throw ApiException.unauthorized("Missing request context");
        }

        String userIdHeader = request.getHeader(USER_ID_HEADER);
        if (userIdHeader == null || userIdHeader.isBlank()) {
            throw ApiException.unauthorized("Missing X-User-Id header");
        }

        long userId;
        try {
            userId = Long.parseLong(userIdHeader.trim());
        } catch (NumberFormatException ex) {
            throw ApiException.unauthorized("Malformed X-User-Id header");
        }

        return userRepository.findById(userId)
                .map(user -> new AuthPrincipal(user.getId(), user.getEmail(), user.getRoles()))
                .orElseThrow(() -> ApiException.unauthorized("Unknown user"));
    }
}
