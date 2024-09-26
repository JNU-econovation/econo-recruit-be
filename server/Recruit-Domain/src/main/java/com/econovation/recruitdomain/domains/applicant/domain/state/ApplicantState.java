package com.econovation.recruitdomain.domains.applicant.domain.state;

public class ApplicantState {

    private PassStates passState;

    public ApplicantState() {
        this.passState = PassStates.NON_PROCESSED; // 초기 상태
    }

    public void pass(PeriodStates period) {
        this.passState = this.passState.pass(period);
    }

    public void nonPass(PeriodStates period) {
        this.passState = this.passState.nonPass(period);
    }

    public String getPassState() {
        return this.passState.toString();
    }

    public PassStates getPassStateToEnum() {
        return this.passState;
    }

}
