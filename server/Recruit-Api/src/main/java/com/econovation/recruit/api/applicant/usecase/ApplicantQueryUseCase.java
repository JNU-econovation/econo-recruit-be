package com.econovation.recruit.api.applicant.usecase;

import com.econovation.recruit.api.applicant.dto.AnswersResponseDto;
import com.econovation.recruit.api.applicant.dto.GetApplicantsStatusResponse;
import com.econovation.recruitcommon.annotation.UseCase;
import com.econovation.recruitdomain.domains.applicant.domain.MongoAnswer;
import java.util.List;
import java.util.Map;

@UseCase
public interface ApplicantQueryUseCase {
    Map<String, Object> execute(String applicantId);

    AnswersResponseDto execute(Integer year, Integer page, String order);

    List<Map<String, Object>> execute();

    Map<String, Object> execute(String applicantId, List<String> fields);

    List<Map<String, Object>> execute(List<String> fields, Integer page);

    List<Map<String, Object>> execute(
            List<String> fields, Integer year, Integer page, String sortedType);

    List<MongoAnswer> execute(List<String> applicantIds);

    Map<String, Map<String, Object>> findAllApplicantVo(List<String> fields);

    AnswersResponseDto search(Integer page, String searchKeyword);

    List<GetApplicantsStatusResponse> getApplicantsStatus(Integer year, String sortType);
}
