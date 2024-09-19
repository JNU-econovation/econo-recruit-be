package com.econovation.recruitdomain.domains.applicant.event.domainevent;

import com.econovation.recruitdomain.common.aop.domainEvent.DomainEvent;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@Builder
public class ApplicantStateModifyEvent extends DomainEvent {

    private final String applicantId;
    private final ApplicantStateEvents event;

    public static ApplicantStateModifyEvent of(String applicantId, String eventName){
        return ApplicantStateModifyEvent.builder()
                .applicantId(applicantId)
                .event(ApplicantStateEvents.find(eventName))
                .build();
    }

}
