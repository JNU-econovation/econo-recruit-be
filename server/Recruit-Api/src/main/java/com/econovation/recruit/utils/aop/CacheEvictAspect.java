package com.econovation.recruit.utils.aop;

import static com.econovation.recruitcommon.consts.RecruitStatic.DESIGNER_COLUMNS_ID;
import static com.econovation.recruitcommon.consts.RecruitStatic.DEVELOPER_COLUMNS_ID;
import static com.econovation.recruitcommon.consts.RecruitStatic.PLANNER_COLUMNS_ID;

import com.econovation.recruit.utils.SpELParser;
import com.econovation.recruitcommon.annotation.InvalidateCache;
import com.econovation.recruitcommon.annotation.InvalidateCacheByCardId;
import com.econovation.recruitcommon.annotation.InvalidateCacheByCardLocation;
import com.econovation.recruitcommon.annotation.InvalidateCacheByColumnLocation;
import com.econovation.recruitcommon.annotation.InvalidateCacheByCreateComment;
import com.econovation.recruitcommon.annotation.InvalidateCacheByCreateWorkCard;
import com.econovation.recruitcommon.annotation.InvalidateCacheByCommentId;
import com.econovation.recruitcommon.annotation.InvalidateCacheByDeleteComment;
import com.econovation.recruitcommon.annotation.InvalidateCacheByHopeField;
import com.econovation.recruitcommon.annotation.InvalidateCacheByUpdateWorkCard;
import com.econovation.recruitcommon.annotation.InvalidateCaches;
import com.econovation.recruitdomain.domains.board.domain.Board;
import com.econovation.recruitdomain.domains.board.domain.Columns;
import com.econovation.recruitdomain.domains.card.domain.Card;
import com.econovation.recruitdomain.domains.comment.domain.Comment;
import com.econovation.recruitdomain.domains.dto.CommentRegisterDto;
import com.econovation.recruitdomain.domains.dto.CreateWorkCardDto;
import com.econovation.recruitdomain.domains.dto.UpdateLocationBoardDto;
import com.econovation.recruitdomain.domains.dto.UpdateLocationColumnDto;
import com.econovation.recruitdomain.out.BoardLoadPort;
import com.econovation.recruitdomain.out.CardLoadPort;
import com.econovation.recruitdomain.out.ColumnLoadPort;
import com.econovation.recruitdomain.out.CommentLoadPort;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class CacheEvictAspect {

    private final String BOARDS_BY_COLUMNS_ID = "boardsByColumnsId";
    private final String COLUMNS_BY_NAVIGATION_ID = "columnsByNavigationId";
    private final String BOARD_CARDS_BY_NAVIGATION_ID = "boardCardsByNavigationId";
    private final String COMMENTS_BY_APPLICANT_ID = "commentsByApplicantId";

    private final CacheManager cacheManager;
    private final CardLoadPort cardLoadPort;
    private final BoardLoadPort boardLoadPort;
    private final ColumnLoadPort columnLoadPort;
    private final CommentLoadPort commentLoadPort;

    @Before("@annotation(invalidateCaches)")
    public void invalidateCaches(JoinPoint joinPoint, InvalidateCaches invalidateCaches) {
        InvalidateCache[] value = invalidateCaches.value();
        for (InvalidateCache invalidateCache : value) {
            invalidateCache(joinPoint, invalidateCache);
        }
    }

    @Before("@annotation(invalidateCache)")
    public void invalidateCache(JoinPoint joinPoint, InvalidateCache invalidateCache) {
        String cacheName = invalidateCache.cacheName();
        String key = invalidateCache.key();

        if (key.isEmpty()) {
            evictCache(cacheName, null);
            return;
        }
        String parsedKey = (String) SpELParser.getDynamicValue(joinPoint, invalidateCache.key());
        evictCache(cacheName, parsedKey);
    }

    private void evictCache(String cacheName, String key) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            return;
        }

        if (key == null) {
            cache.clear();
        } else {
            cache.evictIfPresent(key);
        }
    }

    @Before("@annotation(invalidateCacheByHopeField) && args(*, hopeField, ..)")
    public void invalidateCacheByHopeField(InvalidateCacheByHopeField invalidateCacheByHopeField, String hopeField) {
        Integer columnsId = 0;
        switch (hopeField) {
            case "개발자" -> columnsId = DEVELOPER_COLUMNS_ID;
            case "디자이너" -> columnsId = DESIGNER_COLUMNS_ID;
            case "기획자" -> columnsId = PLANNER_COLUMNS_ID;
            default -> {
            }
        }

        evictCache(BOARDS_BY_COLUMNS_ID, columnsId.toString());
        evictCache(BOARD_CARDS_BY_NAVIGATION_ID, "1");
    }

    @Before("@annotation(invalidateCacheByColumnLocation) && args(updateLocationDto)")
    public void invalidateCachetByColumnLocation(InvalidateCacheByColumnLocation invalidateCacheByColumnLocation, UpdateLocationColumnDto updateLocationDto) {
        // Column 이동은 동일한 navigation에서 이루어지므로 하나만 조회해서 navigationId를 가져오고 무효화
        Columns column = columnLoadPort.findById(updateLocationDto.getColumnId());
        Integer navigationId = column.getNavigationId();

        evictCache(COLUMNS_BY_NAVIGATION_ID, navigationId.toString());
        evictCache(BOARD_CARDS_BY_NAVIGATION_ID, navigationId.toString());
    }

    @Before("@annotation(invalidateCacheByCardLocation) && args(updateLocationDto)")
    public void invalidateCachetByCardLocation(InvalidateCacheByCardLocation invalidateCacheByCardLocation, UpdateLocationBoardDto updateLocationDto) {
        // Board 이동은 동일한 navigation에서 이루어지므로 하나만 조회해서 navigationId를 가져오고 무효화
        // Column에 해당하는 Board가 달라지므로 current, target 둘 다 조회해서 columnId를 가져오고 무효화

        Board currentBoard = boardLoadPort.getBoardById(updateLocationDto.getBoardId());
        Board targetBoard = boardLoadPort.getBoardById(updateLocationDto.getTargetBoardId());

        Integer navigationId = currentBoard.getNavigationId();

        evictCache(BOARDS_BY_COLUMNS_ID, currentBoard.getColumnId().toString());
        evictCache(BOARDS_BY_COLUMNS_ID, targetBoard.getColumnId().toString());
        evictCache(BOARD_CARDS_BY_NAVIGATION_ID, navigationId.toString());
    }

    @Before("@annotation(invalidateCacheByCardId)")
    public void invalidateCacheByCardId(JoinPoint joinPoint, InvalidateCacheByCardId invalidateCacheByCardId) {
        Long cardId = (Long) joinPoint.getArgs()[0];
        Board board = boardLoadPort.getBoardByCardId(cardId);

        evictCache(BOARD_CARDS_BY_NAVIGATION_ID, board.getNavigationId().toString());
    }

    @Before("@annotation(invalidateCacheByCreateWorkCard) && args(createWorkCardDto)")
    public void invalidateCacheByCreateWorkCard(InvalidateCacheByCreateWorkCard invalidateCacheByCreateWorkCard, CreateWorkCardDto createWorkCardDto) {
        Columns column = columnLoadPort.findById(createWorkCardDto.getColumnId());

        evictCache(BOARD_CARDS_BY_NAVIGATION_ID, column.getNavigationId().toString());
    }

    @Before("@annotation(invalidateCacheByUpdateWorkCard) && args(cardId, ..)")
    public void invalidateCacheByUpdateWorkCard(InvalidateCacheByUpdateWorkCard invalidateCacheByUpdateWorkCard, Long cardId) {
        Board board = boardLoadPort.getBoardByCardId(cardId);

        evictCache(BOARD_CARDS_BY_NAVIGATION_ID, board.getNavigationId().toString());
    }

    @Before("@annotation(invalidateCacheByCreateComment) && args(commentDto)")
    public void invalidateCacheByCreateComment(InvalidateCacheByCreateComment invalidateCacheByCreateComment, CommentRegisterDto commentDto) {
        // 업무카드 댓글 생성 요청
        if (commentDto.getApplicantId() == null) {
            Board board = boardLoadPort.getBoardByCardId(commentDto.getCardId());
            evictCache(BOARD_CARDS_BY_NAVIGATION_ID, board.getNavigationId().toString());
            return;
        }

        // 지원서카드 댓글 생성 요청
        Card card = cardLoadPort.findByApplicantId(commentDto.getApplicantId());
        Board board = boardLoadPort.getBoardByCardId(card.getId());

        // Card의 comment count를 변경하므로 무효화
        evictCache(BOARD_CARDS_BY_NAVIGATION_ID, board.getNavigationId().toString());
        // 지원자의 댓글 목록 조회 캐시 무효화
        evictCache(COMMENTS_BY_APPLICANT_ID, commentDto.getApplicantId());
    }

    @Before("@annotation(invalidateCacheByDeleteComment) && args(cardId)")
    public void invalidateCacheByDeleteComment(InvalidateCacheByDeleteComment invalidateCacheByDeleteComment, Long cardId) {
        Board board = boardLoadPort.getBoardByCardId(cardId);
        Card card = cardLoadPort.findById(cardId);

        // 댓글 삭제 시 업무카드의 댓글 수 변경으로 인한 무효화
        evictCache(BOARD_CARDS_BY_NAVIGATION_ID, board.getNavigationId().toString());
    }

    @Before("@annotation(invalidateCacheByCommentId) && args(commentId, ..)")
    public void invalidateCacheByCommentId(InvalidateCacheByCommentId invalidateCacheByCommentId, Long commentId) {
        Comment comment = commentLoadPort.findById(commentId);

        // 지원서 카드의 댓글인 경우 캐시 무효화
        if (comment.getApplicantId() != null) {
            evictCache(COMMENTS_BY_APPLICANT_ID, comment.getApplicantId());
        }
    }

}
