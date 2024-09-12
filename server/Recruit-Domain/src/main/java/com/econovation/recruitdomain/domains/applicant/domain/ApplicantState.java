package com.econovation.recruitdomain.domains.applicant.domain;

public class ApplicantState {

    private PassStates passState;

    public ApplicantState(){
        this.passState = PassStates.NONPASSED; // 초기 상태
    }

    public void nextPassState(){
        this.passState = this.passState.next();
    }

    public void prevPassState(){
        this.passState = this.passState.prev();
    }

    public String getPassState(){
        return this.passState.getStatus();
    }

}
