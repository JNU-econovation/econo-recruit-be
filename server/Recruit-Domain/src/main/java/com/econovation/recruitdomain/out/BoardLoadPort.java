package com.econovation.recruitdomain.out;

import com.econovation.recruitdomain.domains.board.domain.Board;
import java.util.List;
import java.util.Optional;

public interface BoardLoadPort {
    Board getBoardById(Integer id);

    List<Board> getBoardByNavigationIdAndColumnsId(Integer navigationId, Integer colLoc);

    List<Board> getBoardByNavigationIdAndColumnId(Integer navigationId, Integer columnId);

    List<Board> getBoardByNavLoc(Integer navLoc);

    Optional<Board> getByNextBoardId(Integer id);

    Optional<Board> getById(Integer id);

    List<Board> getBoardByColumnsIds(List<Integer> columnsIds);

    List<Board> getBoardByColumnsId(Integer columnsId);

    Board getBoardByCardId(Long cardId);
}
