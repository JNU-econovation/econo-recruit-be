package com.econovation.recruitdomain.domains.card.dto;

import com.econovation.recruitdomain.domains.applicant.domain.state.ApplicantState;
import com.econovation.recruitdomain.domains.board.domain.Board;
import com.econovation.recruitdomain.domains.board.domain.CardType;
import com.econovation.recruitdomain.domains.card.domain.Card;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

import static com.econovation.recruitcommon.consts.RecruitStatic.PASS_STATE_KEY;

@Getter
@Data
@Builder
public class BoardCardResponseDto {
    private Long id;
    private Integer boardId;
    private Integer columnId;
    private Integer nextBoardId;
    private CardType cardType;
    private String title;
    private String content;
    private Integer labelCount;
    private String major;
    private String applicantId;
    private Integer commentCount;
    private String firstPriority;
    private String secondPriority;
    private Boolean isLabeled;
    private Map<String,String> state;

    public static BoardCardResponseDto from(
            Card card,
            Board board,
            String firstPriority,
            String secondPriority,
            String major,
            Boolean isLabeled,
            ApplicantState state) {
        return BoardCardResponseDto.builder()
                .boardId(board.getId())
                .applicantId(card.getApplicantId())
                .nextBoardId(board.getNextBoardId())
                .id(card.getId())
                .major(major)
                .cardType(board.getCardType())
                .columnId(board.getColumnId())
                .title(card.getTitle())
                .content(card.getContent())
                .labelCount(card.getLabelCount())
                .commentCount(card.getCommentCount())
                .firstPriority(firstPriority)
                .secondPriority(secondPriority)
                .isLabeled(isLabeled)
                .state(toMap(state))
                .build();
    }

    private static Map<String, String> toMap(ApplicantState state){
        Map<String, String> stateMap = new HashMap<>();
        stateMap.put(PASS_STATE_KEY, state.getPassState());
        return stateMap;
    }
}
