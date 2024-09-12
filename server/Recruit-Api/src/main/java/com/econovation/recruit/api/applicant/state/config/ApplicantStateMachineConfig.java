package com.econovation.recruit.api.applicant.state.config;

import com.econovation.recruitdomain.domains.applicant.domain.PassStates;
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
                        .initial(PassStates.NONPASSED)
                        .state(PassStates.FIRSTPASSED)
                        .end(PassStates.FINALPASSED);
    }

    /**
     *
     * @param transitions
     * @throws Exception
     *
     * States
         * NONPASSED : 불합격
         * FIRSTPASSED : 1차 합격
         * FINALPASSED : 최종 합격
     *
     * Events
         * NON_PASS : 불합격
         * PASS : 합격 (다음 단계로 전환)
     */

    @Override
    public void configure(StateMachineTransitionConfigurer<PassStates, ApplicantStateEvents> transitions) throws Exception {
        transitions
                .withExternal()
                    .source(PassStates.NONPASSED).target(PassStates.FIRSTPASSED).event(ApplicantStateEvents.PASS)
                .and()
                .withExternal()
                    .source(PassStates.FIRSTPASSED).target(PassStates.FINALPASSED).event(ApplicantStateEvents.PASS)
                .and()
                .withExternal()
                    .source(PassStates.FIRSTPASSED).target(PassStates.NONPASSED).event(ApplicantStateEvents.NON_PASS);
    }
}
