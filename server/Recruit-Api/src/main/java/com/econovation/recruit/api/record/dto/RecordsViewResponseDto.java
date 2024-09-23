package com.econovation.recruit.api.record.dto;

import com.econovation.recruit.utils.vo.PageInfo;
import com.econovation.recruitdomain.domains.applicant.domain.MongoAnswer;
import com.econovation.recruitdomain.domains.record.domain.Record;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class RecordsViewResponseDto {
    private List<RecordViewResponseDto> records;
    private PageInfo pageInfo;

    public static RecordsViewResponseDto of(
            PageInfo pageInfo,
            List<Record> records,
            Map<String, Double> scores,
            List<MongoAnswer> applicants) {
        List<RecordViewResponseDto> recordViewResponseDtos =
                records.stream()
                        .map(
                                record -> {
                                    Double score = scores.getOrDefault(record.getApplicantId(), 0D);
                                    MongoAnswer applicant =
                                            applicants.stream()
                                                    .filter(
                                                            a ->
                                                                    a.getId()
                                                                            .equals(
                                                                                    record
                                                                                            .getApplicantId()))
                                                    .findFirst()
                                                    .get();
                                    return RecordViewResponseDto.from(record, score, applicant);
                                })
                        .toList();
        return RecordsViewResponseDto.builder()
                .pageInfo(pageInfo)
                .records(recordViewResponseDtos)
                .build();
    }

    public static RecordsViewResponseDto empty(PageInfo pageInfo) {
        return RecordsViewResponseDto.builder()
                .pageInfo(pageInfo)
                .records(List.of())
                .build();
    }
}
