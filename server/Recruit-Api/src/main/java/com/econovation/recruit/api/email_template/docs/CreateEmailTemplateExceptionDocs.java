package com.econovation.recruit.api.email_template.docs;

import com.econovation.recruitcommon.annotation.ExceptionDoc;
import com.econovation.recruitcommon.annotation.ExplainError;
import com.econovation.recruitcommon.exception.RecruitCodeException;
import com.econovation.recruitcommon.interfaces.SwaggerExampleExceptions;
import com.econovation.recruitdomain.domains.email_template.exception.EmailTemplateErrorCode;

@ExceptionDoc
public class CreateEmailTemplateExceptionDocs implements SwaggerExampleExceptions {
    @ExplainError("메일 발송 템플릿_포맷_오류")
    public RecruitCodeException 메일_발송_템플릿_포맷_오류 =
            new RecruitCodeException(EmailTemplateErrorCode.EMAIL_TEMPLATE_INVALID_FORMAT);
}
