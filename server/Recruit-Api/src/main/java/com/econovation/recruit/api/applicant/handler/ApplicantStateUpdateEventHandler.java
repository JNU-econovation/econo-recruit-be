package com.econovation.recruit.api.applicant.handler;

import com.econovation.recruit.api.applicant.state.support.PeriodCalculator;
import com.econovation.recruitdomain.domains.applicant.domain.MongoAnswer;
import com.econovation.recruitdomain.domains.applicant.domain.MongoAnswerAdaptor;
import com.econovation.recruitdomain.domains.applicant.event.domainevent.ApplicantStateEvents;
import com.econovation.recruitdomain.domains.applicant.event.domainevent.ApplicantStateModifyEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApplicantStateUpdateEventHandler {

    private final MongoAnswerAdaptor answerAdaptor;
    private final PeriodCalculator periodCalculator;

//    @Async
//    @TransactionalEventListener(
//            classes = ApplicantRegisterEvent.class,
//            phase = TransactionPhase.AFTER_COMMIT)
//    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String handle(ApplicantStateModifyEvent event){
        MongoAnswer answer = answerAdaptor.findById(event.getApplicantId()).get();
        ApplicantStateEvents command = event.getEvent();
        boolean result = answer.stateEmptyCheckAndInit();
        log.error(String.format("validate : %s", (result ? "새로운 state 초기화" : "state 초기화 하지 않고 변경")));
        switch (command) {
            case PASS:
                answer.pass(periodCalculator.execute());
                break;
            case NON_PASS:
                answer.nonPass(periodCalculator.execute());
                break;
        }

        answerAdaptor.save(answer);
        return answer.getApplicantState().getPassState();
    }

}
