package com.econovation.recruit.api.record.dto;

import com.econovation.recruitdomain.domains.applicant.domain.MongoAnswer;
import com.econovation.recruitdomain.domains.applicant.domain.state.ApplicantState;
import com.econovation.recruitdomain.domains.record.domain.Record;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RecordViewResponseDto {
    private String applicantId;
    private Double scores;
    private String name;
    private String url;
    private String record;
    private String field1;
    private String field2;
    private String grade;
    private String semester;
    private String modifiedAt;
    private ApplicantState state;

    public static RecordViewResponseDto from(Record recordVo, Double score, MongoAnswer applicant) {
        String name =
                "["
                        + applicant.getQna().get("field").toString()
                        + "] "
                        + applicant.getQna().get("name").toString();
        return new RecordViewResponseDto(
                recordVo.getApplicantId(),
                score,
                name,
                recordVo.getUrl(),
                recordVo.getRecord(),
                applicant.getQna().get("field1").toString(),
                applicant.getQna().get("field2").toString(),
                applicant.getQna().get("grade").toString(),
                applicant.getQna().get("semester").toString(),
                recordVo.getUpdatedAt().toString(),
                applicant.getApplicantState());
    }
}
