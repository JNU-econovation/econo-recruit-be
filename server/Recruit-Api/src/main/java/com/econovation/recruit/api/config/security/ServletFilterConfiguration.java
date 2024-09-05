package com.econovation.recruit.api.config.security;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class ServletFilterConfiguration {
    @Bean
    public FilterRegistrationBean<JwtTokenFilter> registrationJwtTokenFileter(
            JwtTokenFilter filter) {
        FilterRegistrationBean<JwtTokenFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }

    @Bean
    public FilterRegistrationBean<JwtExceptionFilter> registrationJwtExceptionFilter(
            JwtExceptionFilter filter) {
        FilterRegistrationBean<JwtExceptionFilter> registration =
                new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }
}
