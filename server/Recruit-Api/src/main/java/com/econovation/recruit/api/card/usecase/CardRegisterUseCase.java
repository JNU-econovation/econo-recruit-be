package com.econovation.recruit.api.card.usecase;

import com.econovation.recruitcommon.annotation.UseCase;
import com.econovation.recruitdomain.domains.dto.CreateWorkCardDto;

@UseCase
public interface CardRegisterUseCase {

    void deleteById(Long cardId);

    void saveWorkCard(CreateWorkCardDto createWorkCardDto);
}
