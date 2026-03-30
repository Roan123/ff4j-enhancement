package com.roan.align.config;

import com.roan.align.security.CustomFf4jAuthManager;
import org.ff4j.FF4j;
import org.ff4j.security.AuthorizationsManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * FF4J configuration with custom authorization manager.
 *
 * @author Roan
 * @date 2026/3/30 14:45
 */
@Configuration
public class FF4jConfig {

    @Bean
    public FF4j ff4j() {
        FF4j ff4j = new FF4j();
        ff4j.audit(true);
        // Set custom authorization manager for role-based access control
        ff4j.setAuthorizationsManager(new CustomFf4jAuthManager());
        return ff4j;
    }

    @Bean
    public AuthorizationsManager authorizationsManager() {
        return new CustomFf4jAuthManager();
    }
}
