package com.econovation.recruit.api.applicant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

import static com.econovation.recruitcommon.consts.RecruitStatic.PASS_STATE_KEY;

@Data
@AllArgsConstructor
@Builder
public class GetApplicantsStatusResponse {
    private String field1;
    private String field2;
    private String field3;
    private String name;
    private String id;
    private Integer year;
    private String state;

    public static GetApplicantsStatusResponse of(Map<String,Object> result) {
        return GetApplicantsStatusResponse.builder()
                .field1((String) result.get("field1"))
                .field2((String) result.get("field2"))
                .field3((String) result.get("field3"))
                .name((String) result.get("name"))
                .id((String) result.get("id"))
                .year((Integer) result.get("year"))
                .state(result.get(PASS_STATE_KEY).toString())
                .build();
    }
}
