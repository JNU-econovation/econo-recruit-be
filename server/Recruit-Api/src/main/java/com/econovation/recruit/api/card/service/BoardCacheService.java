package com.econovation.recruit.api.card.service;

import com.econovation.recruitdomain.domains.board.domain.Board;
import com.econovation.recruitdomain.out.BoardLoadPort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoardCacheService {

    private final BoardLoadPort boardLoadPort;

    @Cacheable(value = "boardsByColumnsId", key = "#columnsId")
    public List<Board> getBoardByColumnsId(Integer columnsId) {
        return boardLoadPort.getBoardByColumnsId(columnsId);
    }

}
