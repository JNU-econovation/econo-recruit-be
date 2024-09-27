package com.econovation.recruitdomain.out;

import com.econovation.recruitdomain.domains.email_template.domain.EmailTemplate;

public interface EmailTemplateRecordPort {
    void save(EmailTemplate emailTemplate);
}
