package com.econovation.recruitdomain.domains.applicant.exception;

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
public enum ApplicantErrorCode implements BaseErrorCode {
    APPLICANT_NOT_FOUND(NOT_FOUND, "APPLICANT_NOT_FOUND", "지원자를 찾을 수 없습니다."),
    APPLICANT_DUPLICATE_SUBMIT(BAD_REQUEST, "APPLICANT_400_1", "이미 지원한 지원자입니다."),
    APPLICANT_PROHIBIT_DELETE(BAD_REQUEST, "APPLICANT_400_2", "지원자를 삭제할 수 없습니다."),
    APPLICANT_WRONG_POSITION(BAD_REQUEST, "APPLICANT_400_3", "디자이너, 개발자, 기획자 중 하나의 포지션을 선택해주세요."),
    APPLICANT_WRONG_STATE(BAD_REQUEST, "APPLICANT_400_4", "올바른 합/불 상태를 선택해주세요.")
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
