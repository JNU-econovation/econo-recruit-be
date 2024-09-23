package com.econovation.recruit.api.email_template.docs;

import com.econovation.recruitcommon.annotation.ExceptionDoc;
import com.econovation.recruitcommon.annotation.ExplainError;
import com.econovation.recruitcommon.exception.RecruitCodeException;
import com.econovation.recruitcommon.interfaces.SwaggerExampleExceptions;
import com.econovation.recruitdomain.domains.applicant.exception.ApplicantDuplicateSubmitException;

@ExceptionDoc
public class ReadApplicantExceptionDocs implements SwaggerExampleExceptions {
    @ExplainError("지원서를 중복으로 제출했을 경우")
    public RecruitCodeException 지원서_중복_제출_예외 = ApplicantDuplicateSubmitException.EXCEPTION;
}
