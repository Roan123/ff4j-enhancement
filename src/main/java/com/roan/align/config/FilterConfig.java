package com.roan.align.config;

import com.roan.align.filter.FF4jConsoleScriptInjectionFilter;
import jakarta.servlet.Filter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Web configuration to register filters for FF4j console.
 *
 * @author Roan
 * @date 2026/4/2
 */
@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<Filter> ff4jConsoleScriptInjectionFilter() {
        FilterRegistrationBean<Filter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new FF4jConsoleScriptInjectionFilter());
        // Target FF4j console specifically
        registrationBean.addUrlPatterns("/ff4j-web-console/*");
        registrationBean.setName("ff4jConsoleScriptInjectionFilter");
        registrationBean.setOrder(1);
        return registrationBean;
    }
}
