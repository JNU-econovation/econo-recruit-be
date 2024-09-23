package com.econovation.recruit.api.applicant.aggregate;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

import com.econovation.recruit.api.applicant.command.CreateAnswerCommand;
import com.econovation.recruit.api.applicant.command.UpdateApplicantStateCommand;
import com.econovation.recruitdomain.domains.applicant.domain.MongoAnswer;
import com.econovation.recruitdomain.domains.applicant.event.aggregateevent.AnswerCreatedEvent;
import com.econovation.recruitdomain.domains.applicant.event.aggregateevent.ApplicantStateUpdateEvent;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

@AllArgsConstructor
@Getter
@Aggregate
@Slf4j
@NoArgsConstructor
public class AnswerAggregate {

    @AggregateIdentifier private String id;
    private Integer year;
    private Map<String, Object> qna;
    // Constructor for creating an AnswerAggregate
    @CommandHandler
    public AnswerAggregate(CreateAnswerCommand command) {
        apply(new AnswerCreatedEvent(command.getId(), command.getYear(), command.getQna()));
    }

    @CommandHandler
    public AnswerAggregate(UpdateApplicantStateCommand command) {
        apply(new ApplicantStateUpdateEvent(command.getId(), command.getAfterState()));
    }

    // Event handler for AnswerCreatedEvent
    @EventSourcingHandler
    public void on(AnswerCreatedEvent event) {
        this.id = event.getId();
        this.year = event.getYear();
        this.qna = event.getQna();
    }

    @EventSourcingHandler
    public void on(ApplicantStateUpdateEvent event) {
        this.id = event.getId();
        log.info("ApplicantID : " + event.getId());
        log.info("상태 변경 : " + event.getAfterState());
    }

    public static AnswerAggregate from(MongoAnswer answer) {
        return new AnswerAggregate(answer.getId(), answer.getYear(), answer.getQna());
    }
}
