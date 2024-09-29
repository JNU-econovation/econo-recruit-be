package com.econovation.recruitdomain.domains.email_template.exception;

import static com.econovation.recruitcommon.consts.RecruitStatic.BAD_REQUEST;
import static com.econovation.recruitcommon.consts.RecruitStatic.NOT_FOUND;

import com.econovation.recruitcommon.annotation.ExplainError;
import com.econovation.recruitcommon.exception.BaseErrorCode;
import com.econovation.recruitcommon.exception.ErrorReason;
import java.lang.reflect.Field;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EmailTemplateErrorCode implements BaseErrorCode {
    EMAIL_TEMPLATE_NOT_FOUND(NOT_FOUND, "EMAIL_TEMPLATE_404_1", "해당 템플릿을 찾을 수 없습니다."),
    EMAIL_TEMPLATE_INVALID_FORMAT(
            BAD_REQUEST, "EMAIL_TEMPLATE_400_1", "메일 발송 템플릿이 특수 문자(%s%)를 포함하고 있습니다."),
    EMAIL_TEMPLATE_INVALID_SCHEDULE_TIME(
            BAD_REQUEST, "EMAIL_TEMPLATE_400_2", "예약 시간이 현재 시간보다 이전입니다."),
    ;
    private Integer status;
    private String code;
    private String reason;

    @Override
    public ErrorReason getErrorReason() {
        return ErrorReason.builder().reason(reason).code(code).status(status).build();
    }

    @Override
    public String getExplainError() throws NoSuchFieldException {
        Field field = this.getClass().getField(this.name());
        ExplainError annotation = field.getAnnotation(ExplainError.class);
        return Objects.nonNull(annotation) ? annotation.value() : this.getReason();
    }
}
