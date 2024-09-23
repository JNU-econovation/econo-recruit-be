package com.econovation.recruit.api.email_template.usecase;

import com.econovation.recruitcommon.annotation.UseCase;
import com.econovation.recruitdomain.domains.email_template.domain.EmailTemplate;

@UseCase
public interface EmailTemplateLoadUseCase {
    EmailTemplate findById(Long emailtTemplateId);
}
