package com.example.authservice.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Validates the Bearer access token on protected routes and exposes the resulting
 * {@link AuthPrincipal} as a request attribute. Implemented with Spring Web's
 * {@link OncePerRequestFilter} (no Spring Security filter chain).
 */
public class JwtAuthFilter extends OncePerRequestFilter {

    public static final String PRINCIPAL_ATTRIBUTE = "authPrincipal";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;
    private final ObjectMapper objectMapper;

    public JwtAuthFilter(JwtService jwtService, ObjectMapper objectMapper) {
        this.jwtService = jwtService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith(BEARER_PREFIX)) {
            writeUnauthorized(response, "Missing or malformed Authorization header");
            return;
        }

        String token = header.substring(BEARER_PREFIX.length()).trim();
        try {
            AuthPrincipal principal = jwtService.parse(token);
            request.setAttribute(PRINCIPAL_ATTRIBUTE, principal);
        } catch (JwtException | IllegalArgumentException ex) {
            writeUnauthorized(response, "Invalid or expired token");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void writeUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        Map<String, Object> body = Map.of(
                "timestamp", Instant.now().toString(),
                "status", HttpStatus.UNAUTHORIZED.value(),
                "error", "Unauthorized",
                "message", message);
        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
