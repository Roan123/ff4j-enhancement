package com.roan.align.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Security filter that validates JWT token from cookie and sets security context.
 * Protects FF4J web console paths.
 *
 * @author Roan
 * @date 2026/3/30
 */
@Component
@Order(1)
public class Ff4jSecurityFilter extends OncePerRequestFilter {

    private static final String FF4J_TOKEN_COOKIE = "FF4J_TOKEN";
    private static final String LOGIN_PAGE = "/login.html";
    private static final String API_LOGIN = "/api/login";
    private static final String API_LOGOUT = "/api/logout";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // Skip filtering for login page, login API, and logout API
        if (path.equals(LOGIN_PAGE) || path.equals(API_LOGIN) || path.equals(API_LOGOUT) || 
            path.startsWith("/static/") || path.startsWith("/css/") || path.startsWith("/js/")) {
            chain.doFilter(request, response);
            return;
        }

        // Only protect FF4J console paths
        if (!path.startsWith("/ff4j-web-console")) {
            chain.doFilter(request, response);
            return;
        }

        // Get token from cookie
        String token = getJwtFromCookie(request);

        if (token != null && validateToken(token)) {
            // Parse token and set security context
            Ff4jSecurityContext.Ff4jUser user = parseToken(token);
            Ff4jSecurityContext.set(user);

            try {
                chain.doFilter(request, response);
            } finally {
                Ff4jSecurityContext.clear();
            }
        } else {
            // No valid token, redirect to login page
            response.sendRedirect(LOGIN_PAGE);
        }
    }

    /**
     * Extracts JWT token from cookies.
     */
    private String getJwtFromCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {
            return Arrays.stream(request.getCookies())
                    .filter(c -> FF4J_TOKEN_COOKIE.equals(c.getName()))
                    .map(jakarta.servlet.http.Cookie::getValue)
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    /**
     * Validates the token.
     * For mock tokens, simple validation.
     * In production, should validate JWT signature and expiration.
     */
    private boolean validateToken(String token) {
        // Mock validation - check if token starts with "mock-jwt-token-"
        // In production, use JWT library to validate signature and expiration
        return token != null && token.startsWith("mock-jwt-token-");
    }

    /**
     * Parses token to extract user information.
     * For mock tokens, extract username from token.
     * In production, decode JWT and extract claims.
     */
    private Ff4jSecurityContext.Ff4jUser parseToken(String token) {
        // Mock parsing: "mock-jwt-token-{username}-{timestamp}"
        String[] parts = token.split("-");
        String username = parts.length >= 4 ? parts[3] : "unknown";
        
        // Default role for now
        Set<String> roles = Set.of("ADMIN");
        
        return new Ff4jSecurityContext.Ff4jUser(username, roles);
    }
}
