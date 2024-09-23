package com.econovation.recruitdomain.domains.applicant.adaptor;

import static com.econovation.recruitcommon.consts.RecruitStatic.PAGE_SIZE;

import com.econovation.recruitcommon.annotation.Adaptor;
import com.econovation.recruitdomain.domains.applicant.domain.MongoAnswer;
import com.econovation.recruitdomain.domains.applicant.domain.MongoAnswerRepository;
import java.util.List;

import com.econovation.recruitdomain.domains.applicant.exception.ApplicantNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.core.query.TextQuery;

@Adaptor
@RequiredArgsConstructor
public class AnswerAdaptor {
    private final MongoTemplate mongoTemplate;
    private final MongoAnswerRepository answerRepository;

    public void save(MongoAnswer answer) {
        answerRepository.save(answer);
    }

    public void saveAll(List<MongoAnswer> answers) {
        answerRepository.saveAll(answers);
    }

    public List<MongoAnswer> findAll() {
        return answerRepository.findAll();
    }

    public List<MongoAnswer> findByYear(Integer year, Integer page) {
        Query query =
                new Query()
                        .with(Sort.by(Sort.Direction.DESC, "created_date")) // Optional: 정렬 설정
                        .addCriteria(Criteria.where("year").is(year))
                        .skip((page - 1) * 10L) // offset 설정
                        .limit(PAGE_SIZE); // 페이지 크기 설정
        return mongoTemplate.find(query, MongoAnswer.class);
    }

    public List<MongoAnswer> findByYear(Integer year) {
        Query query =
                new Query()
                        .addCriteria(Criteria.where("year").is(year));
        List<MongoAnswer> result =  mongoTemplate.find(query, MongoAnswer.class);
        if(result.isEmpty()) {
            throw ApplicantNotFoundException.EXCEPTION;
        }
        return result;
    }

    public long getTotalCountByYear(Integer year) {
        return mongoTemplate.count(Query.query(Criteria.where("year").is(year)), MongoAnswer.class);
    }

    public List<MongoAnswer> findByApplicantIds(List<String> applicantIds) {
        return answerRepository.findByIdIn(applicantIds);
    }

    public List<MongoAnswer> findBySearchKeyword(Integer page, String searchKeyword) {
        TextCriteria criteria = TextCriteria.forDefaultLanguage().matchingAny(searchKeyword);
        // 페이지네이션을 위한 페이지 및 결과 수 계산
        TextQuery query = TextQuery.queryText(criteria).sortByScore();
        // 페이지네이션 적용하여 결과 조회
        Pageable pageable = PageRequest.of(page - 1, PAGE_SIZE);
        query.with(pageable);

        return mongoTemplate.find(query, MongoAnswer.class);
    }

    public List<MongoAnswer> findByYearAndSearchKeyword(Integer year, Integer page, String sortType, String searchKeyword) {
        Query query = new Query()
                .addCriteria(Criteria.where("year").is(year))
                .skip((page - 1) * 10L)
                .limit(PAGE_SIZE);

        setSortType(query, sortType);

        addCriteriaIfSearchKeywordExists(searchKeyword, query);

        return mongoTemplate.find(query, MongoAnswer.class);
    }

    private void addCriteriaIfSearchKeywordExists(String searchKeyword, Query query) {
        if (searchKeyword != null && !searchKeyword.isEmpty()) {
            TextCriteria criteria = TextCriteria.forDefaultLanguage().matchingAny(searchKeyword);
            query.addCriteria(criteria);
        }
    }

    private void setSortType(Query query, String sortType) {
        switch (sortType) {
            case "name" -> query.with(Sort.by(Direction.ASC, "qna.name"));
            case "newest" -> query.with(Sort.by(Direction.DESC, "created_date"));
            case "objective" -> query.with(Sort.by(Direction.DESC, "qna.field1"));  // WEB, GAME, APP, AI
        }
    }

    public long getTotalCountByYearAndSearchKeyword(Integer year, String searchKeyword) {
        Query query = new Query()
                .addCriteria(Criteria.where("year").is(year));

        addCriteriaIfSearchKeywordExists(searchKeyword, query);

        return mongoTemplate.count(query, MongoAnswer.class);
    }

    public List<MongoAnswer> findByYearAndSearchKeywordAndApplicantIds(Integer page, Integer year, String sortType, String searchKeyword,
            List<String> applicantIds) {

        Query query = new Query()
                .addCriteria(Criteria.where("year").is(year))
                .addCriteria(Criteria.where("id").in(applicantIds))
                .skip((page - 1) * 10L)
                .limit(PAGE_SIZE);

        setSortType(query, sortType);

        addCriteriaIfSearchKeywordExists(searchKeyword, query);

        return mongoTemplate.find(query, MongoAnswer.class);
    }

    public List<MongoAnswer> findByYearAndSearchKeywordAndApplicantIds(Integer year, String sortType, String searchKeyword,
            List<String> applicantIds) {

        Query query = new Query()
                .addCriteria(Criteria.where("year").is(year))
                .addCriteria(Criteria.where("id").in(applicantIds));

        setSortType(query, sortType);

        addCriteriaIfSearchKeywordExists(searchKeyword, query);

        return mongoTemplate.find(query, MongoAnswer.class);
    }

    public long getTotalCountByYearAndSearchKeywordAndApplicantIds(Integer year, String searchKeyword, List<String> applicantIds) {
        Query query = new Query()
                .addCriteria(Criteria.where("year").is(year))
                .addCriteria(Criteria.where("id").in(applicantIds));

        addCriteriaIfSearchKeywordExists(searchKeyword, query);

        return mongoTemplate.count(query, MongoAnswer.class);
    }
}
