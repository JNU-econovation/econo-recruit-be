package com.econovation.recruit.api.email.service;

import static com.econovation.recruitdomain.domains.applicant.domain.state.PassStates.FINAL_FAILED;
import static com.econovation.recruitdomain.domains.applicant.domain.state.PassStates.FINAL_PASSED;

import com.econovation.recruit.api.applicant.usecase.ApplicantQueryUseCase;
import com.econovation.recruitdomain.domains.applicant.domain.MongoAnswer;
import com.econovation.recruitdomain.domains.applicant.domain.state.PassStates;
import com.econovation.recruitinfrastructure.apache.CommonsEmailSender;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@RequiredArgsConstructor
public class ApplicantPassEmailScheduler {
    private final CommonsEmailSender emailSender;
    private final ApplicantQueryUseCase applicantQueryUseCase;
    private final Integer MAX_EMAIL_SEND_RETRY = 10;

    @Value("${econovation.year}")
    private Integer year;

    private File attachment;

    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 30000))
    @SneakyThrows
    @Async
    @Scheduled(cron = "${econovation.recruit.period.finalDiscussion}", zone = "Asia/Seoul")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handle() {
        int startIndex = 0;
        int batchSize = 14;
        List<MongoAnswer> applicants;
        Queue<MongoAnswer> failQueue = new LinkedList<>();
        Map<MongoAnswer, Integer> retryCounts = new HashMap<>(); // Map to track retry counts
        do {
            applicants = applicantQueryUseCase.getApplicantsByYear(year);
            List<MongoAnswer> batch = new ArrayList<>(batchSize);
            for (MongoAnswer applicant : applicants) {
                batch.add(applicant);
                if (batch.size() == batchSize) {
                    processBatch(batch, failQueue, retryCounts);
                    batch.clear();
                    TimeUnit.MILLISECONDS.sleep(300);
                }
            }
            if (!batch.isEmpty()) {
                processBatch(batch, failQueue, retryCounts);
                batch.clear();
                TimeUnit.MILLISECONDS.sleep(300);
            }
            if (startIndex >= 10) break;
            startIndex++;
        } while (applicants.iterator().hasNext());
        failOver(failQueue, retryCounts);
    }

    // 포트폴리오 파일을 가져오는 메서드
    private File getPortfolioFile() {
        // applicant에서 포트폴리오 파일 경로나 ID 등을 이용해 파일을 가져오는 로직
        String filePath = "./portfolio.pdf";
        return new File(filePath);
    }

    private void processBatch(
            List<MongoAnswer> batch,
            Queue<MongoAnswer> failQueue,
            Map<MongoAnswer, Integer> retryCounts) {
        for (MongoAnswer applicant : batch) {
            try {
                // 이메일 템플릿 생성
                String template = generateEmailTemplate(applicant);
                attachment = getPortfolioFile();

                // 이메일 발송 및 실패 처리
                boolean result =
                        sendEmailWithRetry(applicant, template, attachment, retryCounts, failQueue);
                if (!result) {
                    failQueue.add(applicant);
                    retryCounts.put(applicant, retryCounts.getOrDefault(applicant, 0) + 1);
                }
            } catch (Exception e) {
                log.error(
                        "Email sending failed for {}: {}",
                        applicant.getQna().get("email").toString(),
                        e.getMessage());
                failQueue.add(applicant);
                retryCounts.put(applicant, retryCounts.getOrDefault(applicant, 0) + 1);
            }
        }
    }

    private void failOver(Queue<MongoAnswer> failQueue, Map<MongoAnswer, Integer> retryCounts)
            throws InterruptedException {
        while (!failQueue.isEmpty()) {
            int queueSize = failQueue.size();
            for (int i = 0; i < queueSize; i++) {
                MongoAnswer applicant = failQueue.poll();
                int retryCount = retryCounts.getOrDefault(applicant, 0);

                if (retryCount >= MAX_EMAIL_SEND_RETRY) {
                    log.error("최대 10번 retry 실패시: {}", applicant.getQna().get("email").toString());
                    continue;
                }

                try {
                    // 이메일 템플릿 생성
                    String template = generateEmailTemplate(applicant);

                    // 이메일 발송 및 실패 처리
                    boolean result =
                            sendEmailWithRetry(
                                    applicant, template, attachment, retryCounts, failQueue);
                    if (!result) {
                        retryCounts.put(applicant, retryCount + 1);
                        failQueue.add(applicant);
                        log.warn(
                                "Retry failed for email: {} (Attempt {})",
                                applicant.getQna().get("email").toString(),
                                retryCount + 1);
                    }
                } catch (Exception e) {
                    log.error(
                            "Retry exception for email {}: {}",
                            applicant.getQna().get("email").toString(),
                            e.getMessage());
                    retryCounts.put(applicant, retryCount + 1);
                    failQueue.add(applicant);
                }
            }
            TimeUnit.SECONDS.sleep(1);
        }
    }

    // 이메일 템플릿 생성 메서드
    private String generateEmailTemplate(MongoAnswer applicant) {
        PassStates passState = applicant.getApplicantState().getPassStateToEnum();
        String template = "";
        switch (passState) {
            case FINAL_PASSED:
                template = generateFinalPassedTemplate(applicant);
                break;
            case FINAL_FAILED:
                template = generateFinalFailedTemplate(applicant);
                break;
            default:
                log.error("잘못된 상태 처리: {}", applicant.getId());
        }
        return template;
    }

    // 이메일 발송 및 실패 처리 메서드
    private boolean sendEmailWithRetry(
            MongoAnswer applicant,
            String template,
            File attachment,
            Map<MongoAnswer, Integer> retryCounts,
            Queue<MongoAnswer> failQueue) {
        boolean result;
        if (attachment != null) {
            result =
                    emailSender.sendEmailWithAttachment(
                            applicant.getQna().get("email").toString(),
                            "에코노베이션 신입 모집 최종 결과 안내",
                            template,
                            attachment);
        } else {
            result =
                    emailSender.sendEmail(
                            applicant.getQna().get("email").toString(),
                            "에코노베이션 신입 모집 최종 결과 안내",
                            template);
            log.error("attachment 가 첨부되지 않았습니다. file dir : " + attachment.getAbsolutePath());
        }

        if (!result) {
            retryCounts.put(applicant, retryCounts.getOrDefault(applicant, 0) + 1);
            failQueue.add(applicant);
        }

        return result;
    }
    /** 면접 합격자 이메일 템플릿 */
    private String generateFinalPassedTemplate(MongoAnswer applicant) {
        String template =
                "<img alt='econo-3d-logo' width='114' height='143' style='color:transparent; margin:auto;' src='https://recruit.econovation.kr/images/econo-3d-logo.png'><br><br>안녕하세요, NAME님.<br><br>에코노베이션에 관심을 가지고 지원해 주셔서 감사합니다.<br><br>에코노베이션 28기 신입 모집에 최종 합격하신 것을 축하드립니다!<br><br>사전에 안내해 드린 대로 OT가 진행될 예정입니다.<br><br>OT는 대면으로 진행되며, 일정에 대해 잘 숙지하시고 반드시 참여해주시기를 바랍니다.<br><br>에코노베이션에 대한 소개를 담은 포트폴리오를 아래에 첨부하였으니 OT 시작 전 확인해주시기를 바랍니다.<br><br>에코노베이션은 다양한 프로젝트와 스터디에 GitHub을 사용하고 있으니 원활한 동아리 활동을 위해 OT 전 <b>꼭 Github에 가입해주시길 바랍니다.</b><br><br>메일 확인 후 참석 여부에 대한 회신 부탁드립니다. 예) 확인, 참석합니다.<br><br>—OT—<br><br><b>일시: 10월 2일 수요일 19:00 ~ 21:00<br><br>장소 : 전남대학교 정보전산원 1층 109호</b><br><br>";
        return template.replace("NAME", applicant.getQna().get("name").toString());
    }

    /** 면접 탈락자 이메일 템플릿 ) */
    private String generateFinalFailedTemplate(MongoAnswer applicant) {
        String template =
                "<img alt='econo-3d-logo' width='114' height='143' style='color:transparent; margin:auto;' src='https://recruit.econovation.kr/images/econo-3d-logo.png'><br><br>안녕하세요 NAME님. 전남대학교 IT 개발 동아리 에코노베이션입니다.<br><br>먼저 에코노베이션 28기 신입 모집에 관심을 가지고 지원해주셔서 진심으로 감사드립니다.<br><br>혹시 이번 모집 과정 중 저희가 의도치 않게 불편을 드린 점은 없었는지 여러모로 마음이 쓰입니다.아쉽게도 이번에는 좋은 결과를 전해드리지 못하게 되었습니다.<br><br>열정을 가지고 지원해 주신 모든 분과 함께할 수 있기를 바라고 있습니다만, 선발 규모 대비 많은 분이 지원해 주셔서 모든 분께 기회를 드릴 수 없었던 점 양해 부탁드립니다.<br><br>앞으로도 에코노베이션에 많은 관심을 가져주시기 바라며, 더 좋은 기회에 다시 만나 뵐 수 있기를 바라겠습니다.<br><br>감사합니다.";
        return template.replace("NAME", applicant.getQna().get("name").toString());
    }
}
