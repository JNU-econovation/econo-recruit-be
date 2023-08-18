package com.econovation.recruitdomain.domains.label;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Label {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "label_id")
    private Integer id;

    @Column(name = "applicant_id")
    private Integer applicantId;

    @Column(name = "idpId")
    private Long idpId;
}
