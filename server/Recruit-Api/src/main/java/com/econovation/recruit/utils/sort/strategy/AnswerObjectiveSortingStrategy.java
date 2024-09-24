package com.econovation.recruit.utils.sort.strategy;

import com.econovation.recruitdomain.domains.applicant.domain.MongoAnswer;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Qualifier;

// @Component
@Qualifier("answerObjectiveSort")
public class AnswerObjectiveSortingStrategy implements SortStrategy<MongoAnswer> {
    String[] fieldOrder = {"WEB", "GAME", "APP", "AI"};

    @Override
    public int compare(MongoAnswer obj1, MongoAnswer obj2) {
        return Integer.compare(
                Arrays.asList(fieldOrder).indexOf(obj1.getQna().get("field1").toString()),
                Arrays.asList(fieldOrder).indexOf(obj2.getQna().get("field1").toString()));
    }
}
