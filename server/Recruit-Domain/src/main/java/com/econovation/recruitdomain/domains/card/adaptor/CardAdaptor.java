package com.econovation.recruitdomain.domains.card.adaptor;

import com.econovation.recruitdomain.domains.card.domain.Card;
import com.econovation.recruitdomain.domains.card.domain.CardRepository;
import com.econovation.recruitdomain.domains.card.exception.CardNotFoundException;
import com.econovation.recruitdomain.out.CardLoadPort;
import com.econovation.recruitdomain.out.CardRecordPort;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CardAdaptor implements CardLoadPort, CardRecordPort {
    private static final String NO_MATCH_CARD = "일치하는 Card 가 존재하지 않습니다";
    private final CardRepository cardRepository;

    @Override
    public Card save(Card card) {
        return cardRepository.save(card);
    }

    @Override
    public void delete(Long cardId) {
        cardRepository.findById(cardId).orElseThrow(() -> CardNotFoundException.EXCEPTION);
        cardRepository.deleteById(cardId);
    }

    @Override
    public List<Card> findAll() {
        return cardRepository.findAll();
    }

    @Override
    public Card findById(Long cardId) {
        return cardRepository.findById(cardId).orElseThrow(() -> CardNotFoundException.EXCEPTION);
    }

    @Override
    public Card findByApplicantId(String applicantId) {
        Optional<Card> applicant = cardRepository.findByApplicantId(applicantId);
        if (applicant.isEmpty()) {
            throw CardNotFoundException.EXCEPTION;
        } else {
            return applicant.get();
        }
    }

    @Override
    public List<Card> findByIdIn(List<Long> cardIds) {
        return cardRepository.findAllById(cardIds);
    }

    @Override
    public List<String> findApplicantIdByColumnTitle(String columnTitle) {
        return cardRepository.findByApplicantIdIn(columnTitle);
    }
}
