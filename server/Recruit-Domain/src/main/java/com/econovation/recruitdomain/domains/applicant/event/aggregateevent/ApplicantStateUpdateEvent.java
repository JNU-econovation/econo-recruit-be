package com.econovation.recruitdomain.domains.applicant.event.aggregateevent;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ApplicantStateUpdateEvent {

    private String id;
    private String afterState;
}
