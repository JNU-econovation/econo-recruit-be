package com.econovation.recruit;

import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.filter.ForwardedHeaderFilter;

@SpringBootApplication
@RequiredArgsConstructor
@EnableScheduling
@ComponentScan(basePackages = {"com.econovation"})
@Slf4j
public class RecruitApplication implements ApplicationListener<ApplicationReadyEvent> {
    private final Environment environment;

    public static void main(String[] args) {
        SpringApplication.run(RecruitApplication.class, args);
    }

    @Bean
    ForwardedHeaderFilter forwardedHeaderFilter() {
        return new ForwardedHeaderFilter();
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("applicationReady status" + Arrays.toString(environment.getActiveProfiles()));
    }
}
