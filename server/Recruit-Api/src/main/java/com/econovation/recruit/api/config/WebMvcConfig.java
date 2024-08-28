package com.econovation.recruit.api.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.WebContentInterceptor;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    //    @Autowired private CookieFilter cookieFilter;
    // Filter에 포함되는 URL 주소
    private static final String[] INCLUDE_PATHS = {"/test/*", "/test2/*", "/*"};

    //    @Bean
    //    public FilterRegistrationBean filterBean() {
    //
    //        FilterRegistrationBean registrationBean = new FilterRegistrationBean(cookieFilter);
    //        registrationBean.setOrder(Integer.MIN_VALUE); // 필터 여러개 적용 시 순번
    //        //        registrationBean.addUrlPatterns("/*"); //전체 URL 포함
    //        //        registrationBean.addUrlPatterns("/test/*"); //특정 URL 포함
    //        registrationBean.setUrlPatterns(Arrays.asList(INCLUDE_PATHS)); // 여러 특정 URL 포함
    //        //        registrationBean.setUrlPatterns(Arrays.asList("/api/v1/*"));
    //        return registrationBean;3
    //    }

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        CacheControl cacheControl = CacheControl.noCache().mustRevalidate();

        WebContentInterceptor webContentInterceptor = new WebContentInterceptor();
        webContentInterceptor.addCacheMapping(cacheControl, "/**");

        registry.addInterceptor(webContentInterceptor);
    }
}
