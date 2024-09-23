package com.econovation.recruitdomain.out;

import com.econovation.recruitdomain.domains.email_template.domain.EmailTemplate;
import com.econovation.recruitdomain.domains.email_template.domain.EmailTemplateType;

public interface EmailTemplateLoadPort {
    EmailTemplate findById(Long id);

    EmailTemplate getByEmailTemplateType(EmailTemplateType emailTemplateType);

    void save(EmailTemplate emailTemplate);
}
