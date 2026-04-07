package com.roan.align.security;

import com.roan.align.entity.FF4jUserRole;
import com.roan.align.repository.UserRoleRepository;
import org.ff4j.security.AuthorizationsManager;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Custom FF4J Authorization Manager that retrieves user permissions
 * from the database via Spring Data JPA.
 *
 * @author Roan
 * @date 2026/3/30
 */
@Component
public class CustomFf4jAuthManager implements AuthorizationsManager {

    private final UserRoleRepository userRoleRepository;

    public CustomFf4jAuthManager(UserRoleRepository userRoleRepository) {
        this.userRoleRepository = userRoleRepository;
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
     * Query user roles from database using JPA.
     */
    private Set<String> getRolesFromDatabase(String username) {
        List<FF4jUserRole> userRoles = userRoleRepository.findByUsername(username);
        return userRoles.stream()
                .map(FF4jUserRole::getRoleName)
                .collect(Collectors.toSet());
    }

    /**
     * Returns all available permissions in the system.
     * Used by FF4J console to display permissions dropdown.
     */
    @Override
    public Set<String> listAllPermissions() {
        List<String> distinctRoles = userRoleRepository.findAllDistinctRoleNames();

        // Return database roles if available, otherwise default roles
        if (distinctRoles != null && !distinctRoles.isEmpty()) {
            return new HashSet<>(distinctRoles);
        }

        // Default roles if database is empty
        Set<String> defaultPermissions = new HashSet<>();
        defaultPermissions.add("ADMIN");
        defaultPermissions.add("USER");
        defaultPermissions.add("READONLY");
        return defaultPermissions;
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