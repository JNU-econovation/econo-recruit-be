package com.econovation.recruit.api.record.dto;

import com.econovation.recruitdomain.domains.record.domain.Record;
import java.util.List;
import java.util.Map;

public record FilteredRecordsWithScoresDto(
        List<Record> records,
        Map<String, Double> scoreMap
) {
}
