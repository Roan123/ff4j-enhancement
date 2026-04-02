package com.roan.align.controller;

import com.roan.align.dto.LoginRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Login controller that handles authentication with external Auth API.
 * For now, it uses a mock implementation.
 *
 * @author Roan
 * @date 2026/3/30
 */
@RestController
public class LoginController {

    private static final String MOCK_USERNAME = "admin";
    private static final String MOCK_PASSWORD = "admin123";

    /**
     * Mock external Auth API response.
     * In production, this should call the real Auth API.
     */
    @PostMapping("/api/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        // Validate credentials
        if (!MOCK_USERNAME.equals(loginRequest.getUsername()) || 
            !MOCK_PASSWORD.equals(loginRequest.getPassword())) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid username or password"));
        }

        // Mock external Auth API response - Option B: JSON format
        String mockToken = generateMockToken(loginRequest.getUsername());
        
        Map<String, Object> authResponse = new HashMap<>();
        authResponse.put("token", mockToken);
        authResponse.put("username", loginRequest.getUsername());
        authResponse.put("roles", Set.of("ADMIN")); // Default role for FF4J access

        // Write token to HttpOnly Cookie
        Cookie cookie = new Cookie("FF4J_TOKEN", mockToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/align");
        // disable to let it expire after browser closed
        // cookie.setMaxAge(3600); // 1 hour
        // cookie.setSecure(true); // Enable if using HTTPS
        response.addCookie(cookie);

        return ResponseEntity.ok(authResponse);
    }

    /**
     * Logout endpoint - clears the authentication cookie.
     */
    @PostMapping("/api/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("FF4J_TOKEN", "");
        cookie.setHttpOnly(true);
        cookie.setPath("/align");
        cookie.setMaxAge(0); // Expire immediately
        response.addCookie(cookie);
        
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    /**
     * Mock token generation.
     * In production, this should call external Auth API.
     */
    private String generateMockToken(String username) {
        // Simple mock token - in production, this would be a real JWT from external Auth API
        long timestamp = System.currentTimeMillis();
        return "mock-jwt-token-" + username + "-" + timestamp;
    }
}
