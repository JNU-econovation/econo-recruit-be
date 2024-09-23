package com.econovation.recruit.api.email_template.usecase;

import com.econovation.recruit.api.email_template.dto.EmailTemplateRequestDto;
import com.econovation.recruitcommon.annotation.UseCase;
import com.econovation.recruitdomain.domains.email_template.domain.EmailTemplate;

@UseCase
public interface EmailTemplateRegisterUseCase {
    EmailTemplate execute(EmailTemplateRequestDto emailTemplate);
}
