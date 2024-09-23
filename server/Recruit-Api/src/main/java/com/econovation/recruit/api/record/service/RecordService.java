package com.econovation.recruit.api.record.service;

import static com.econovation.recruit.utils.sort.SortHelper.paginateList;

import com.econovation.recruit.api.applicant.usecase.ApplicantQueryUseCase;
import com.econovation.recruit.api.record.dto.RecordsViewResponseDto;
import com.econovation.recruit.api.record.usecase.RecordUseCase;
import com.econovation.recruit.utils.sort.SortHelper;
import com.econovation.recruit.utils.vo.PageInfo;
import com.econovation.recruitdomain.domains.applicant.domain.MongoAnswer;
import com.econovation.recruitdomain.domains.applicant.exception.ApplicantNotFoundException;
import com.econovation.recruitdomain.domains.dto.CreateRecordDto;
import com.econovation.recruitdomain.domains.dto.UpdateRecordDto;
import com.econovation.recruitdomain.domains.record.domain.Record;
import com.econovation.recruitdomain.domains.record.exception.RecordDuplicateCreatedException;
import com.econovation.recruitdomain.domains.record.exception.RecordNotFoundException;
import com.econovation.recruitdomain.domains.score.domain.Score;
import com.econovation.recruitdomain.out.RecordLoadPort;
import com.econovation.recruitdomain.out.RecordRecordPort;
import com.econovation.recruitdomain.out.ScoreLoadPort;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RecordService implements RecordUseCase {

    private final RecordRecordPort recordRecordPort;
    private final RecordLoadPort recordLoadPort;
    private final ScoreLoadPort scoreLoadPort;
    private final ApplicantQueryUseCase applicantQueryUseCase;
    private final SortHelper<Record> sortHelper;

    @Override
    @Transactional
    public Record createRecord(CreateRecordDto recordDto) {
        if (applicantQueryUseCase.execute(recordDto.getApplicantId()) == null) {
            throw ApplicantNotFoundException.EXCEPTION;
        }
        if (recordLoadPort.findByApplicantId(recordDto.getApplicantId()).isPresent()) {
            throw RecordDuplicateCreatedException.EXCEPTION;
        }
        Record record = CreateRecordDto.toRecord(recordDto);
        return recordRecordPort.save(record);
    }

    @Override
    public List<Record> findAll() {
        return recordLoadPort.findAll();
    }

    /**
     * 1. Newest // 시간 순 오름차순 2. Name // 이름순 오름차순 3. Object // 지원 분야별 오름차순 4. Score // 점수 내림차순
     *
     * @return List<RecordResponseDto> // 지원자의 면접기록을 페이지별로 조회합니다 ( 이 화면에서는 Applicants,Scores, Records를 모두 조회합니다 )
     */
    @Override
    public RecordsViewResponseDto execute(Integer page, Integer year, String sortType) {
        List<Record> result = recordLoadPort.findAll(page);
        PageInfo pageInfo = getPageInfo(page);

        List<String> applicantIds = result.stream().map(Record::getApplicantId).toList();
        List<MongoAnswer> applicants = applicantQueryUseCase.execute(applicantIds).stream()
                .filter(applicant -> year == null || applicant.getYear().equals(year)).toList();

        if (result.isEmpty() || applicants.isEmpty()) {
            return createEmptyResponse(pageInfo);
        }

        Map<String, Integer> yearByAnswerIdMap = applicants.stream().collect(Collectors.toMap(MongoAnswer::getId, MongoAnswer::getYear));
        Map<String, Double> scoreMap = getScoreMap(year, applicantIds, yearByAnswerIdMap);

        result = result.stream().filter(record -> year == null ||
                        Optional.ofNullable(record.getApplicantId())
                                .map(yearByAnswerIdMap::get)
                                .map(y -> y.equals(year))
                                .orElse(false)
                )
                .toList();

        applicants = new ArrayList<>(applicants); // Unmodifiable List일 경우 Sort 불가. stream().toList()의 결과는 Unmodifiable List

        List<Record> records;
        if (sortType.equals("score")) {
            records = sortRecordsByScoresDesc(result, scoreMap);
        } else {
            records = sortRecordsByApplicantsAndSortType(result, applicants, sortType);
        }

        return RecordsViewResponseDto.of(pageInfo, records, scoreMap, applicants);
    }

    @Override
    public RecordsViewResponseDto execute(Integer page, Integer year, String sortType, String searchKeyword) {
        List<Record> result = recordLoadPort.findAll();
        List<String> applicantIds = result.stream().map(Record::getApplicantId).toList();

        List<MongoAnswer> applicants;
        if (sortType.equals("score")) {
            applicants = applicantQueryUseCase.execute(year, sortType, searchKeyword, applicantIds);
        } else {
            applicants = applicantQueryUseCase.execute(page, year, sortType, searchKeyword, applicantIds);
        }

        if (result.isEmpty() || applicants.isEmpty()) {
            return createEmptyResponse(new PageInfo(0, page));
        }

        Map<String, Integer> yearByAnswerIdMap = applicants.stream().collect(Collectors.toMap(MongoAnswer::getId, MongoAnswer::getYear));

        applicantIds = applicants.stream().map(MongoAnswer::getId).toList();    // 검색 결과에 따라 applicantIds 재할당
        Map<String, Double> scoreMap = getScoreMap(year, applicantIds, yearByAnswerIdMap);

        result = result.stream().filter(record -> year == null ||
                        Optional.ofNullable(record.getApplicantId())
                                .map(yearByAnswerIdMap::get)
                                .map(y -> y.equals(year))
                                .orElse(false)
                )
                .toList();

        List<Record> records;
        if (sortType.equals("score")) {
            records = sortRecordsByScoresDesc(result, scoreMap, page);
        } else {
            records = sortRecordsByApplicantsAndSortType(result, applicants);
        }

        PageInfo pageInfo = applicantQueryUseCase.getPageInfo(page, year, searchKeyword, applicantIds);
        return RecordsViewResponseDto.of(pageInfo, records, scoreMap, applicants);
    }

    private RecordsViewResponseDto createEmptyResponse(PageInfo pageInfo) {
        return RecordsViewResponseDto.of(
                pageInfo,
                Collections.emptyList(),
                Collections.emptyMap(),
                Collections.emptyList());
    }

    private Map<String, Double> getScoreMap(Integer year, List<String> applicantIds, Map<String, Integer> yearByAnswerIdMap) {
        List<Score> scores = scoreLoadPort.findByApplicantIds(applicantIds);
        return scores.stream()
                .filter(score -> year == null || yearByAnswerIdMap.get(score.getApplicantId()).equals(year))
                .collect(
                        Collectors.groupingBy(
                                Score::getApplicantId,
                                Collectors.averagingDouble(Score::getScore)));
    }

    private List<Record> sortRecordsByScoresDesc(
            List<Record> records, Map<String, Double> scoreMap) {
        // score 내림차순 정렬
        List<Record> sortedRecords = records.stream()
                .sorted(
                        Comparator.comparing(
                                record -> {
                                    Double score = scoreMap.get(record.getApplicantId());
                                    return score == null ? 0 : score;
                                }))
                .toList();
        return sortedRecords;
    }

    private List<Record> sortRecordsByScoresDesc(
            List<Record> records, Map<String, Double> scoreMap, Integer page) {
        // score 내림차순 정렬
        List<Record> sortedRecords = records.stream()
                .sorted(
                        Comparator.comparing(
                                record -> scoreMap.getOrDefault(record.getApplicantId(), 0.0),
                                Comparator.reverseOrder()
                        )
                )
                .toList();
        // 페이징 함수 호출
        return paginateList(sortedRecords, page);
    }

    private List<Record> sortRecordsByApplicantsAndSortType(
            List<Record> records, List<MongoAnswer> applicants, String sortType) {
        // Newest, Name, Object 정렬
        if (!applicants.isEmpty()) {
            sortHelper.sort(applicants, sortType);
        }
        Map<String, Integer> applicantIndexMap = new HashMap<>();
        for (int i = 0; i < applicants.size(); i++) {
            applicantIndexMap.put(applicants.get(i).getId(), i);
        }

        return records.stream()
                .sorted(
                        Comparator.comparing(
                                record ->
                                        applicantIndexMap.getOrDefault(
                                                record.getApplicantId(), Integer.MAX_VALUE)))
                .toList();
    }

    private List<Record> sortRecordsByApplicantsAndSortType(List<Record> records, List<MongoAnswer> applicants) {

        Map<String, Integer> applicantIndexMap = new HashMap<>();
        for (int i = 0; i < applicants.size(); i++) {
            applicantIndexMap.put(applicants.get(i).getId(), i);
        }

        return records.stream()
                .sorted(
                        Comparator.comparing(
                                record ->
                                        applicantIndexMap.getOrDefault(
                                                record.getApplicantId(), Integer.MAX_VALUE)))
                .toList();
    }

    private PageInfo getPageInfo(Integer page) {
        long totalCount = recordLoadPort.getTotalCount();
        return new PageInfo(totalCount, page);
    }

    @Override
    public Record findByApplicantId(String applicantId) {
        Optional<Record> byApplicantId = recordLoadPort.findByApplicantId(applicantId);
        return byApplicantId.orElseThrow(() -> RecordNotFoundException.EXCEPTION);
    }

    @Override
    @Transactional
    public void updateRecordUrl(String applicantId, String url) {
        recordLoadPort
                .findByApplicantId(applicantId)
                .ifPresent(
                        record -> record.updateUrl(url));
    }

    @Override
    @Transactional
    public void updateRecordContents(String applicantId, String contents) {
        recordLoadPort
                .findByApplicantId(applicantId)
                .ifPresent(
                        record -> record.updateRecord(contents));
    }

    @Override
    @Transactional
    public void updateRecord(String applicantId, UpdateRecordDto updateRecordDto) {
        recordLoadPort
                .findByApplicantId(applicantId)
                .ifPresent(
                        record -> {
                            if (updateRecordDto.getUrl() != null) {
                                record.updateUrl(updateRecordDto.getUrl());
                            }
                            if (updateRecordDto.getRecord() != null) {
                                record.updateRecord(updateRecordDto.getRecord());
                            }
                        });
    }
}
