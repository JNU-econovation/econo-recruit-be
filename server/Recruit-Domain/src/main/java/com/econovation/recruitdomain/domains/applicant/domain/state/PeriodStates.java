package com.econovation.recruitdomain.domains.applicant.domain.state;

import lombok.Getter;

@Getter
public enum PeriodStates {
    FIRST_DISCUSSION("first-discussion"),
    FINAL_DISCUSSION("final-discussion"),
    END("end");

    private final String state;

    PeriodStates(String state) {
        this.state = state;
    }
}
