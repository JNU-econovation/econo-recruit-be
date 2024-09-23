package com.econovation.recruit.api.applicant.state.support;

import com.econovation.recruitdomain.domains.applicant.domain.state.PeriodStates;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PeriodCalculator {

    @Value("${econovation.recruit.period.firstDiscussion}")
    private String firstDiscussionPeriod;

    @Value("${econovation.recruit.period.finalDiscussion}")
    private String finalDiscussionPeriod;

    public PeriodStates execute() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime firstDiscussion = LocalDateTime.parse(firstDiscussionPeriod);
        LocalDateTime finalDiscussion = LocalDateTime.parse(finalDiscussionPeriod);

        if (now.isBefore(firstDiscussion)) return PeriodStates.FIRST_DISCUSSION;
        else if (now.isBefore(finalDiscussion)) return PeriodStates.FINAL_DISCUSSION;
        else return PeriodStates.END;
    }
}
