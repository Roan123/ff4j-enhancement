package com.roan.align.controller;

import com.roan.align.dto.LoginRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
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
@Controller
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    private static final String MOCK_USERNAME = "admin";
    private static final String MOCK_PASSWORD = "admin123";
    private static final String MOCK_USER1 = "user1";
    private static final String MOCK_PASSWORD1 = "user123";

    /**
     * Test endpoint to verify controller is working
     */
    @GetMapping("/api/test")
    @ResponseBody
    public ResponseEntity<?> test() {
        logger.info("Test endpoint called");
        return ResponseEntity.ok(Map.of("status", "ok"));
    }

    /**
     * Mock external Auth API response.
     * In production, this should call the real Auth API.
     */
    @PostMapping("/api/login")
    @ResponseBody
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials, HttpServletResponse response) {
        try {
            String username = credentials.get("username");
            String password = credentials.get("password");
            
            logger.info("Login attempt for username: {}", username);
            logger.info("Received credentials: username={}, password={}", username, password);
            
            // Validate credentials
            boolean validAdmin = MOCK_USERNAME.equals(username) && MOCK_PASSWORD.equals(password);
            boolean validUser = MOCK_USER1.equals(username) && MOCK_PASSWORD1.equals(password);
            
            if (!validAdmin && !validUser) {
                logger.warn("Invalid credentials for username: {}", username);
                return ResponseEntity.status(401).body(Map.of("error", "Invalid username or password"));
            }

            // Mock external Auth API response - Option B: JSON format
            String mockToken = generateMockToken(username);
            
            Map<String, Object> authResponse = new HashMap<>();
            authResponse.put("token", mockToken);
            authResponse.put("username", username);
            
            // Determine roles based on username
            if (validAdmin) {
                authResponse.put("roles", Set.of("ADMIN", "READONLY"));
            } else {
                authResponse.put("roles", Set.of("READONLY"));
            }

            // Write token to HttpOnly Cookie
            Cookie cookie = new Cookie("FF4J_TOKEN", mockToken);
            cookie.setHttpOnly(true);
            cookie.setPath("/align");
            // disable to let it expire after browser closed
            // cookie.setMaxAge(3600); // 1 hour
            // cookie.setSecure(true); // Enable if using HTTPS
            response.addCookie(cookie);

            logger.info("Login successful for username: {}", username);
            return ResponseEntity.ok(authResponse);
        } catch (Exception e) {
            logger.error("Login error", e);
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Logout endpoint - clears the authentication cookie.
     */
    @PostMapping("/api/logout")
    @ResponseBody
    public ResponseEntity<?> logout(HttpServletResponse response) {
        try {
            Cookie cookie = new Cookie("FF4J_TOKEN", "");
            cookie.setHttpOnly(true);
            cookie.setPath("/align");
            cookie.setMaxAge(0); // Expire immediately
            response.addCookie(cookie);
            
            return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
        } catch (Exception e) {
            logger.error("Logout error", e);
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Mock token generation.
     * In production, this should call external Auth API.
     * Format: "mock-jwt-token-{username}-{timestamp}.{role1}.{role2}"
     */
    private String generateMockToken(String username) {
        // Simple mock token - in production, this would be a real JWT from external Auth API
        long timestamp = System.currentTimeMillis();
        
        String rolesStr;
        if (MOCK_USERNAME.equals(username)) {
            rolesStr = "ADMIN.READONLY";
        } else {
            rolesStr = "READONLY";
        }
        
        return "mock-jwt-token-" + username + "-" + timestamp + "." + rolesStr;
    }
}
