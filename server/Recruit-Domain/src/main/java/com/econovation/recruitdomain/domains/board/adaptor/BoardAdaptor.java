package com.econovation.recruitdomain.domains.board.adaptor;

import com.econovation.recruitcommon.annotation.Adaptor;
import com.econovation.recruitdomain.domains.board.domain.Board;
import com.econovation.recruitdomain.domains.board.domain.BoardRepository;
import com.econovation.recruitdomain.domains.board.exception.BoardNotFoundException;
import com.econovation.recruitdomain.out.BoardLoadPort;
import com.econovation.recruitdomain.out.BoardRecordPort;
import java.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;

@Slf4j
@Adaptor
@RequiredArgsConstructor
public class BoardAdaptor implements BoardLoadPort, BoardRecordPort {
    private static final String NO_NAVLOC_MESSAGE = "해당하는 메뉴가 존재하지 않습니다";
    private static final String NO_BOARD_MESSAGE = "해당하는 BOARD가 존재하지 않습니다";
    private static final String NOT_MATCH_MESSAGE = "일치하는 Board를 찾을 수 없습니다";
    private final BoardRepository boardRepository;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Board getBoardById(Integer id) {
        return boardRepository.findById(id).orElseThrow(() -> BoardNotFoundException.EXCEPTION);
    }

    @Override
    public List<Board> getBoardByNavigationIdAndColumnsId(Integer navigationId, Integer columnsId) {
        return boardRepository.findByNavigationIdAndColumnId(navigationId, columnsId);
    }

    @Override
    public List<Board> getBoardByNavigationIdAndColumnId(Integer navigationId, Integer columnId) {
        return boardRepository.findByNavigationIdAndColumnId(navigationId, columnId);
    }

    @Override
    public List<Board> getBoardByNavLoc(Integer navigationId) {
        return boardRepository.findByNavigationId(navigationId);
    }

    @Override
    public Optional<Board> getByNextBoardId(Integer nextBoardId) {
        Optional<Board> byNextBoardId = boardRepository.findByNextBoardId(nextBoardId);
        if (byNextBoardId == null) {
            return null;
        }
        return byNextBoardId;
    }

    @Override
    public Optional<Board> getById(Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        return boardRepository.findById(id);
    }

    @Override
    public List<Board> getBoardByColumnsIds(List<Integer> columnsIds) {
        return boardRepository.findByColumnIdIn(columnsIds);
    }

    @Override
    public List<Board> getBoardByColumnsId(Integer columnsId) {
        return boardRepository.findByColumnId(columnsId);
    }

    @Override
    public Board getBoardByCardId(Long cardId) {
        Optional<Board> board = boardRepository.findByCardId(cardId);
        if (board.isEmpty()) {
            throw BoardNotFoundException.EXCEPTION;
        }
        return board.get();
    }

    @Override
    public Board save(Board board) {
        return boardRepository.save(board);
    }

    @Override
    public void delete(Board board) {
        boardRepository.delete(board);
    }

    @Override
    public void saveAll(List<Board> board) {
        boardRepository.saveAll(board);
    }
}
