package com.econovation.recruit.api.applicant.command;

import lombok.*;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@AllArgsConstructor
@ToString
@Data
@NoArgsConstructor
@Getter
public class UpdateApplicantStateCommand {

    @TargetAggregateIdentifier private String id;
    private String afterState;

}
