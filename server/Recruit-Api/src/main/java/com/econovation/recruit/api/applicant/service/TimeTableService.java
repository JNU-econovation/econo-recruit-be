package com.econovation.recruit.api.applicant.service;

import static com.econovation.recruitcommon.consts.RecruitStatic.TIMETABLE_APPLICANT_FIELD;

import com.econovation.recruit.api.applicant.usecase.ApplicantQueryUseCase;
import com.econovation.recruit.api.applicant.usecase.TimeTableLoadUseCase;
import com.econovation.recruit.api.applicant.usecase.TimeTableRegisterUseCase;
import com.econovation.recruitdomain.domains.applicant.dto.TimeTableVo;
import com.econovation.recruitdomain.domains.timetable.domain.TimeTable;
import com.econovation.recruitdomain.domains.timetable.exception.TimeTableNotFoundException;
import com.econovation.recruitdomain.out.TimeTableLoadPort;
import com.econovation.recruitdomain.out.TimeTableRecordPort;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TimeTableService implements TimeTableRegisterUseCase, TimeTableLoadUseCase {
    private final TimeTableLoadPort timeTableLoadPort;
    private final TimeTableRecordPort timeTableRecordPort;
    private final ApplicantQueryUseCase applicantQueryUseCase;

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, List<TimeTableVo>>> findAll() {
        List<TimeTable> timeTables = timeTableLoadPort.findAll();
        return timeTables.stream()
                .collect(
                        Collectors.groupingBy(
                                TimeTable::getApplicantId,
                                Collectors.mapping(
                                        timeTable -> {
                                            return TimeTableVo.builder()
                                                    .startTime(timeTable.getStartTime())
                                                    .build();
                                        },
                                        Collectors.toList())))
                .entrySet()
                .stream()
                .map(
                        entry -> {
                            return Map.of(entry.getKey(), entry.getValue());
                        })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Integer> getTimeTableByApplicantId(String applicantId) {
        List<TimeTable> timeTableByApplicantId =
                timeTableLoadPort.getTimeTableByApplicantId(applicantId);
        if (!timeTableByApplicantId.isEmpty()) {
            return timeTableByApplicantId.stream().map(TimeTable::getStartTime).toList();
        }
        throw TimeTableNotFoundException.EXCEPTION;
    }

    @Override
    public List<TimeTable> getTimeTableByApplicantIdIn(List<String> applicantIds) {
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Integer, List<String>> findAllSimpleApplicantWithTimeTable() {
        List<TimeTable> timeTables = timeTableLoadPort.findAll();
        Map<String, Map<String, Object>> allApplicantVo =
                applicantQueryUseCase.findAllApplicantVo(TIMETABLE_APPLICANT_FIELD);
        // SimpleApplicant : {applicantId : {name(이름), field(지원분야) }
        return timeTables.stream()
                .collect(
                        Collectors.groupingBy(
                                TimeTable::getStartTime,
                                Collectors.mapping(
                                        timeTable -> {
                                            return "["
                                                    + allApplicantVo
                                                            .get(timeTable.getApplicantId())
                                                            .get("field")
                                                    + "] : "
                                                    + allApplicantVo
                                                            .get(timeTable.getApplicantId())
                                                            .get("name");
                                        },
                                        Collectors.toList())));
    }

    @Override
    public void execute(String applicantId, List<Integer> startTimes) {
        List<TimeTable> timeTableList = new LinkedList<>();
        for (Integer startTime : startTimes) {
            timeTableList.add(
                    TimeTable.builder().startTime(startTime).applicantId(applicantId).build());
        }
        timeTableRecordPort.saveAll(timeTableList);
    }
}
