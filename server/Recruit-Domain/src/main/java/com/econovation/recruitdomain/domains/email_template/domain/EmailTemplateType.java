package com.econovation.recruitdomain.domains.email_template.domain;

import com.econovation.recruitcommon.annotation.EnumClass;

@EnumClass
public enum EmailTemplateType {
    // 서류합격
    DOCUMENT_PASS("서류 합격", "document_pass"),
    // 서류불합격
    DOCUMENT_FAIL("서류 불합격", "document_fail"),
    // 면접합격
    INTERVIEW_PASS("면접 합격", "interview_pass"),
    // 면접불합격
    INTERVIEW_FAIL("면접 불합격", "interview_fail"),
    ;
    private String name;
    private String type;

    EmailTemplateType(String name, String type) {
        this.name = name;
        this.type = type;
    }
}
