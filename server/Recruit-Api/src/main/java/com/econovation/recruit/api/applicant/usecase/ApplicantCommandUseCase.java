package com.econovation.recruit.api.applicant.usecase;

import com.econovation.recruitcommon.annotation.UseCase;
import java.util.Map;
import java.util.UUID;

@UseCase
public interface ApplicantCommandUseCase {
    UUID execute(Map<String, Object> blocks);

    String execute(String applicantId, String state);
}
