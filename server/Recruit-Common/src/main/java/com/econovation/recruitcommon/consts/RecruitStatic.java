package com.econovation.recruitcommon.consts;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RecruitStatic {
    public static final String AUTH_HEADER = "Authorization";
    public static final String BEARER = "Bearer ";
    public static final String TOKEN_ROLE = "role";
    public static final String TOKEN_TYPE = "type";
    public static final String EMPTY_STRING = "";
    public static final String TOKEN_ISSUER = "Recruit";
    public static final String ACCESS_TOKEN = "ACCESS_TOKEN";
    public static final String REFRESH_TOKEN = "REFRESH_TOKEN";

    public static final String SET_COOKIE = "Set-Cookie";
    public static final Integer DEVELOPER_COLUMNS_ID = 1;
    public static final Integer DESIGNER_COLUMNS_ID = 2;
    public static final Integer PLANNER_COLUMNS_ID = 3;
    //    itemsPerPage
    public static final Integer PAGE_SIZE = 10;
    public static final Integer ANSWER_COUNTS_PER_PERSON = 33;

    public static final int MILLI_TO_SECOND = 1000;
    public static final int BAD_REQUEST = 400;
    public static final int UNAUTHORIZED = 401;
    public static final int FORBIDDEN = 403;

    public static final int NOT_FOUND = 404;
    public static final int TOO_MANY_REQUESTS = 429;
    public static final int INTERNAL_SERVER = 500;
    public static final String COMMENT_LIKE_SUCCESS_UPDATE_MESSAGE = "성공적으로 댓글 좋아요가 수정됐습니다.";
    public static Set<String> CRETERIA_SET =
            new HashSet<>(Arrays.asList("열정/실천력", "동아리 애착", "협업", "태도"));

    public static final String PASS_STATE_KEY = "passState";

    public static final String APPLICANT_SUCCESS_REGISTER_MESSAGE = "성공적으로 지원됐습니다";
    public static final String NO_MATCH_INTERVIEWER_MESSAGE = "해당하는 면접관이 없습니다.";
    public static final String BOARD_SUCCESS_REGISTER_MESSAGE = "성공적으로 업무카드가 등록됐습니다";
    public static final String BOARD_SUCCESS_DELETE_MESSAGE = "성공적으로 업무카드가 삭제되었습니다.";
    public static final String LABEL_SUCCESS_DELETE_MESSAGE = "성공적으로 라벨이 삭제되었습니다.";
    public static final String LABEL_SUCCESS_DELETE_IN_CREATE_MESSAGE = "성공적으로 라벨이 삭제되었습니다.";
    public static final String LABEL_SUCCESS_CREATE_MESSAGE = "성공적으로 라벨이 생성되었습니다.";
    public static final String NO_MATCH_COMMENT_MESSAGE = "조회된 Comment가 존재하지 않습니다";
    public static final String COMMENT_SUCCESS_REGISTER_MESSAGE = "성공적으로 댓글이 등록됐습니다";
    public static final String COMMENT_LIKE_SUCCESS_REGISTER_MESSAGE = "성공적으로 댓글 좋아요를 등록됐습니다";
    public static final String COMMENT_LIKE_SUCCESS_DELETE_MESSAGE = "성공적으로 댓글 좋아요가 삭제되었습니다.";
    public static final String SCORE_SUCCESS_REGISTER_MESSAGE = "성공적으로 점수가 등록됐습니다";
    public static final String SCORE_SUCCESS_UPDATE_MESSAGE = "성공적으로 점수가 수정됐습니다";
    public static final String INTERVIEWER_SUCCESS_REGISTER_MESSAGE = "성공적으로 면접관이 등록됐습니다";
    public static final String INTERVIEWER_SUCCESS_DELETE_MESSAGE = "성공적으로 면접관이 삭제됐습니다";
    public static final String RECORD_SUCCESS_CREATE_MESSAGE = "성공적으로 지원자의 면접 기록이 생성됐습니다";
    public static final String COLUMN_SUCCESS_REGISTER_MESSAGE = "성공적으로 Column이 등록됐습니다";
    public static final String BOARD_SUCCESS_LOCATION_CHANGE_MESSAGE = "성공적으로 카드의 위치가 변경되었습니다.";
    public static final String QUESTION_SUCCESS_REGISTER_MESSAGE = "성공적으로 질문이 등록됐습니다";
    public static final String BOARD_SUCCESS_UPDATE_MESSAGE = "성공적으로 업무카드가 수정됐습니다";
    public static final String RECORD_SUCCESS_UPDATE_MESSAGE = "성공적으로 지원자의 면접 기록이 수정됐습니다";
    public static final String INTERVIEWER_SUCCESS_SIGNUP_MESSAGE = "성공적으로 면접관이 등록됐습니다";
    public static final String PASSWORD_SUCCESS_CHANGE_MESSAGE = "성공적으로 비밀번호가 변경됐습니다";
    public static final String COLUMN_SUCCESS_LOCATION_CHANGE_MESSAGE =
            "성공적으로 Column의 위치가 변경되었습니다.";
    public static final String COMMENT_SUCCESS_UPDATE_MESSAGE = "성공적으로 댓글이 수정됐습니다";
    public static final String WORK_CARD_SUCCESS_UPDATE_MESSAGE = "성공적으로 업무카드가 수정됐습니다";
    public static final List<String> TIMETABLE_APPLICANT_FIELD = List.of("field", "name");

    public static final String LOGOUT_SUCCESS_MESSAGE = "성공적으로 로그아웃 됐습니다";

    public static final String[] SwaggerPatterns = {
        "/swagger-resources/**",
        "/swagger-ui/**",
        "/swagger-ui.html",
        "/v3/api-docs/**",
        "/v3/api-docs",
        "/api-docs/**",
        "/api-docs"
    };
    public static final String[] RolePattern = {
        "ROLE_TF", "ROLE_PRESIDENT", "ROLE_OPERATION", "ROLE_SWAGGER"
    };
}
