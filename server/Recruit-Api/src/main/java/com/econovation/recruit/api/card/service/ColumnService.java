package com.econovation.recruit.api.card.service;

import com.econovation.recruit.api.card.usecase.ColumnsUseCase;
import com.econovation.recruitdomain.domains.board.domain.Columns;
import com.econovation.recruitdomain.out.ColumnLoadPort;
import com.econovation.recruitdomain.out.ColumnRecordPort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ColumnService implements ColumnsUseCase {
    private final ColumnRecordPort columnRecordPort;
    private final ColumnLoadPort columnLoadPort;

    @Cacheable(value = "columnsByNavigationId", key = "#navigationId")
    public List<Columns> getByNavigationId(Integer navigationId) {
        return columnLoadPort.getColumnByNavigationId(navigationId);
    }
}
