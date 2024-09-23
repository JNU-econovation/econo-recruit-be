package com.econovation.recruitdomain.domains.email_template.exception;

import com.econovation.recruitcommon.exception.RecruitCodeException;

public class EmailTemplateInvaldFormatException extends RecruitCodeException {
    public static final EmailTemplateInvaldFormatException EXCEPTION =
            new EmailTemplateInvaldFormatException();

    private EmailTemplateInvaldFormatException() {
        super(EmailTemplateErrorCode.EMAIL_TEMPLATE_INVALID_FORMAT);
    }
}
