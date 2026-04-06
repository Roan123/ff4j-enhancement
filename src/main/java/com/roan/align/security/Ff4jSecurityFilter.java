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
 * Protects FF4J web console paths with role-based access control.
 *
 * @author Roan
 * @date 2026/3/30
 */
@Component
@Order(1)
public class Ff4jSecurityFilter extends OncePerRequestFilter {

    private static final String CONTEXT_PATH = "/align";
    private static final String FF4J_TOKEN_COOKIE = "FF4J_TOKEN";
    private static final String LOGIN_PAGE = "/align/login.html";
    private static final String API_LOGIN = "/align/api/login";
    private static final String API_LOGOUT = "/align/api/logout";
    
    // Role required for write operations
    private static final String ADMIN_ROLE = "ADMIN";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // Skip filtering for login page, login API, and logout API
        if (path.equals(LOGIN_PAGE) || path.equals(API_LOGIN) || path.equals(API_LOGOUT) || 
            path.startsWith("/align/static/") || path.startsWith("/align/css/") || path.startsWith("/align/js/")) {
            chain.doFilter(request, response);
            return;
        }

        // Only protect FF4J console paths
        if (!path.startsWith("/align/ff4j-web-console")) {
            chain.doFilter(request, response);
            return;
        }

        // Get token from cookie
        String token = getJwtFromCookie(request);

        if (token != null && validateToken(token)) {
            // Parse token and set security context
            Ff4jSecurityContext.Ff4jUser user = parseToken(token);
            Ff4jSecurityContext.set(user);
            
            // Pass user info to other filters via request attribute
            request.setAttribute("ff4j.user", user);

            try {
            // Check if user has ADMIN role for write operations
            String method = request.getMethod();
            if (!user.hasRole(ADMIN_ROLE) && isWriteOperation(path, method)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied. ADMIN role required for this operation.");
                return;
            }
                
                chain.doFilter(request, response);
            } finally {
                Ff4jSecurityContext.clear();
            }
        } else {
            // No valid token, redirect to login page
            response.sendRedirect(CONTEXT_PATH + "/login.html");
        }
    }

    /**
     * Check if the request is a write operation (create, update, delete)
     */
    private boolean isWriteOperation(String path, String method) {
        // Check HTTP method - POST, PUT, DELETE are write operations
        if ("POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method) || "DELETE".equalsIgnoreCase(method)) {
            return true;
        }
        
        // Console write operations via GET with specific paths
        return path.contains("/api/features") && (
            path.contains("/create") || 
            path.contains("/enable") || 
            path.contains("/disable") ||
            path.contains("/delete") ||
            path.endsWith("/enable") ||
            path.endsWith("/disable") ||
            path.endsWith("/delete")
        );
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
     * For mock tokens, extract username and roles from token.
     * In production, decode JWT and extract claims.
     */
    private Ff4jSecurityContext.Ff4jUser parseToken(String token) {
        // Mock parsing: "mock-jwt-token-{username}-{timestamp}.{role1}.{role2}"
        String[] parts = token.split("\\.");
        String[] tokenParts = parts[0].split("-");
        String username = tokenParts.length >= 4 ? tokenParts[3] : "unknown";
        
        // Extract roles if present
        Set<String> roles;
        if (parts.length > 1 && parts[1] != null && !parts[1].isEmpty()) {
            roles = Arrays.stream(parts[1].split("\\."))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toSet());
        } else {
            // Default role for now
            roles = Set.of("ADMIN");
        }
        
        return new Ff4jSecurityContext.Ff4jUser(username, roles);
    }
}
