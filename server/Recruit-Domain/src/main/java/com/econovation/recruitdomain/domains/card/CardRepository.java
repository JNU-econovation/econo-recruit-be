package com.econovation.recruitdomain.domains.card;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardRepository extends JpaRepository<Card, Integer> {
    List<Card> findAllByBoardIdIn(List<Integer> boardIds);

    Optional<Card> findByApplicantId(Integer applicantId);
}