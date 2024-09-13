package com.econovation.recruitdomain.domains.applicant.domain.state;

import com.econovation.recruitdomain.domains.applicant.exception.ApplicantWrongStateException;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum PassStates{

    NON_PROCESSED("non-processed"){
        @Override
        public PassStates pass(PeriodStates period){
            if(period.equals(PeriodStates.FIRST_DISCUSSION)) return PassStates.FIRST_PASSED;
            else return this;
        }

        @Override
        public PassStates nonPass(PeriodStates period){
            if(period.equals(PeriodStates.FIRST_DISCUSSION)) return PassStates.FIRST_FAILED;
            else return this;
        }
    },
    FIRST_PASSED("first-passed"){
        @Override
        public PassStates pass(PeriodStates period) {
            if(period.equals(PeriodStates.FIRST_DISCUSSION)) return this;
            else if(period.equals(PeriodStates.FINAL_DISCUSSION)) return PassStates.FINAL_PASSED;
            else return PassStates.FINAL_FAILED;
        }

        @Override
        public PassStates nonPass(PeriodStates period) {
            if(period.equals(PeriodStates.FIRST_DISCUSSION)) return PassStates.FIRST_FAILED;
            else if(period.equals(PeriodStates.FINAL_DISCUSSION)) return PassStates.FINAL_FAILED;
            else return this;
        }
    },
    FIRST_FAILED("first-failed"){
        @Override
        public PassStates pass(PeriodStates period) {
            if(period.equals(PeriodStates.FIRST_DISCUSSION)) return PassStates.FIRST_PASSED;
            else return this;
        }

        @Override
        public PassStates nonPass(PeriodStates period) {
            return this;
        }
    },
    FINAL_PASSED("final-passed"){
        @Override
        public PassStates pass(PeriodStates period) {
            return this;
        }

        @Override
        public PassStates nonPass(PeriodStates period) {
            if(period.equals(PeriodStates.FIRST_DISCUSSION)) return this; // 1차 합격자 논의 기간에 최종합격 상태에 대한 요청이 있으면 에러를..?
            else if(period.equals(PeriodStates.FINAL_DISCUSSION)) return PassStates.FIRST_PASSED;
            else return this;
        }
    },
    FINAL_FAILED("final-failed"){
        @Override
        public PassStates pass(PeriodStates period) {
            if(period.equals(PeriodStates.FIRST_DISCUSSION)) return this;
            else if(period.equals(PeriodStates.FINAL_DISCUSSION)) return PassStates.FIRST_PASSED;
            else return this;
        }

        @Override
        public PassStates nonPass(PeriodStates period) {
            return this;
        }
    };

    private final String state;

    PassStates(String state){ this.state = state; }

    public abstract PassStates pass(PeriodStates period);
    public abstract PassStates nonPass(PeriodStates period);

    public static PassStates findStatus(String state){
        return Arrays.stream(PassStates.values())
                .filter(s -> s.getState().equals(state))
                .findFirst()
                .orElseThrow(ApplicantWrongStateException::new);
    }

    @Override
    public String toString() {
        if (this==FIRST_FAILED || this==FINAL_FAILED) return "non-passed";
        return this.state;
    }
}
