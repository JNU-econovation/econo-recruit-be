package com.econovation.recruit.api.applicant.dto;

import com.econovation.recruitdomain.domains.applicant.domain.state.ApplicantState;
import com.econovation.recruitdomain.domains.applicant.exception.ApplicantWrongStateException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

import static com.econovation.recruitcommon.consts.RecruitStatic.PASS_STATE_KEY;

@Data
@AllArgsConstructor
@Builder
public class GetApplicantsStatusResponse {
    private String field;
    private String field1;
    private String field2;
    private String name;
    private String id;
    private Integer year;
    private ApplicantState state;

    public static GetApplicantsStatusResponse of(Map<String, Object> result) {
        ApplicantState state;
        if (result.get(PASS_STATE_KEY) instanceof ApplicantState applicantState) {
            state = applicantState;
        } else {
            throw ApplicantWrongStateException.wrongStatusException;
        }
        return GetApplicantsStatusResponse.builder()
                .field((String) result.get("field"))
                .field1((String) result.get("field1"))
                .field2((String) result.get("field2"))
                .name((String) result.get("name"))
                .id((String) result.get("id"))
                .year((Integer) result.get("year"))
                .state(state)
                .build();
    }
}
