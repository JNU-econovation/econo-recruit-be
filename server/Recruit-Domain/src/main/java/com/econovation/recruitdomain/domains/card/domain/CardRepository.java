package com.econovation.recruitdomain.domains.card.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    Optional<Card> findByApplicantId(String applicantId);

    @Query(
            "SELECT c.applicantId FROM Card c JOIN Board b ON c.id = b.cardId JOIN Columns col ON b.columnId = col.id WHERE col.title = :columnTitle")
    List<String> findByApplicantIdIn(@Param("columnTitle") String columnTitle);
}
