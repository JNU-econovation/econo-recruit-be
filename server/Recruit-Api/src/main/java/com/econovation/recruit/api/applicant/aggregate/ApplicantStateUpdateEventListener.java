package com.econovation.recruit.api.applicant.aggregate;

import com.econovation.recruit.api.applicant.state.support.PeriodCalculator;
import com.econovation.recruitdomain.domains.applicant.domain.MongoAnswer;
import com.econovation.recruitdomain.domains.applicant.domain.MongoAnswerAdaptor;
import com.econovation.recruitdomain.domains.applicant.event.aggregateevent.ApplicantStateUpdateEvent;
import com.econovation.recruitdomain.domains.applicant.event.domainevent.ApplicantStateEvents;
import lombok.RequiredArgsConstructor;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ApplicantStateUpdateEventListener {

    private final MongoAnswerAdaptor answerAdaptor;
    private final PeriodCalculator periodCalculator;

    @EventHandler
    @Transactional
    public String handle(ApplicantStateUpdateEvent event) {
        MongoAnswer answer = answerAdaptor.findById(event.getId()).get();
        ApplicantStateEvents command = ApplicantStateEvents.find(event.getAfterState());

        switch (command) {
            case PASS:
                answer.pass(periodCalculator.execute());
                break;
            case NON_PASS:
                answer.nonPass(periodCalculator.execute());
                break;
        }

        answerAdaptor.save(answer);
        return answer.getApplicantStateOrDefault().getPassState();
    }
}
