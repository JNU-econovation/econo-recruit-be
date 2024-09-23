package com.econovation.recruitdomain.domains.email_template.domain;

import static com.econovation.recruitcommon.consts.RecruitStatic.PREFIX_EMATIL_TEMPLATE;
import static com.econovation.recruitcommon.consts.RecruitStatic.SUFFIX_EMATIL_TEMPLATE;

import com.econovation.recruitdomain.domains.BaseTimeEntity;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class EmailTemplate extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "email_template_id")
    private Long id;

    @Column(name = "template_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private EmailTemplateType emailTemplateType;

    @Column(name = "content", columnDefinition = "TEXT")
    private String message;
    // 예약 발송 시간

    @Column(name = "scheduled_time")
    private LocalDateTime scheduledTime;

    public void convertMessageToHtml() {
        this.message = PREFIX_EMATIL_TEMPLATE.concat(this.message).concat(SUFFIX_EMATIL_TEMPLATE);
    }
}
