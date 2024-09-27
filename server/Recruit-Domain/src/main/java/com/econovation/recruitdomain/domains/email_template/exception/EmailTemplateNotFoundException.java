package com.econovation.recruitdomain.domains.email_template.exception;

import com.econovation.recruitcommon.exception.RecruitCodeException;

public class EmailTemplateNotFoundException extends RecruitCodeException {
    public static final EmailTemplateNotFoundException EXCEPTION =
            new EmailTemplateNotFoundException();

    private EmailTemplateNotFoundException() {
        super(EmailTemplateErrorCode.EMAIL_TEMPLATE_NOT_FOUND);
    }
}
