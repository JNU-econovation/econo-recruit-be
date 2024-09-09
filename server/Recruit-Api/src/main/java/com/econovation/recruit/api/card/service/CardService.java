package com.econovation.recruit.api.card.service;

import com.econovation.recruit.api.applicant.usecase.ApplicantQueryUseCase;
import com.econovation.recruit.api.card.usecase.BoardLoadUseCase;
import com.econovation.recruit.api.card.usecase.BoardRegisterUseCase;
import com.econovation.recruit.api.card.usecase.CardLoadUseCase;
import com.econovation.recruit.api.card.usecase.CardRegisterUseCase;
import com.econovation.recruit.api.card.usecase.ColumnsUseCase;
import com.econovation.recruit.api.config.security.SecurityUtils;
import com.econovation.recruitcommon.utils.Result;
import com.econovation.recruitdomain.common.aop.domainEvent.Events;
import com.econovation.recruitdomain.common.events.WorkCardDeletedEvent;
import com.econovation.recruitdomain.domains.applicant.adaptor.AnswerAdaptor;
import com.econovation.recruitdomain.domains.applicant.domain.MongoAnswer;
import com.econovation.recruitdomain.domains.applicant.domain.MongoAnswerAdaptor;
import com.econovation.recruitdomain.domains.applicant.exception.ApplicantProhibitDeleteException;
import com.econovation.recruitdomain.domains.board.domain.Board;
import com.econovation.recruitdomain.domains.board.domain.CardType;
import com.econovation.recruitdomain.domains.board.domain.Columns;
import com.econovation.recruitdomain.domains.card.domain.Card;
import com.econovation.recruitdomain.domains.card.dto.BoardCardResponseDto;
import com.econovation.recruitdomain.domains.card.dto.CardResponseDto;
import com.econovation.recruitdomain.domains.dto.CreateWorkCardDto;
import com.econovation.recruitdomain.domains.dto.UpdateWorkCardDto;
import com.econovation.recruitdomain.domains.label.domain.Label;
import com.econovation.recruitdomain.out.CardLoadPort;
import com.econovation.recruitdomain.out.CardRecordPort;
import com.econovation.recruitdomain.out.LabelLoadPort;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CardService implements CardRegisterUseCase, CardLoadUseCase {
    private final CardRecordPort cardRecordPort;
    private final CardLoadPort cardLoadPort;
    private final BoardRegisterUseCase boardRegisterUseCase;
    private final BoardLoadUseCase boardLoadUseCase;
    private final ColumnsUseCase columnsUseCase;
    private final ApplicantQueryUseCase applicantQueryUseCase;
    private final LabelLoadPort labelLoadPort;
    private final AnswerAdaptor answerAdaptor;
    private final MongoAnswerAdaptor mongoAnswerAdaptor;

    @Override
    @Transactional(readOnly = true)
    public List<Card> findAll() {
        return cardLoadPort.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BoardCardResponseDto> getByNavigationId(Integer navigationId, Integer year) {
        Long userId = SecurityUtils.getCurrentUserId();
        List<Columns> columns = columnsUseCase.getByNavigationId(navigationId);

        List<Integer> columnsIds =
                columns.stream().map(Columns::getId).collect(Collectors.toList());

        List<Board> boards = boardLoadUseCase.getBoardByColumnsIds(columnsIds);

        List<MongoAnswer> mongoAnswers = answerAdaptor.findAll();

        Map<String, Integer> yearByAnswerIdMap = mongoAnswers.stream()
                .collect(Collectors.toMap(MongoAnswer::getId, MongoAnswer::getYear));

        List<Card> cards = cardLoadPort.findAll();

        Map<Long, String> answerIdByCardIdMap = cards.stream()
                .collect(Collectors.toMap(Card::getId, Card::getApplicantId));

        boards = boards.stream()
                .filter(
                        board ->{
                            if(board.getId()==1 || board.getId()==2 || board.getId()==3) {
                                return true;
                            }
                            return Optional.ofNullable(board.getCardId())
                                    .map(answerIdByCardIdMap::get)
                                    .map(yearByAnswerIdMap::get)
                                    .map(y -> y.equals(year))
                                    .orElse(false);
                        })
                .toList();

        cards =
                cardLoadPort.findByIdIn(
                        boards.stream().map(Board::getCardId).collect(Collectors.toList()));

        Map<Long, Card> cardByBoardIdMap =
                cards.stream().collect(Collectors.toMap(Card::getId, card -> card));

        List<BoardCardResponseDto> result = new LinkedList<>();

        // key : applicantId
        Map<String, Map<String, Object>> answers =
                applicantQueryUseCase.findAllApplicantVo(List.of("field1", "field2", "major"));

        List<Label> labels =
                labelLoadPort.loadLabelByCardIdIn(
                        cards.stream().map(Card::getId).collect(Collectors.toList()));

        for (Board board : boards) {
            if (board.getCardType().equals(CardType.INVISIBLE)) {
                result.add(BoardCardResponseDto.from(Card.empty(), board, "", "", "", false));
                continue;
            }
            Card card = cardByBoardIdMap.get(board.getCardId());
            String firstPriority = "";
            String secondPriority = "";
            String major = "";
            if (answers.isEmpty()) {
                result.add(
                        BoardCardResponseDto.from(
                                card, board, firstPriority, secondPriority, "", false));
                continue;
            }
            Map<String, Object> applicantAnswers = answers.get(card.getApplicantId());
            if (applicantAnswers != null) {
                major = applicantAnswers.getOrDefault("major", "").toString();
                firstPriority = applicantAnswers.getOrDefault("field1", "").toString();
                secondPriority = applicantAnswers.getOrDefault("field2", "").toString();
            } else {
                firstPriority = "";
                secondPriority = "";
                major = "";
            }
            Boolean isLabeled =
                    labels.stream()
                            .anyMatch(
                                    label ->
                                            label.getCardId().equals(card.getId())
                                                    && label.getIdpId().equals(userId));

            result.add(
                    BoardCardResponseDto.from(
                            card, board, firstPriority, secondPriority, major, isLabeled));
        }
        return result;
    }

    @Override
    public CardResponseDto findCardById(Long cardId) {
        Card card = cardLoadPort.findById(cardId);
        return CardResponseDto.from(card);
    }

    @Override
    @Transactional
    public void deleteById(Long cardId) {
        Board board = boardLoadUseCase.getBoardByCardId(cardId);
        Result<Board> prevBoard = boardLoadUseCase.getBoardByNextBoardId(board.getId());
        // 지원서 카드는 삭제가 불가능하다. -> 업무 카드만 삭제가 가능하다.
        if (board.getCardType().equals(CardType.WORK_CARD)) {
            prevBoard.onSuccess(
                    prev -> {
                        prev.updateNextBoardID(board.getNextBoardId());
                    });
            boardRegisterUseCase.delete(board);
            cardRecordPort.delete(cardId);
            // Work 관련된 댓글 및 라벨을 전체 삭제 Cascade를 Event 로 동작
            Events.raise(WorkCardDeletedEvent.of(board.getCardId()));
        } else {
            throw ApplicantProhibitDeleteException.EXCEPTION;
        }
    }

    @Override
    @Transactional
    public void saveWorkCard(CreateWorkCardDto createWorkCardDto) {
        Card card =
                Card.builder()
                        .title(createWorkCardDto.getTitle())
                        .content(createWorkCardDto.getContent())
                        .applicantId("")
                        .build();
        Card savedCard = cardRecordPort.save(card);
        boardRegisterUseCase.createWorkBoard(createWorkCardDto.getColumnId(), savedCard.getId());
    }

    @Override
    @Transactional
    public void update(Long cardId, UpdateWorkCardDto updateWorkCardDto) {
        Card card = cardLoadPort.findById(cardId);
        //        단 title 이 null일 수도 있고, content가 null일 수도 있다.
        if (updateWorkCardDto.getTitle() != null) {
            card.updateTitle(updateWorkCardDto.getTitle());
        }
        if (updateWorkCardDto.getContent() != null) {
            card.updateContent(updateWorkCardDto.getContent());
        }
    }
}
