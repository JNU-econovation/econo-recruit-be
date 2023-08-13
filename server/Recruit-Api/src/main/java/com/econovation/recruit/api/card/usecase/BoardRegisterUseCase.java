package com.econovation.recruit.api.card.usecase;

import com.econovation.recruitcommon.annotation.UseCase;
import com.econovation.recruitdomain.domains.board.domain.Board;
import com.econovation.recruitdomain.domains.board.domain.Columns;
import java.util.UUID;

@UseCase
public interface BoardRegisterUseCase {
    void execute(Board board);

    Board createWorkBoard(Integer navigationId, Integer colLoc, Integer cardId);

    void createApplicantBoard(UUID applicantId, String hopeField, Integer cardId);

    Columns createColumn(String title, Integer navigationId);
    //    void execute(CreateWorkCardDto createWorkCardDto, CardType cardType);
}