package com.econovation.recruit.api.email_template.service;

import com.econovation.recruit.api.email_template.dto.EmailTemplateRequestDto;
import com.econovation.recruit.api.email_template.usecase.EmailTemplateLoadUseCase;
import com.econovation.recruit.api.email_template.usecase.EmailTemplateRegisterUseCase;
import com.econovation.recruitcommon.exception.RecruitCodeException;
import com.econovation.recruitdomain.domains.email_template.adaptor.EmailTemplateAdaptor;
import com.econovation.recruitdomain.domains.email_template.domain.EmailTemplate;
import com.econovation.recruitdomain.domains.email_template.exception.EmailTemplateInvaldScheduleTimeException;
import io.vavr.collection.Seq;
import io.vavr.control.Validation;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmailTemplateService
        implements EmailTemplateRegisterUseCase, EmailTemplateLoadUseCase {
    private final EmailTemplateAdaptor emailTemplateAdaptor;
    //    private final EmailSendScheduler emailSendScheduler;

    @Override
    public EmailTemplate findById(Long emailTemplateId) {
        return emailTemplateAdaptor.findById(emailTemplateId);
    }

    @Override
    @Transactional
    public EmailTemplate execute(EmailTemplateRequestDto emailTemplateDto) {
        EmailTemplate emailTemplate = emailTemplateDto.toEntity();
        emailTemplate.convertMessageToHtml();

        validateEmailTemplate(emailTemplate);
        emailTemplateAdaptor.save(emailTemplate);
        // 이메일 발송 예약
        //        emailSendScheduler.scheduleEmailSend(emailTemplate);
        return emailTemplate;
    }

    private Validation<Seq<RecruitCodeException>, EmailTemplate> validateEmailTemplate(
            EmailTemplate emailTemplate) {
        return Validation.combine(
                        validateMessage(emailTemplate), validateScheduleTime(emailTemplate))
                .ap((a, b) -> emailTemplate);
    }

    private Validation<RecruitCodeException, EmailTemplate> validateMessage(
            EmailTemplate emailTemplate) {
        return Validation.valid(emailTemplate);
    }

    private Validation<RecruitCodeException, EmailTemplate> validateScheduleTime(
            EmailTemplate emailTemplate) {
        if (emailTemplate.getScheduledTime().isBefore(LocalDateTime.now())) {
            throw EmailTemplateInvaldScheduleTimeException.EXCEPTION;
        }
        return Validation.valid(emailTemplate);
    }
}
