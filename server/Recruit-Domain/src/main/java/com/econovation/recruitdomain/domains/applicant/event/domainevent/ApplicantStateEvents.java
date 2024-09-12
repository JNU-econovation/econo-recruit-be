package com.econovation.recruitdomain.domains.applicant.event.domainevent;

import com.econovation.recruitdomain.common.aop.domainEvent.DomainEvent;
import com.econovation.recruitdomain.domains.applicant.exception.ApplicantWrongStateException;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ApplicantStateEvents {

    NON_PASS("non-pass"),
    PASS("pass");


    private String event;

    ApplicantStateEvents(String event){ this.event = event; }


    public static ApplicantStateEvents find(String event){
        return Arrays.stream(ApplicantStateEvents.values())
                .filter(e -> e.getEvent().equals(event))
                .findFirst()
                .orElseThrow(ApplicantWrongStateException::new);
    }
}
