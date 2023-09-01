package com.econovation.recruit.api.label.service;

import com.econovation.recruit.api.config.security.SecurityUtils;
import com.econovation.recruit.api.label.usecase.LabelUseCase;
import com.econovation.recruitdomain.common.aop.redissonLock.RedissonLock;
import com.econovation.recruitdomain.domains.card.domain.Card;
import com.econovation.recruitdomain.domains.interviewer.domain.Interviewer;
import com.econovation.recruitdomain.domains.label.domain.Label;
import com.econovation.recruitdomain.domains.label.exception.LabelNotFoundException;
import com.econovation.recruitdomain.out.CardLoadPort;
import com.econovation.recruitdomain.out.InterviewerLoadPort;
import com.econovation.recruitdomain.out.LabelLoadPort;
import com.econovation.recruitdomain.out.LabelRecordPort;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LabelService implements LabelUseCase {
    private final LabelRecordPort labelRecordPort;
    private final LabelLoadPort labelLoadPort;
    private final InterviewerLoadPort interviewerLoadPort;
    private final CardLoadPort cardLoadPort;

    @Override
    @RedissonLock(
            LockName = "라벨 생성",
            identifier = "applicantId",
            waitTime = 1000L,
            leaseTime = 1000L)
    @Transactional
    public Boolean createLabel(String applicantId) {
        Long idpId = SecurityUtils.getCurrentUserId();
        Card card = cardLoadPort.findByApplicantId(applicantId);
        Label label =
                Label.builder().idpId(idpId).applicantId(applicantId).cardId(card.getId()).build();
        //         라벨 중복 처리 : 라벨이 있으면 라벨을 삭제한다.
        Label label2 = labelLoadPort.loadLabelByApplicantIdAndIdpId(applicantId, idpId);
        if (label2 != null) {
            labelRecordPort.delete(label2);
            card.minusLabelCount();
            return false;
        }
        labelRecordPort.save(label);
        card.plusLabelCount();
        return true;
    }

    @Override
    public List<String> findByApplicantId(String applicantId) {
        List<Label> labels = labelLoadPort.loadLabelByApplicantId(applicantId);

        if (labels.isEmpty()) {
            throw LabelNotFoundException.EXCEPTION;
        }

        List<Long> idpIds = labels.stream().map(Label::getIdpId).collect(Collectors.toList());
        List<Interviewer> interviewers = interviewerLoadPort.loadInterviewerByIdpIds(idpIds);

        Map<Long, String> interviewerMap =
                interviewers.stream()
                        .collect(Collectors.toMap(Interviewer::getId, Interviewer::getName));

        return labels.stream()
                .map(label -> interviewerMap.get(label.getIdpId()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    @RedissonLock(
            LockName = "라벨 삭제",
            identifier = "applicantId",
            waitTime = 1000L,
            leaseTime = 1000L)
    public void deleteLabel(String applicantId) {
        Long idpId = SecurityUtils.getCurrentUserId();
        Label label = labelLoadPort.loadLabelByApplicantIdAndIdpId(applicantId, idpId);
        if (label == null) throw LabelNotFoundException.EXCEPTION;
        // Card LabelCount 감소
        cardLoadPort.findByApplicantId(applicantId).minusLabelCount();
        labelRecordPort.delete(label);
    }
}