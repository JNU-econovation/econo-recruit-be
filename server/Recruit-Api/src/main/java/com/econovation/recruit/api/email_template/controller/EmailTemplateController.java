package com.econovation.recruit.api.email_template.controller;

import com.econovation.recruit.api.email_template.docs.ReadEmailTemplateExceptionDocs;
import com.econovation.recruit.api.email_template.dto.EmailTemplateRequestDto;
import com.econovation.recruit.api.email_template.usecase.EmailTemplateLoadUseCase;
import com.econovation.recruit.api.email_template.usecase.EmailTemplateRegisterUseCase;
import com.econovation.recruitcommon.annotation.ApiErrorExceptionsExample;
import com.econovation.recruitdomain.domains.email_template.domain.EmailTemplate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "[1.0]. 지원서 API", description = "지원서 관련 API")
public class EmailTemplateController {

    private final EmailTemplateRegisterUseCase emailTemplateRegisterUseCase;
    private final EmailTemplateLoadUseCase emailTemplateLoadUseCase;

    @Operation(summary = "이메일 템플릿 조회", description = "이메일 템플릿을 id로 조회합니다.")
    @ApiErrorExceptionsExample(ReadEmailTemplateExceptionDocs.class)
    @GetMapping("/email-templates/{emailTemplateId}")
    public ResponseEntity<EmailTemplate> getApplicantById(Long applicantId) {
        return new ResponseEntity<>(emailTemplateLoadUseCase.findById(applicantId), HttpStatus.OK);
    }

    @Operation(summary = "이메일 템플릿 생성", description = "이메일 템플릿을 생성합니다.")
    @PostMapping("/email-templates")
    public ResponseEntity<EmailTemplate> createApplicant(
            @RequestBody EmailTemplateRequestDto emailTemplateRequestDto) {
        return new ResponseEntity<>(
                emailTemplateRegisterUseCase.execute(emailTemplateRequestDto), HttpStatus.OK);
    }
    //    @ApiErrorExceptionsExample(CreateEmailTemplateExceptionDocs.class)

}
