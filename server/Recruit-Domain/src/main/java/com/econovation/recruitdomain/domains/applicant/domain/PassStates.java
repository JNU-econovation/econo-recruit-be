package com.econovation.recruitdomain.domains.applicant.domain;

import com.econovation.recruitdomain.domains.applicant.exception.ApplicantWrongStateException;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum PassStates{

    NONPASSED("non-passed"){
        @Override
        public PassStates next(){ return PassStates.FIRSTPASSED; } // 불합격 상태를 1차 합격 상태로 변경

        @Override
        public PassStates prev(){ return this; } // 불합격 상태의 이전 상태는 없으므로 현재 상태 그대로 리턴
    },
    FIRSTPASSED("first-passed"){
        @Override public PassStates next(){ return PassStates.FINALPASSED; } // 1차 합격 상태의 다음 상태는 최종 합격 상태

        @Override
        public PassStates prev(){ return PassStates.NONPASSED; } // 1차 합격 상태의 이전 상태는 불합격 상태
    },
    FINALPASSED("final-passed"){
        @Override
        public PassStates next(){ return this; } // 최종 합격 상태의 다음 상태는 없으므로 현재 상태 그대로 리턴

        @Override
        public PassStates prev(){ return PassStates.FIRSTPASSED; } // 최종 합격 상태의 이전 상태는 1차 합격 상태
    };

    private String status;

    PassStates(String status){ this.status = status; }

    public abstract PassStates next();
    public abstract PassStates prev();

    public static PassStates findStatus(String status){
        return Arrays.stream(PassStates.values())
                .filter(s -> s.getStatus().equals(status))
                .findFirst()
                .orElseThrow(ApplicantWrongStateException::new);
    }

    @Override
    public String toString() {
        return this.status;
    }
}
