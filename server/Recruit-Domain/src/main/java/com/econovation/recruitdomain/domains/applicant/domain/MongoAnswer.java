package com.econovation.recruitdomain.domains.applicant.domain;

import com.econovation.recruitdomain.domains.BaseTimeEntity;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "applicant")
@AllArgsConstructor
@Getter
@Builder
public class MongoAnswer extends BaseTimeEntity {
    @Id
    //    @GeneratedValue(
    //            generator =
    // "com.econovation.recruitdomain.domains.idGenerator.SnowFlakeGenerator")
    private String id;

    @Field("year")
    private Integer year;

    // shemaless
    @Field("qna")
    private Map<String, Object> qna;
}
