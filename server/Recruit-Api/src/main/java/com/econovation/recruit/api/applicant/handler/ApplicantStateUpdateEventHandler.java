package com.econovation.recruit.api.applicant.handler;

import com.econovation.recruitdomain.domains.applicant.domain.MongoAnswer;
import com.econovation.recruitdomain.domains.applicant.domain.MongoAnswerAdaptor;
import com.econovation.recruitdomain.domains.applicant.event.domainevent.ApplicantRegisterEvent;
import com.econovation.recruitdomain.domains.applicant.event.domainevent.ApplicantStateEvents;
import com.econovation.recruitdomain.domains.applicant.event.domainevent.ApplicantStateModifyEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ApplicantStateUpdateEventHandler {

    private final MongoAnswerAdaptor answerAdaptor;

//    @Async
//    @TransactionalEventListener(
//            classes = ApplicantRegisterEvent.class,
//            phase = TransactionPhase.AFTER_COMMIT)
//    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String handle(ApplicantStateModifyEvent event){
        MongoAnswer answer = answerAdaptor.findById(event.getApplicantId()).get();
        ApplicantStateEvents command = event.getEvent();

        switch (command) {
            case PASS:
                answer.pass();
                break;
            case NON_PASS:
                answer.nonPass();
                break;
        }

        answerAdaptor.save(answer);
        return answer.getApplicantState().getPassState();
    }

}
