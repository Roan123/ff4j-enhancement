package com.roan.align.security;

import org.ff4j.security.AuthorizationsManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Custom FF4J Authorization Manager that retrieves user permissions
 * from the database (FF4J_USER_ROLES table).
 *
 * @author Roan
 * @date 2026/3/30
 */
public class CustomFf4jAuthManager implements AuthorizationsManager {

    private final DataSource dataSource;

    public CustomFf4jAuthManager(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Returns the current user's permissions from the security context.
     * Falls back to database if security context is not set.
     */
    @Override
    public Set<String> getCurrentUserPermissions() {
        // First, try to get roles from security context (set from token)
        Ff4jSecurityContext.Ff4jUser user = Ff4jSecurityContext.get();
        if (user != null && user.getRoles() != null && !user.getRoles().isEmpty()) {
            return user.getRoles();
        }
        
        // Fallback to database if security context is not set
        if (user == null || user.getUsername() == null) {
            return Collections.emptySet();
        }
        
        return getRolesFromDatabase(user.getUsername());
    }

    /**
     * Query user roles from database.
     */
    private Set<String> getRolesFromDatabase(String username) {
        Set<String> roles = new HashSet<>();
        String sql = "SELECT ROLE_NAME FROM FF4J_USER_ROLES WHERE USERNAME = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    roles.add(rs.getString("ROLE_NAME"));
                }
            }
        } catch (SQLException e) {
            // Log error and return empty set
            System.err.println("Error fetching user roles: " + e.getMessage());
        }
        
        return roles;
    }

    /**
     * Returns all available permissions in the system.
     * Used by FF4J console to display permissions dropdown.
     */
    @Override
    public Set<String> listAllPermissions() {
        Set<String> permissions = new HashSet<>();
        String sql = "SELECT DISTINCT ROLE_NAME FROM FF4J_USER_ROLES";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                permissions.add(rs.getString("ROLE_NAME"));
            }
        } catch (SQLException e) {
            // Return default roles if query fails
            permissions.add("ADMIN");
            permissions.add("USER");
            permissions.add("READONLY");
        }
        
        return permissions;
    }

    /**
     * Returns the current username from the security context.
     */
    @Override
    public String getCurrentUserName() {
        Ff4jSecurityContext.Ff4jUser user = Ff4jSecurityContext.get();
        return user != null ? user.getUsername() : null;
    }

    /**
     * Returns JSON representation of the authorization manager.
     */
    @Override
    public String toJson() {
        return "{}";
    }
}
