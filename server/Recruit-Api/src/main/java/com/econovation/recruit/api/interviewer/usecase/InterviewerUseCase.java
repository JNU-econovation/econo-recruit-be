package com.econovation.recruit.api.interviewer.usecase;

import com.econovation.recruitcommon.annotation.UseCase;
import com.econovation.recruitdomain.domains.dto.InterviewerResponseDto;
import com.econovation.recruitdomain.domains.interviewer.domain.Interviewer;
import java.util.List;

@UseCase
public interface InterviewerUseCase {
    List<Interviewer> createInterviewers(List<Long> idpIds);

    Interviewer getById(Long idpId);

    void updateRole(Long idpId, String role);

    void createInterviewersByName(List<String> names);

    List<InterviewerResponseDto> findAll();

    List<InterviewerResponseDto> findAll(String sortType, List<String> roles);

    void createTempInterviewers();

    InterviewerResponseDto findMe();

    void deleteInterviewer(Long idpId);
}
