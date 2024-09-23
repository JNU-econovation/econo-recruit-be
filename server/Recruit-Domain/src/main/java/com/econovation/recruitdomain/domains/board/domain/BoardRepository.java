package com.econovation.recruitdomain.domains.board.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository<Board, Integer> {

    List<Board> findByNavigationId(Integer navigationId);

    List<Board> findByNavigationIdAndColumnId(Integer navigationId, Integer columnId);

    Optional<Board> findByNextBoardId(Integer nextBoardId);

    List<Board> findByColumnIdIn(List<Integer> columnsIds);

    List<Board> findByColumnId(Integer columnId);

    Optional<Board> findByCardId(Long cardId);
}
