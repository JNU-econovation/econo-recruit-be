package com.econovation.recruit.api.interviewer.controller;

import static com.econovation.recruitcommon.consts.RecruitStatic.INTERVIEWER_SUCCESS_DELETE_MESSAGE;
import static com.econovation.recruitcommon.consts.RecruitStatic.INTERVIEWER_SUCCESS_REGISTER_MESSAGE;

import com.econovation.recruit.api.interviewer.docs.InterviewerDeleteExceptionDocs;
import com.econovation.recruit.api.interviewer.docs.InterviewerExceptionDocs;
import com.econovation.recruit.api.interviewer.usecase.InterviewerUseCase;
import com.econovation.recruitcommon.annotation.ApiErrorExceptionsExample;
import com.econovation.recruitcommon.annotation.DevelopOnlyApi;
import com.econovation.recruitdomain.domains.dto.InterviewerResponseDto;
import com.econovation.recruitdomain.domains.interviewer.domain.Interviewer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "[4.0] Interviewer 면접관 API", description = "Interviewer 면접관 API")
@RequestMapping("/api/v1")
public class InterviewerController {
    private final InterviewerUseCase interviewerUseCase;

    @Operation(description = "Interviewer 조회", summary = "면접관 조회")
    @ApiErrorExceptionsExample(InterviewerExceptionDocs.class)
    @GetMapping("/idp/{idp_id}/interviewers")
    public ResponseEntity<Interviewer> findByApplicantId(
            @PathVariable(name = "idp_id") Long idpId) {
        Interviewer interviewer = interviewerUseCase.getById(idpId);
        return new ResponseEntity(interviewer, HttpStatus.OK);
    }

    @Operation(description = "Interviewer 전체 조회", summary = "면접관 전체 조회")
    @ApiErrorExceptionsExample(InterviewerExceptionDocs.class)
    @GetMapping("/interviewers")
    public ResponseEntity<List<InterviewerResponseDto>> findAll(
            @ParameterObject String order, @RequestParam(required = false) List<String> roles) {
        List<InterviewerResponseDto> interviewers = interviewerUseCase.findAll(order);
        return new ResponseEntity(interviewers, HttpStatus.OK);
    }

    @Operation(description = "idpId로 Interviewer 등록", summary = "면접관 등록")
    @PostMapping("/interviewers")
    public ResponseEntity<String> createInterviewers(@RequestBody List<Long> idpIds) {
        interviewerUseCase.createInterviewers(idpIds);
        return new ResponseEntity<>(INTERVIEWER_SUCCESS_REGISTER_MESSAGE, HttpStatus.OK);
    }

    @DevelopOnlyApi
    @Operation(description = "임시 Interviewer 등록", summary = "임시 면접관 등록")
    @PostMapping("/interviewers/temp")
    public ResponseEntity<String> createInterviewers() {
        interviewerUseCase.createTempInterviewers();
        return new ResponseEntity<>(INTERVIEWER_SUCCESS_REGISTER_MESSAGE, HttpStatus.OK);
    }

    @Operation(description = "name으로 Interviewer 등록", summary = "면접관 등록")
    @PostMapping("/interviewers/name")
    public ResponseEntity<String> createInterviewersByName(@RequestBody List<String> names) {
        interviewerUseCase.createInterviewersByName(names);
        return new ResponseEntity<>(INTERVIEWER_SUCCESS_REGISTER_MESSAGE, HttpStatus.OK);
    }

    @Operation(description = "내 Interviewer 조회", summary = "내(면접관) 정보 조회")
    @ApiErrorExceptionsExample(InterviewerExceptionDocs.class)
    @GetMapping("/interviewers/me")
    public ResponseEntity<InterviewerResponseDto> findMyInterviewer() {
        InterviewerResponseDto interviewer = interviewerUseCase.findMe();
        return new ResponseEntity(interviewer, HttpStatus.OK);
    }

    @Operation(description = "Interviewer Role 변경")
    @PutMapping("/interviewers/{idp-id}/roles")
    public ResponseEntity<String> updateRole(
            @PathVariable(name = "idp-id") Long idpId, String role) {
        interviewerUseCase.updateRole(idpId, role);
        return new ResponseEntity<>("Success", HttpStatus.OK);
    }

    @Operation(description = "Interviewer 삭제")
    @ApiErrorExceptionsExample(InterviewerDeleteExceptionDocs.class)
    @DeleteMapping("/interviewers/{idp-id}")
    public ResponseEntity<String> deleteInterviewer(@PathVariable(name = "idp-id") Long idpId) {
        interviewerUseCase.deleteInterviewer(idpId);
        return new ResponseEntity<>(INTERVIEWER_SUCCESS_DELETE_MESSAGE, HttpStatus.OK);
    }
}
