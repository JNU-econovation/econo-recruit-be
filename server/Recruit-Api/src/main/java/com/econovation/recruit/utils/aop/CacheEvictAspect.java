package com.econovation.recruit.utils.aop;

import static com.econovation.recruitcommon.consts.RecruitStatic.DESIGNER_COLUMNS_ID;
import static com.econovation.recruitcommon.consts.RecruitStatic.DEVELOPER_COLUMNS_ID;
import static com.econovation.recruitcommon.consts.RecruitStatic.PLANNER_COLUMNS_ID;

import com.econovation.recruit.utils.SpELParser;
import com.econovation.recruitcommon.annotation.InvalidateCacheByCardLocation;
import com.econovation.recruitcommon.annotation.InvalidateCacheByColumnLocation;
import com.econovation.recruitcommon.annotation.InvalidateCacheByHopeField;
import com.econovation.recruitcommon.annotation.InvalidateCache;
import com.econovation.recruitcommon.annotation.InvalidateCaches;
import com.econovation.recruitdomain.domains.board.domain.Board;
import com.econovation.recruitdomain.domains.board.domain.Columns;
import com.econovation.recruitdomain.domains.dto.UpdateLocationBoardDto;
import com.econovation.recruitdomain.domains.dto.UpdateLocationColumnDto;
import com.econovation.recruitdomain.out.BoardLoadPort;
import com.econovation.recruitdomain.out.ColumnLoadPort;
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

    private final String BOARDS_BY_COLUMNS_IDS = "boardsByColumnsIds";
    private final String COLUMNS_BY_NAVIGATION_ID = "columnsByNavigationId";
    private final String BOARD_CARDS_BY_NAVIGATION_ID = "boardCardsByNavigationId";

    private final CacheManager cacheManager;
    private final BoardLoadPort boardLoadPort;
    private final ColumnLoadPort columnLoadPort;

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
        Integer parsedKey = (Integer) SpELParser.getDynamicValue(joinPoint, invalidateCache.key());
        evictCache(cacheName, parsedKey);
    }

    private void evictCache(String cacheName, Integer key) {
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

        evictCache(BOARDS_BY_COLUMNS_IDS, columnsId);
        evictCache(BOARD_CARDS_BY_NAVIGATION_ID, 1);
    }

    @Before("@annotation(invalidateCacheByColumnLocation) && args(updateLocationDto)")
    public void invalidateCachetByColumnLocation(InvalidateCacheByColumnLocation invalidateCacheByColumnLocation, UpdateLocationColumnDto updateLocationDto) {
        // Column 이동은 동일한 navigation에서 이루어지므로 하나만 조회해서 navigationId를 가져오고 무효화
        Columns column = columnLoadPort.findById(updateLocationDto.getColumnId());
        Integer navigationId = column.getNavigationId();

        evictCache(COLUMNS_BY_NAVIGATION_ID, navigationId);
        evictCache(BOARD_CARDS_BY_NAVIGATION_ID, navigationId);
    }

    @Before("@annotation(invalidateCacheByCardLocation) && args(updateLocationDto)")
    public void invalidateCachetByCardLocation(InvalidateCacheByCardLocation invalidateCacheByCardLocation, UpdateLocationBoardDto updateLocationDto) {
        // Board 이동은 동일한 navigation에서 이루어지므로 하나만 조회해서 navigationId를 가져오고 무효화
        // Column에 해당하는 Board가 달라지므로 current, target 둘 다 조회해서 columnId를 가져오고 무효화

        Board currentBoard = boardLoadPort.getBoardById(updateLocationDto.getBoardId());
        Board targetBoard = boardLoadPort.getBoardById(updateLocationDto.getTargetBoardId());

        Integer navigationId = currentBoard.getNavigationId();

        evictCache(BOARDS_BY_COLUMNS_IDS, currentBoard.getColumnId());
        evictCache(BOARDS_BY_COLUMNS_IDS, targetBoard.getColumnId());
        evictCache(BOARD_CARDS_BY_NAVIGATION_ID, navigationId);
    }

}
