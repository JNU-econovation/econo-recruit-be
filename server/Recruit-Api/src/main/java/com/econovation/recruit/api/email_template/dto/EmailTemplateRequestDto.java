package com.econovation.recruit.api.email_template.dto;

import com.econovation.recruitdomain.domains.email_template.domain.EmailTemplate;
import com.econovation.recruitdomain.domains.email_template.domain.EmailTemplateType;
import java.time.LocalDateTime;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmailTemplateRequestDto {
    @NotNull private String emailTemplateType;

    @Max(2000)
    @NotNull
    private String message;

    @NotNull private LocalDateTime scheduledTime;

    public static EmailTemplateRequestDto of(
            String emailTemplateType, String message, LocalDateTime scheduledTime) {
        return new EmailTemplateRequestDto(emailTemplateType, message, scheduledTime);
    }

    public EmailTemplate toEntity() {
        return EmailTemplate.builder()
                .emailTemplateType(EmailTemplateType.valueOf(this.emailTemplateType))
                .message(this.message)
                .scheduledTime(this.scheduledTime)
                .build();
    }
}
