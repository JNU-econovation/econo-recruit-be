package com.econovation.recruit.api.card.handler;

import com.econovation.recruit.api.card.usecase.BoardRegisterUseCase;
import com.econovation.recruitdomain.domains.applicant.event.domainevent.ApplicantRegisterEvent;
import com.econovation.recruitdomain.domains.card.adaptor.CardAdaptor;
import com.econovation.recruitdomain.domains.card.domain.Card;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(
        name = "notification.slack.enabled",
        havingValue = "false",
        matchIfMissing = false)
// Slakc 알림 기능이 false일 경우에 Card 생성 이벤트 핸들러
public class ApplicantCardCreateEventHandler {
    private final CardAdaptor cardAdaptor;
    private final BoardRegisterUseCase boardRegisterUseCase;

    @Async
    @TransactionalEventListener(
            classes = ApplicantRegisterEvent.class,
            phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handle(ApplicantRegisterEvent applicantRegistEvent) {
        Card card = fromApplicantRegisterEvent(applicantRegistEvent);
        cardAdaptor.save(card);

        boardRegisterUseCase.createApplicantBoard(
                applicantRegistEvent.getApplicantId(),
                applicantRegistEvent.getHopeField(),
                card.getId());
    }

    private Card fromApplicantRegisterEvent(ApplicantRegisterEvent event) {
        String title = generateCardTitle(event);
        return Card.builder()
                .applicantId(event.getApplicantId())
                .title(title)
                .applicantId(event.getApplicantId())
                .content("")
                .build();
    }

    private String generateCardTitle(ApplicantRegisterEvent event) {
        return String.format(
                "[%s] %s", extractHopeField(event.getHopeField()), event.getUserName());
    }

    private String extractHopeField(String title) {
        String[] titleParts = title.split(" ");
        log.info("hopeField = {}", titleParts[0]);
        return titleParts[0];
    }

}
