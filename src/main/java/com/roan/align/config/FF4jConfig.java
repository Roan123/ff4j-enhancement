package com.roan.align.config;

import com.roan.align.security.CustomFf4jAuthManager;
import org.ff4j.FF4j;
import org.ff4j.security.AuthorizationsManager;
import org.ff4j.store.JdbcFeatureStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * FF4J configuration with JDBC persistence and custom authorization manager.
 *
 * @author Roan
 * @date 2026/3/30 14:45
 */
@Configuration
public class FF4jConfig {

    @Autowired
    private DataSource dataSource;

    @Bean
    public FF4j ff4j() {
        FF4j ff4j = new FF4j();
        
        // Configure JDBC feature store
        JdbcFeatureStore jdbcStore = new JdbcFeatureStore(dataSource);
        jdbcStore.createSchema(); // Create FF4J tables if not exist
        ff4j.setFeatureStore(jdbcStore);
        
        // Enable audit
        ff4j.audit(true);
        
        // Set custom authorization manager for role-based access control
        ff4j.setAuthorizationsManager(new CustomFf4jAuthManager(dataSource));
        
        return ff4j;
    }

    @Bean
    public AuthorizationsManager authorizationsManager(@Autowired DataSource dataSource) {
        return new CustomFf4jAuthManager(dataSource);
    }
}
