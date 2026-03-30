package com.roan.align.config;

import org.ff4j.FF4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.interfaces.PBEKey;

/**
 * @author Roan
 * @date 2026/3/30 14:45
 */
@Configuration
public class FF4jConfig {

    @Bean
    public FF4j ff4j(){
        FF4j ff4j = new FF4j();
        ff4j.audit(true);
        return ff4j;
    }
}
