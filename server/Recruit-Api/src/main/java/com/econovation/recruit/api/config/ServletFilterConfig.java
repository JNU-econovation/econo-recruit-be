package com.econovation.recruit.api.config;

import javax.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
import org.springframework.web.filter.ForwardedHeaderFilter;
import org.springframework.web.filter.ShallowEtagHeaderFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.ResourceUrlEncodingFilter;

@Configuration
@RequiredArgsConstructor
@Profile({"prod", "staging", "dev"})
public class ServletFilterConfig implements WebMvcConfigurer {

    private final HttpContentCacheFilter httpContentCacheFilter;
    private final ForwardedHeaderFilter forwardedHeaderFilter;

    @Bean
    public FilterRegistrationBean securityFilterChain(
            @Qualifier(AbstractSecurityWebApplicationInitializer.DEFAULT_FILTER_NAME)
            Filter securityFilter) {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean(securityFilter);
        registrationBean.setOrder(Integer.MAX_VALUE - 5);
        registrationBean.setName(AbstractSecurityWebApplicationInitializer.DEFAULT_FILTER_NAME);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean setResourceUrlEncodingFilter() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(new ResourceUrlEncodingFilter());
        registrationBean.setOrder(Integer.MAX_VALUE - 4);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean setForwardedHeaderFilterOrder() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(forwardedHeaderFilter);
        registrationBean.setOrder(Integer.MAX_VALUE - 3);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean setHttpContentCacheFilterOrder() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(httpContentCacheFilter);
        registrationBean.setOrder(Integer.MAX_VALUE - 2);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<ShallowEtagHeaderFilter> shallowEtagHeaderFilter() {
        FilterRegistrationBean<ShallowEtagHeaderFilter> registrationBean
                = new FilterRegistrationBean<>(new ShallowEtagHeaderFilter());
        registrationBean.setOrder(Integer.MAX_VALUE - 1);
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }
}
