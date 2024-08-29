package com.econovation.recruitdomain.domains.card.dto;

import com.econovation.recruitdomain.domains.board.domain.Board;
import com.econovation.recruitdomain.domains.board.domain.CardType;
import com.econovation.recruitdomain.domains.card.domain.Card;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
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

    public static BoardCardResponseDto from(
            Card card,
            Board board,
            String firstPriority,
            String secondPriority,
            String major,
            Boolean isLabeled) {
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
                .build();
    }
}
