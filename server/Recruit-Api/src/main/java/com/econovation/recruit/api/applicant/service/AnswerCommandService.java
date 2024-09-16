package com.econovation.recruit.api.applicant.service;

import com.econovation.recruit.api.applicant.handler.ApplicantStateUpdateEventHandler;
import com.econovation.recruit.api.applicant.usecase.ApplicantCommandUseCase;
import com.econovation.recruitdomain.common.aop.domainEvent.Events;
import com.econovation.recruitdomain.domains.applicant.domain.state.ApplicantState;
import com.econovation.recruitdomain.domains.applicant.domain.MongoAnswer;
import com.econovation.recruitdomain.domains.applicant.domain.MongoAnswerAdaptor;
import com.econovation.recruitdomain.domains.applicant.event.domainevent.ApplicantRegisterEvent;
import java.util.Map;
import java.util.UUID;

import com.econovation.recruitdomain.domains.applicant.event.domainevent.ApplicantStateModifyEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AnswerCommandService implements ApplicantCommandUseCase {
    private final MongoAnswerAdaptor answerAdaptor;
    private final ApplicantStateUpdateEventHandler applicantStateUpdateEventHandler;

    @Value("${econovation.year}")
    private Integer year;

    @Override
    @Transactional
    public UUID execute(Map<String, Object> qna) {
        UUID id = UUID.randomUUID();
        execute(qna, id);
        return id;
    }

    @Override
    @Transactional
    public String execute(String applicantId, String afterState) {
        ApplicantStateModifyEvent stateModifyEventEvents =
                ApplicantStateModifyEvent.of(applicantId, afterState);
        return applicantStateUpdateEventHandler.handle(stateModifyEventEvents); // 동기로 처리
    }

    @Override
    public UUID execute(Map<String, Object> qna, UUID id) {
        ApplicantState nonProcessed = new ApplicantState();
        MongoAnswer answer = MongoAnswer.builder().id(id.toString()).qna(qna).year(year).applicantState(nonProcessed).build();
        //        학번으로 중복 체크
        //        validateRegisterApplicant(qna);
        answerAdaptor.save(answer);

        String name = qna.get("name").toString();
        String hopeField = qna.get("field").toString();
        String email = qna.get("email").toString();

        ApplicantRegisterEvent applicantRegisterEvent =
                ApplicantRegisterEvent.of(answer.getId(), name, hopeField, email);
        Events.raise(applicantRegisterEvent);
        return null;
    }
}
