package com.econovation.recruit.api.applicant.controller;

import com.econovation.recruit.api.applicant.command.CreateAnswerCommand;
import com.econovation.recruit.api.applicant.docs.CreateApplicantExceptionDocs;
import com.econovation.recruit.api.applicant.dto.AnswersResponseDto;
import com.econovation.recruit.api.applicant.dto.GetApplicantsStatusResponse;
import com.econovation.recruit.api.applicant.usecase.ApplicantCommandUseCase;
import com.econovation.recruit.api.applicant.usecase.ApplicantQueryUseCase;
import com.econovation.recruit.api.applicant.usecase.TimeTableLoadUseCase;
import com.econovation.recruit.api.applicant.usecase.TimeTableRegisterUseCase;
import com.econovation.recruit.api.applicant.validate.ApplicantValidator;
import com.econovation.recruitcommon.annotation.ApiErrorExceptionsExample;
import com.econovation.recruitcommon.annotation.TimeTrace;
import com.econovation.recruitcommon.annotation.XssProtected;
import com.econovation.recruitdomain.domains.applicant.dto.TimeTableVo;
import com.econovation.recruitdomain.domains.dto.EmailSendDto;
import com.econovation.recruitdomain.domains.timetable.domain.TimeTable;
import com.econovation.recruitinfrastructure.apache.CommonsEmailSender;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.econovation.recruitcommon.consts.RecruitStatic.APPLICANT_SUCCESS_REGISTER_MESSAGE;
import static com.econovation.recruitcommon.consts.RecruitStatic.PASS_STATE_KEY;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "[1.0]. 지원서 API", description = "지원서 관련 API")
public class ApplicantController {

    private final TimeTableRegisterUseCase timeTableRegisterUseCase;
    private final TimeTableLoadUseCase timeTableLoadUseCase;
    private final ApplicantQueryUseCase applicantQueryUseCase;
    private final CommonsEmailSender commonsEmailSender;
    private final CommandGateway commandGateway;
    private final ApplicantValidator applicantValidator;
    private final ApplicantCommandUseCase applicantCommandUseCase;

    @Value("${econovation.year}")
    private Integer year;

    @Operation(summary = "지원자가 지원서를 작성합니다.", description = "반환 값은 생성된 지원자의 ID입니다.")
    @ApiErrorExceptionsExample(CreateApplicantExceptionDocs.class)
    @XssProtected
    @PostMapping("/applicants")
    @TimeTrace
    public ResponseEntity registerMongoApplicant(@RequestBody Map<String, Object> qna) {
        applicantValidator.validateRegisterApplicant(qna);
        String applicantId = UUID.randomUUID().toString();
        commandGateway.send(new CreateAnswerCommand(applicantId, year, qna));
        return new ResponseEntity<>(applicantId, HttpStatus.OK);
    }

    @Operation(summary = "지원자 id로 지원서를 조회합니다.")
    @TimeTrace
    @ApiErrorExceptionsExample(CreateApplicantExceptionDocs.class)
    @GetMapping("/applicants")
    public ResponseEntity<Map<String, Object>> getApplicantById(String applicantId) {
        return new ResponseEntity<>(applicantQueryUseCase.execute(applicantId), HttpStatus.OK);
    }

    @Operation(
            summary = "지원자 기수로 지원서를 조회합니다. / page는 1부터 시작합니다.",
            description = "order는 name, newest, objective중 하나입니다.")
    @TimeTrace
    @GetMapping("/page/{page}/year/{year}/applicants")
    public ResponseEntity<AnswersResponseDto> getApplicantsByYear(
            @PathVariable(value = "year") Integer year,
            @PathVariable(value = "page") Integer page,
            @ParameterObject String order) {
        AnswersResponseDto result = applicantQueryUseCase.execute(year, page, order);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(summary = "모든 지원자의 지원서를 조회합니다.")
    @TimeTrace
    @GetMapping("/applicants/all")
    public ResponseEntity<List<Map<String, Object>>> getApplicants() {
        return new ResponseEntity<>(applicantQueryUseCase.execute(), HttpStatus.OK);
    }

    @Operation(summary = "지원서를 검색합니다.", description = "지원자가 지원서를 작성합니다.")
    @ApiErrorExceptionsExample(CreateApplicantExceptionDocs.class)
    @GetMapping("/page/{page}/search/{search-keyword}/applicants")
    public ResponseEntity<AnswersResponseDto> searchApplicant(
            @PathVariable(value = "page") Integer page,
            @PathVariable(value = "search-keyword") String searchKeyword) {
        AnswersResponseDto result = applicantQueryUseCase.search(page, searchKeyword);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(summary = "지원자가 면접 가능 시간을 작성합니다.")
    @ApiErrorExceptionsExample(CreateApplicantExceptionDocs.class)
    @TimeTrace
    @PostMapping("/applicants/{applicant-id}/timetables")
    public ResponseEntity registerApplicantTimeTable(
            @PathVariable(value = "applicant-id") String applicantId,
            @RequestBody List<Integer> startTimes) {
        timeTableRegisterUseCase.execute(applicantId, startTimes);
        return new ResponseEntity<>(APPLICANT_SUCCESS_REGISTER_MESSAGE, HttpStatus.OK);
    }

    @Operation(summary = "모든 면접 가능 시간을 조회합니다.")
    @TimeTrace
    @GetMapping("/timetables")
    public ResponseEntity<List<Map<String, List<TimeTableVo>>>> getTimeTables() {
        return new ResponseEntity(timeTableLoadUseCase.findAll(), HttpStatus.OK);
    }

    @Operation(summary = "지원자의 면접 가능 시간을 조회합니다.")
    @GetMapping("/applicants/{applicant-id}/timetables")
    @TimeTrace
    public ResponseEntity<List<TimeTable>> getTimeTables(
            @PathVariable(name = "applicant-id") String applicantId) {
        List<Integer> timeTableDto = timeTableLoadUseCase.getTimeTableByApplicantId(applicantId);
        return new ResponseEntity(timeTableDto, HttpStatus.OK);
    }

    @Operation(summary = "면접 가능 시간마다 일치하는 지원자의 정보(희망분야, 이름)를 조회합니다.")
    @TimeTrace
    @GetMapping("/timetables/applicants")
    public ResponseEntity<Map<Integer, List<String>>> getApplicantsByTimeTable() {
        return new ResponseEntity(
                timeTableLoadUseCase.findAllSimpleApplicantWithTimeTable(), HttpStatus.OK);
    }

    @Operation(summary = "지원서 제출한 html을 email 로 전송합니다.")
    @TimeTrace
    @PostMapping("/applicants/mail")
    public ResponseEntity sendEmail(@RequestBody EmailSendDto emailSendDto) {
        commonsEmailSender.send(
                emailSendDto.getEmail(), emailSendDto.getApplicantId(), LocalDateTime.now());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "지원자의 합/불 상태를 변경합니다.")
    @PatchMapping("/applicants/{applicant-id}/state")
    public ResponseEntity<Map<String, String>> updateStatus(@PathVariable("applicant-id") String applicantId,
                                               @RequestParam("afterState") String afterState){
//        commandGateway.send(new UpdateApplicantStateCommand(applicantId, afterState));
        String state = applicantCommandUseCase.execute(applicantId, afterState);
        Map<String, String> response = new HashMap<>();
        response.put(PASS_STATE_KEY, state);
        return new ResponseEntity(response,HttpStatus.OK);
    }

    @Operation(summary = "지원서의 합/불 상태를 조회합니다. (합/불 관리자 페이지 전용)")
    @GetMapping("year/{year}/applicants/pass-state")
    public ResponseEntity<List<GetApplicantsStatusResponse>> getApplicantsStatus(@PathVariable("year") Integer year,
                                                                           @RequestParam("order") String sortType) {
        List<GetApplicantsStatusResponse> result = applicantQueryUseCase.getApplicantsStatus(year, sortType);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
