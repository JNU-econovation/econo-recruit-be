package com.econovation.recruit.utils;

import com.econovation.recruit.api.applicant.usecase.ApplicantCommandUseCase;
import com.econovation.recruit.api.applicant.usecase.ApplicantQueryUseCase;
import io.vavr.concurrent.Future;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApplicantStateCheck implements ApplicationRunner {

    private final ApplicantCommandUseCase applicantCommandUseCase;
    private final ApplicantQueryUseCase applicantQueryUseCase;

    @PostConstruct
    public void init() throws IOException, SQLException {
        // init.sql 파일을 읽어와서 실행합니다.
        log.info("MongoDB Applicant State 체크를 시작합니다.");
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<Map<String, Object>> answers = applicantQueryUseCase.execute();
        Future.of(() -> answers)
                .map(
                        answer -> {
                            return answer.stream()
                                    .map(
                                            map -> {
                                                Map<String, Object> qna =
                                                        map.entrySet().stream()
                                                                .collect(
                                                                        Collectors.toMap(
                                                                                Map.Entry::getKey,
                                                                                Map.Entry::getValue));
                                                applicantCommandUseCase.execute(qna, UUID.fromString((String)qna.get("id")));
                                                return qna;
                                            })
                                    .collect(Collectors.toList());
                        })
                .onSuccess(
                        (qna) -> {
                            log.info("MongoDB Applicant State Check를 완료했습니다.");
                        })
                .onFailure(
                        (exception) -> {
                            exception.printStackTrace();
                            log.error("MongoDB Applicant State Check를 실패했습니다.");
                        });
    }
}
