package com.econovation.recruit.api.config.security;

import static com.econovation.recruitcommon.consts.RecruitStatic.RolePattern;
import static com.econovation.recruitcommon.consts.RecruitStatic.SwaggerPatterns;

import com.econovation.recruitcommon.helper.SpringEnvironmentHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;

@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {
    private final FilterConfig filterConfig;

    @Value("${swagger.user}")
    private String swaggerUser;

    @Value("${swagger.password}")
    private String swaggerPassword;

    private final SpringEnvironmentHelper springEnvironmentHelper;

    /** 스웨거용 인메모리 유저 설정 */
    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        UserDetails user =
                User.withUsername(swaggerUser)
                        .password(passwordEncoder().encode(swaggerPassword))
                        .roles("SWAGGER")
                        .build();
        return new InMemoryUserDetailsManager(user);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(8);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.formLogin().disable().cors().and().csrf().disable();

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.authorizeRequests().expressionHandler(expressionHandler());

        // 베이직 시큐리티 설정
        // 베이직 시큐리티는 ExceptionTranslationFilter 에서 authenticationEntryPoint 에서
        // commence 로 401 넘겨줌. -> 응답 헤더에 www-authenticate 로 인증하라는 응답줌.
        // 브라우저가 basic auth 실행 시켜줌.
        // 개발 환경에서만 스웨거 비밀번호 미설정.
        if (springEnvironmentHelper.isProdProfile()) {
            http.authorizeRequests().mvcMatchers(SwaggerPatterns).authenticated().and().httpBasic();
        }

        http.authorizeRequests()
                // 면접관 삭제는 회장단 이상부터 가능합니다.
                //                .mvcMatchers("/**")
                //                .permitAll()
                // 스웨거용 인메모리 유저의 권한은 SWAGGER 이다
                // 따라서 스웨거용 인메모리 유저가 basic auth 필터를 통과해서 들어오더라도
                // ( jwt 필터나 , basic auth 필터의 순서는 상관이없다.) --> 왜냐면 jwt는 토큰 여부 파악만하고 있으면 검증이고 없으면 넘김.
                // 내부 소스까지 실행을 못함. 권한 문제 때문에.
                .mvcMatchers(HttpMethod.DELETE, "/api/v1//interviewers/*")
                .hasAnyRole("ROLE_OPERATION", "ROLE_PRESIDENT")
                .mvcMatchers(HttpMethod.PATCH, "/api/v1/applicants/{applicant-id}/status")
                .hasAnyRole("ROLE_OPERATION", "ROLE_PRESIDENT")
                .anyRequest()
                .hasAnyRole(RolePattern);

        http.apply(filterConfig);

        return http.build();
    }

    @Bean
    public RoleHierarchyImpl roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy(
                "ROLE_OPERATION > ROLE_PRESIDENT > ROLE_TF > ROLE_SWAGGER > ROLE_GUEST");
        return roleHierarchy;
    }

    @Bean
    public DefaultWebSecurityExpressionHandler expressionHandler() {
        DefaultWebSecurityExpressionHandler expressionHandler =
                new DefaultWebSecurityExpressionHandler();
        expressionHandler.setRoleHierarchy(roleHierarchy());
        return expressionHandler;
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web ->
                web.ignoring()
                        .antMatchers(SwaggerPatterns)
                        .antMatchers(
                                HttpMethod.POST,
                                "/api/v1/applicants/mail",
                                "/api/v1/applicants",
                                "/api/v1/timetables",
                                "/api/v1/applicants/*/timetables",
                                "/api/v1/questions",
                                "/api/v1/applicants",
                                "/api/v1/signup",
                                "/api/v1/token/refresh",
                                "/api/v1/login",
                                "/api/v1/register")
                        .antMatchers(
                                HttpMethod.GET,
                                "/api/v1/applicants",
                                "/api/v1/token",
                                "/api/v1/timetables",
                                "/api/v1/applicants/*/timetables")
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }
}
