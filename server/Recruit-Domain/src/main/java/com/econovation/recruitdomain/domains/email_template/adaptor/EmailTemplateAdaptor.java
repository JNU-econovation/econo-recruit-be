package com.econovation.recruitdomain.domains.email_template.adaptor;

import com.econovation.recruitdomain.domains.email_template.domain.EmailTemplate;
import com.econovation.recruitdomain.domains.email_template.domain.EmailTemplateRepository;
import com.econovation.recruitdomain.domains.email_template.domain.EmailTemplateType;
import com.econovation.recruitdomain.domains.email_template.exception.EmailTemplateNotFoundException;
import com.econovation.recruitdomain.out.EmailTemplateLoadPort;
import com.econovation.recruitdomain.out.EmailTemplateRecordPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailTemplateAdaptor implements EmailTemplateLoadPort, EmailTemplateRecordPort {
    private final EmailTemplateRepository emailTemplateRepository;

    @Override
    public EmailTemplate findById(Long id) {
        return emailTemplateRepository
                .findById(id)
                .orElseThrow(() -> EmailTemplateNotFoundException.EXCEPTION);
    }

    @Override
    public EmailTemplate getByEmailTemplateType(EmailTemplateType emailTemplateType) {
        return emailTemplateRepository
                .findByEmailTemplateType(emailTemplateType.name())
                .orElseThrow(() -> EmailTemplateNotFoundException.EXCEPTION);
    }

    @Override
    public void save(EmailTemplate emailTemplate) {
        emailTemplateRepository.save(emailTemplate);
    }
}
