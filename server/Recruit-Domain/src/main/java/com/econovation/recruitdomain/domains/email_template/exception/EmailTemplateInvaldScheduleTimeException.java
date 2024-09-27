package com.econovation.recruitdomain.domains.email_template.exception;

import com.econovation.recruitcommon.exception.RecruitCodeException;

public class EmailTemplateInvaldScheduleTimeException extends RecruitCodeException {
    public static final EmailTemplateInvaldScheduleTimeException EXCEPTION =
            new EmailTemplateInvaldScheduleTimeException();

    private EmailTemplateInvaldScheduleTimeException() {
        super(EmailTemplateErrorCode.EMAIL_TEMPLATE_INVALID_SCHEDULE_TIME);
    }
}
