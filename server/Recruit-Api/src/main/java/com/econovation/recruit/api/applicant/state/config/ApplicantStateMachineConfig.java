package com.econovation.recruit.api.applicant.state.config;

import com.econovation.recruitdomain.domains.applicant.domain.state.PassStates;
import com.econovation.recruitdomain.domains.applicant.event.domainevent.ApplicantStateEvents;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

@Configuration
@EnableStateMachine
public class ApplicantStateMachineConfig extends EnumStateMachineConfigurerAdapter<PassStates, ApplicantStateEvents> {


    @Override
    public void configure(StateMachineStateConfigurer<PassStates, ApplicantStateEvents> states) throws Exception {
        states
                .withStates()
                        .initial(PassStates.NON_PROCESSED)
                        .state(PassStates.FIRST_PASSED)
                        .end(PassStates.FIRST_FAILED)
                        .end(PassStates.FINAL_PASSED)
                        .end(PassStates.FINAL_FAILED);

    }

    /**
     *
     * @param transitions
     * @throws Exception
     *
     * States
         * NON_PROCESSED : 불합격
         * FIRST_PASSED : 1차 합격
         * FIRST_FAILED : 1차 불합격
         * FINAL_PASSED : 최종 합격
         * FINAL_FAILED : 최종 불합격
     *
     * Events
         * NON_PASS : 불합격
         * PASS : 합격 (다음 단계로 전환)
     */

    @Override
    public void configure(StateMachineTransitionConfigurer<PassStates, ApplicantStateEvents> transitions) throws Exception {
        transitions
                .withExternal()
                    .source(PassStates.NON_PROCESSED).target(PassStates.FIRST_PASSED).event(ApplicantStateEvents.PASS)
                .and()
                .withExternal()
                    .source(PassStates.FIRST_PASSED).target(PassStates.FINAL_PASSED).event(ApplicantStateEvents.PASS)
                .and()
                .withExternal()
                    .source(PassStates.FIRST_PASSED).target(PassStates.FINAL_FAILED).event(ApplicantStateEvents.NON_PASS);
    }
}
