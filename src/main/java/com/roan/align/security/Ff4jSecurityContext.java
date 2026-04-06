package com.roan.align.security;

import java.util.Set;

/**
 * Thread-local holder for FF4J security context.
 * Stores the current user's information during request processing.
 *
 * @author Roan
 * @date 2026/3/30
 */
public class Ff4jSecurityContext {

    private static final ThreadLocal<Ff4jUser> CURRENT_USER = new ThreadLocal<>();

    private Ff4jSecurityContext() {
    }

    public static void set(Ff4jUser user) {
        CURRENT_USER.set(user);
    }

    public static Ff4jUser get() {
        return CURRENT_USER.get();
    }

    public static void clear() {
        CURRENT_USER.remove();
    }

    /**
     * User context containing username and roles.
     */
    public static class Ff4jUser {
        private final String username;
        private final Set<String> roles;

        public Ff4jUser(String username, Set<String> roles) {
            this.username = username;
            this.roles = roles;
        }

        public String getUsername() {
            return username;
        }

        public Set<String> getRoles() {
            return roles;
        }
        
        public boolean hasRole(String role) {
            return roles != null && roles.contains(role);
        }
    }
}
