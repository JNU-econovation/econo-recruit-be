package com.econovation.recruit.api.applicant.dto;

import static com.econovation.recruitcommon.consts.RecruitStatic.PASS_STATE_KEY;

import com.econovation.recruitdomain.domains.applicant.domain.state.ApplicantState;
import com.econovation.recruitdomain.domains.applicant.exception.ApplicantWrongStateException;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

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
        if (result.get(PASS_STATE_KEY) instanceof ApplicantState applicantState) {
            return GetApplicantsStatusResponse.builder()
                    .field((String) result.get("field"))
                    .field1((String) result.get("field1"))
                    .field2((String) result.get("field2"))
                    .name((String) result.get("name"))
                    .id((String) result.get("id"))
                    .year((Integer) result.get("year"))
                    .state(applicantState)
                    .build();
        }
        throw ApplicantWrongStateException.wrongStatusException;
    }
}
