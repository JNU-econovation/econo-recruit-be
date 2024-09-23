package com.econovation.recruit.api.applicant.state.support;

import com.econovation.recruitdomain.domains.applicant.support.ApplicantStateManger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApplicantManager implements ApplicantStateManger {

    @Override
    public void update(String applicantId, String afterState) {}
}
