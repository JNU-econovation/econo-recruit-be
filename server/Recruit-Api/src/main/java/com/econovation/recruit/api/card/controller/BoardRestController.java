package com.econovation.recruit.api.card.controller;

import static com.econovation.recruitcommon.consts.RecruitStatic.*;

import com.econovation.recruit.api.applicant.usecase.ApplicantQueryUseCase;
import com.econovation.recruit.api.card.docs.CreateBoardExceptionDocs;
import com.econovation.recruit.api.card.docs.CreateColumnsExceptionDocs;
import com.econovation.recruit.api.card.docs.CreateNavigationExceptionDocs;
import com.econovation.recruit.api.card.docs.DeleteBoardExceptionDocs;
import com.econovation.recruit.api.card.docs.FindBoardExceptionDocs;
import com.econovation.recruit.api.card.docs.FindNavigationExceptionDocs;
import com.econovation.recruit.api.card.docs.UpdateBoardExceptionDocs;
import com.econovation.recruit.api.card.usecase.BoardLoadUseCase;
import com.econovation.recruit.api.card.usecase.BoardRegisterUseCase;
import com.econovation.recruit.api.card.usecase.CardLoadUseCase;
import com.econovation.recruit.api.card.usecase.CardRegisterUseCase;
import com.econovation.recruit.api.card.usecase.NavigationUseCase;
import com.econovation.recruitcommon.annotation.ApiErrorExceptionsExample;
import com.econovation.recruitdomain.domains.board.domain.Navigation;
import com.econovation.recruitdomain.domains.board.dto.ColumnsResponseDto;
import com.econovation.recruitdomain.domains.card.dto.BoardCardResponseDto;
import com.econovation.recruitdomain.domains.card.dto.CardResponseDto;
import com.econovation.recruitdomain.domains.dto.CreateWorkCardDto;
import com.econovation.recruitdomain.domains.dto.UpdateLocationBoardDto;
import com.econovation.recruitdomain.domains.dto.UpdateLocationColumnDto;
import com.econovation.recruitdomain.domains.dto.UpdateWorkCardDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@SecurityRequirement(name = "access-token")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "[2.0]. 칸반보드 API", description = "칸반보드 관련 API")
@Slf4j
public class BoardRestController {
    private final BoardLoadUseCase boardLoadUseCase;
    private final BoardRegisterUseCase boardRecordUseCase;
    private final CardRegisterUseCase cardRegisterUseCase;
    private final CardLoadUseCase cardLoadUseCase;
    private final NavigationUseCase navigationUseCase;
    private final ApplicantQueryUseCase answerQueryUseCase;
    //    ---------- Navigation ----------
    @Operation(summary = "네비게이션 바 생성", description = "카드 생성 전에 네비게이션 바를 생성하셔야 합니다.")
    @ApiErrorExceptionsExample(value = CreateNavigationExceptionDocs.class)
    @PostMapping("/boards/navigations")
    public ResponseEntity<Navigation> createNavigation(String navTitle) {
        Navigation navigation = navigationUseCase.createNavigation(navTitle);
        return new ResponseEntity<>(navigation, HttpStatus.OK);
    }

    @Operation(summary = "네비게이션 바 수정", description = "navigation 수정.")
    @ApiErrorExceptionsExample(value = CreateNavigationExceptionDocs.class)
    @PostMapping("/boards/navigations/{navigation-id}/update")
    public ResponseEntity<Navigation> createNavigation(
            @PathVariable(name = "navigation-id") Integer navigationId,
            @RequestBody String navTitle) {
        Navigation navigation = navigationUseCase.updateNavigationByNavLoc(navigationId, navTitle);
        return new ResponseEntity<>(navigation, HttpStatus.OK);
    }

    @Operation(summary = "네비게이션 바 조회", description = "navigationId에 해당하는 네비게이션 바를 조회합니다.")
    @GetMapping("/boards/navigations/{navigation-id}")
    @ApiErrorExceptionsExample(value = FindNavigationExceptionDocs.class)
    public ResponseEntity<Navigation> getNavigationByNavLoc(
            @PathVariable(name = "navigation-id") Integer navigationId) {
        return new ResponseEntity(
                boardLoadUseCase.getNavigationByNavLoc(navigationId), HttpStatus.OK);
    }

    @Operation(summary = "네비게이션 바 전체 조회", description = "네비게이션 바 전체를 조회합니다.")
    @ApiErrorExceptionsExample(value = FindNavigationExceptionDocs.class)
    @GetMapping("/boards/navigations")
    public ResponseEntity<List<Navigation>> getAllNavigation() {
        List<Navigation> navigations = boardLoadUseCase.getAllNavigation();
        return new ResponseEntity<>(navigations, HttpStatus.OK);
    }

    @Operation(summary = "네비게이션 바 삭제", description = "navigationId에 해당하는 네비게이션 바를 삭제합니다.")
    @ApiErrorExceptionsExample(value = FindNavigationExceptionDocs.class)
    @PostMapping("/boards/navigations/{navigation-id}/delete")
    public ResponseEntity deleteNavigation(
            @PathVariable(name = "navigation-id") Integer navigationId) {
        navigationUseCase.deleteById(navigationId);
        return new ResponseEntity(HttpStatus.OK);
    }

    @Operation(summary = "지원서 칸반보드 열(세로줄) 생성", description = "지원서 칸반보드 열(세로줄) 생성")
    @ApiErrorExceptionsExample(CreateColumnsExceptionDocs.class)
    @PostMapping("/boards/navigations/{navigation-id}/columns")
    public ResponseEntity<String> createBoardColumn(
            @PathVariable("navigation-id") Integer navigationId, String title) {
        boardRecordUseCase.createColumn(title, navigationId);
        return new ResponseEntity(COLUMN_SUCCESS_REGISTER_MESSAGE, HttpStatus.OK);
    }

    @Operation(summary = "지원서 칸반보드 열(세로줄) 위치 변경", description = "지원서 칸반보드 열(세로줄) 위치 변경")
    @ApiErrorExceptionsExample(CreateColumnsExceptionDocs.class)
    @PutMapping("/boards/columns")
    public ResponseEntity<String> updateBoardColumn(
            @RequestBody UpdateLocationColumnDto updateLocationDto) {
        boardRecordUseCase.updateColumnLocation(updateLocationDto);
        return new ResponseEntity(COLUMN_SUCCESS_LOCATION_CHANGE_MESSAGE, HttpStatus.OK);
    }

    @Operation(
            summary = "지원서 세로줄 조회(by NavigationId)",
            description = "navigationId에 해당하는 모든 세로줄을 조회합니다., 세로줄이 없으면 빈 배열을 반환합니다.")
    @GetMapping("/boards/navigations/{navigation-id}/columns")
    public ResponseEntity<List<ColumnsResponseDto>> getBoardColumnByNavigationId(
            @PathVariable("navigation-id") Integer navigationId) {
        return new ResponseEntity<>(
                boardLoadUseCase.getColumnsByNavigationId(navigationId), HttpStatus.OK);
    }
    // 칸반보드 전체 조회 by navLoc
    @Operation(summary = "업무 칸반보드 생성", description = "업무 칸반(지원자가 아닌) 생성")
    @ApiErrorExceptionsExample(CreateBoardExceptionDocs.class)
    @PostMapping("/boards/work-cards")
    public ResponseEntity<String> createWorkBoard(
            @RequestBody CreateWorkCardDto createWorkCardDto) {
        cardRegisterUseCase.saveWorkCard(createWorkCardDto);
        return new ResponseEntity(BOARD_SUCCESS_REGISTER_MESSAGE, HttpStatus.OK);
    }

    @Operation(summary = "업무 칸반보드 수정", description = "업무 칸반(지원자가 아닌) 내용,제목 ( 둘중 하나만 보내주셔도 됩니다 ) 수정")
    @ApiErrorExceptionsExample(CreateBoardExceptionDocs.class)
    @PutMapping("/boards/cards/{card-id}")
    public ResponseEntity<String> updateWorkBoardContent(
            @PathVariable(name = "card-id") Long cardId,
            @RequestBody UpdateWorkCardDto updateWorkCardDto) {
        cardRegisterUseCase.update(cardId, updateWorkCardDto);
        return new ResponseEntity(WORK_CARD_SUCCESS_UPDATE_MESSAGE, HttpStatus.OK);
    }

    @Operation(summary = "업무 카드 조회", description = "업무 카드 조회")
    @GetMapping("/cards/{card-id}")
    public ResponseEntity<CardResponseDto> getWorkCard(
            @PathVariable(name = "card-id") Long cardId) {
        return new ResponseEntity<>(cardLoadUseCase.findCardById(cardId), HttpStatus.OK);
    }

    @Operation(summary = "지원서 칸반보드 위치 수정")
    @ApiErrorExceptionsExample(UpdateBoardExceptionDocs.class)
    @PostMapping("/boards/locations")
    public ResponseEntity<String> updateLocationBoard(
            @RequestBody UpdateLocationBoardDto updateLocationBoardDto) {
        boardRecordUseCase.relocateCard(updateLocationBoardDto);
        return new ResponseEntity(BOARD_SUCCESS_LOCATION_CHANGE_MESSAGE, HttpStatus.OK);
    }

    @Operation(
            summary = "지원서 칸반보드 조회 by navigationId",
            description = "navigationId에 해당하는 모든 칸반을 조회합니다.")
    @GetMapping("/navigations/{navigation-id}/boards")
    public ResponseEntity<List<BoardCardResponseDto>> getBoardByNavigationId(
            @PathVariable("navigation-id") Integer navigationId,
            @ParameterObject Integer year) {
        return new ResponseEntity<>(cardLoadUseCase.getByNavigationId(navigationId, year), HttpStatus.OK);
    }

    @Operation(summary = "지원서 조회(원하는 field) 만 조회", description = "원하는 field만(리스트) 조회합니다.")
    @ApiErrorExceptionsExample(FindBoardExceptionDocs.class)
    @PostMapping("/page/{page}/boards")
    public ResponseEntity<List<Map<String, Object>>> getBoardByNavigationId(
            @PathVariable(name = "page") Integer page, @RequestBody List<String> fields) {
        return new ResponseEntity<>(answerQueryUseCase.execute(fields, page), HttpStatus.OK);
    }

    @Operation(
            summary = "특정 지원서 조회(원하는 field) 만 조회",
            description = "특정 지원자에 대하여 원하는 field만(리스트) 조회합니다.")
    @ApiErrorExceptionsExample(FindBoardExceptionDocs.class)
    @PostMapping("/boards/{applicant-id}")
    public ResponseEntity<Map<String, Object>> getBoardByNavigationId(
            @PathVariable(name = "applicant-id") String applicantId,
            @RequestBody List<String> fields) {
        return new ResponseEntity<>(answerQueryUseCase.execute(applicantId, fields), HttpStatus.OK);
    }

    @Operation(summary = "카드 삭제", description = "업무 카드를 삭제합니다(지원서 삭제는 불가합니다.)")
    @ApiErrorExceptionsExample(DeleteBoardExceptionDocs.class)
    @DeleteMapping("/cards/{card-id}")
    public ResponseEntity<String> deleteCard(@PathVariable(name = "card-id") Long cardId) {
        cardRegisterUseCase.deleteById(cardId);
        return new ResponseEntity<>(BOARD_SUCCESS_DELETE_MESSAGE, HttpStatus.OK);
    }
}
