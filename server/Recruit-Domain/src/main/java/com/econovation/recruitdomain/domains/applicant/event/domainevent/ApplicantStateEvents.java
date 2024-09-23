package com.econovation.recruitdomain.domains.applicant.event.domainevent;

import com.econovation.recruitdomain.domains.applicant.exception.ApplicantWrongStateException;
import java.util.Arrays;
import lombok.Getter;

@Getter
public enum ApplicantStateEvents {
    NON_PASS("non-pass"),
    PASS("pass");

    private String event;

    ApplicantStateEvents(String event) {
        this.event = event;
    }

    public static ApplicantStateEvents find(String event) {
        return Arrays.stream(ApplicantStateEvents.values())
                .filter(e -> e.getEvent().equals(event))
                .findFirst()
                .orElseThrow(() -> ApplicantWrongStateException.wrongStatusException);
    }
}
