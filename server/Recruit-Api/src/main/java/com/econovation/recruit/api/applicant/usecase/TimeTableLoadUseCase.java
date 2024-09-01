package com.econovation.recruit.api.applicant.usecase;

import com.econovation.recruitcommon.annotation.UseCase;
import com.econovation.recruitdomain.domains.applicant.dto.TimeTableVo;
import com.econovation.recruitdomain.domains.timetable.domain.TimeTable;
import java.util.List;
import java.util.Map;

@UseCase
public interface TimeTableLoadUseCase {
    List<Map<String, List<TimeTableVo>>> findAll();

    List<Integer> getTimeTableByApplicantId(String applicantId);

    List<TimeTable> getTimeTableByApplicantIdIn(List<String> applicantIds);

    Map<Integer, List<String>> findAllSimpleApplicantWithTimeTable();
}
