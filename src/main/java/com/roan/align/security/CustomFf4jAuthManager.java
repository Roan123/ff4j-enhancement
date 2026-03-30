package com.roan.align.security;

import org.ff4j.security.AuthorizationsManager;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Custom FF4J Authorization Manager that retrieves user permissions
 * from the security context set by Ff4jSecurityFilter.
 *
 * @author Roan
 * @date 2026/3/30
 */
public class CustomFf4jAuthManager implements AuthorizationsManager {

    /**
     * Returns the current user's permissions from the security context.
     */
    @Override
    public Set<String> getCurrentUserPermissions() {
        Ff4jSecurityContext.Ff4jUser user = Ff4jSecurityContext.get();
        if (user != null) {
            return user.getRoles();
        }
        return Collections.emptySet();
    }

    /**
     * Returns all available permissions in the system.
     * Used by FF4J console to display permissions dropdown.
     */
    @Override
    public Set<String> listAllPermissions() {
        // Define all possible roles that can be assigned to features
        return new HashSet<>(Set.of("ADMIN", "USER", "READONLY"));
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
