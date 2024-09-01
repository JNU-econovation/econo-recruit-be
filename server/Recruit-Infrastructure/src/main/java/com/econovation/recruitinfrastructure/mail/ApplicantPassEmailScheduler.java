package com.econovation.recruitinfrastructure.mail;

import com.econovation.recruitinfrastructure.apache.CommonsEmailSender;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ApplicantPassEmailScheduler {
    private final CommonsEmailSender emailSender;
    private final ExecutorService executor = Executors.newCachedThreadPool();

    @Value("${econovation.year}")
    private Integer year;

    @Scheduled(cron = "${econovation.recruit.period.passedDate}", zone = "Asia/Seoul")
    public void sendDocumentPassEmail() throws IOException {
        log.info("서류 합격자 이메일 발송 시작");
        //        Resource Loader setting classPath
        String path = new File(".").getCanonicalPath(); // 현재 작업 디렉토리를 가져옴
        String dir = path + "/documentPassed.csv";
        CSVReader csvReader = new CSVReaderBuilder(new FileReader(dir)).withSkipLines(1).build();
        try {
            csvReader.forEach(
                    line ->
                            CompletableFuture.runAsync(
                                    () -> {
                                        String template = generatePassedTemplates(line);
                                        emailSender.send(line[3], "에코노베이션 지원서 합격 안내", template);
                                        log.info(line[3] + "에게 이메일 발송 완료");
                                    },
                                    executor));
        } catch (Exception e) {
            log.error("서류 합격자 이메일 발송 실패");
        }
    }

    @Scheduled(cron = "${econovation.recruit.period.passedDate}", zone = "Asia/Seoul")
    public void sendDocumentFailEmail() throws FileNotFoundException {
        log.info("서류 탈락자 이메일 발송 시작");

        CSVReader csvReader =
                new CSVReaderBuilder(new FileReader("documentFailed.csv")).withSkipLines(1).build();
        try {
            csvReader.forEach(
                    line ->
                            CompletableFuture.runAsync(
                                    () -> {
                                        String template = generateFailedTemplate(line);
                                        emailSender.send(line[3], "에코노베이션 지원서 불합격 안내", template);
                                    },
                                    executor));
        } catch (Exception e) {
            log.error("서류 탈락자 이메일 발송 실패");
        }
    }
    /**
     * 서류 합격자 이메일 템플릿
     *
     * @param line ( 번호, 이름, 합격여부, 이메일, 면접 날짜, 면접 시간, 오픈채팅방 링크, 오픈채팅방 입장 마감일)
     */
    private String generatePassedTemplates(String[] line) {
        String template =
                "\n"
                        + "<!DOCTYPE html><html lang=\"ko\"><head> <meta charset=\"utf-8\"> <meta name=\"viewport\" content=\"width=device-width,initial-scale=1\"> <title>Econovation Recruit</title> <meta name=\"description\" content=\"Recruit site for econovation\"> <style>.hover-a{text-decoration: none;} .hover-a:hover{text-decoration: underline;}</style></head><body style=\"width:fit-content; height: 100vh; max-width: 1920px; margin: auto; display: block; text-align: center;\"> <section style=\"padding-top: 100px;\"><img alt=\"econo-3d-logo\" width=\"114\" height=\"143\" style=\"color:transparent; margin:auto;\" src=\"https://recruit.econovation.kr/images/econo-3d-logo.png\"> <h1 style=\"font-weight: 800; font-size:40px;\">에코노베이션 서류 합격 결과 안내</h1> <div style=\"font-size: 18px;\"> <div style=\"margin: 8px;\">안녕하세요, %NAME%님!<br><br>전남대학교 IT 개발 동아리 에코노베이션입니다.<br><br>에코노베이션 %YEAR%기 신입 회원 모집 서류 전형에 합격하신 것을 축하드립니다!<br></div> <div style=\"margin: 8px;\">면접에 대한 안내는 다음과 같습니다.<br><br>일시: %DATE% %TIME%<br><br>방법: 대면<br><br>장소: 전남대학교 정보화본부 1층 휴게실</b><br><br>--필독--<br><br>면접은 약 10분 동안 진행되며 제출하신 지원서의 내용을 바탕으로 질문이 이루어질 예정입니다. 면접 시작 전 정보화본부 1층 105호에서 대기 해주시길 바라며, 면접 시작 시간이 지난 후에 도착하실 경우 면접에 불이익이 있으므로 유의해 주시기를 바랍니다.<br><br>평가를 진행하기 위해 면접 내용은 <b>지원자님의 동의를 받은 후 녹화</b>될 예정입니다. 녹화된 영상은 평가 이후 삭제됩니다.<br><br>원활한 면접 진행을 위해 아래 오픈채팅방에 입장해주시기 바랍니다. (%KAKAOTALK_ENTRANCE_ENDDATE%까지 입장하지 않으실 경우 탈락처리됩니다.) <br><br> <br>%DATE% 오픈채팅방 링크: %LINK%</br><br><br>기타 문의 사항이 있으시면 카카오톡 채널 '에코노베이션'을 이용해 주시기를 바랍니다.<br><br></div></section></body></html>";
        return template.replace("%YEAR%", year.toString())
                .replace("%NAME%", line[1]) // 이름
                .replace("%DATE%", line[4]) // 면접 날짜
                .replace("%TIME%", line[5]) // 면접 시간
                .replace("%LINK%", line[6]) // 오픈채팅방 링크
                .replace("%KAKAOTALK_ENTRANCE_ENDDATE%", line[4]); // 오픈채팅방 입장 마감일
    }
    /**
     * 탈락 지원자 이메일 템플릿
     *
     * @param line ( 번호,이름,합격 상태,메일 주소)
     */
    private String generateFailedTemplate(String[] line) {
        String template =
                "<!DOCTYPE html><html lang=\"ko\"><head> <meta charset=\"utf-8\"> <meta name=\"viewport\" content=\"width=device-width,initial-scale=1\"> <title>Econovation Recruit</title> <meta name=\"description\" content=\"Recruit site for econovation\"> <style>.hover-a{text-decoration: none;} .hover-a:hover{text-decoration: underline;}</style></head><body style=\"width:fit-content; height: 100vh; max-width: 1920px; margin: auto; display: block; text-align: center;\"> <section style=\"padding-top: 100px;\"><img alt=\"econo-3d-logo\" width=\"114\" height=\"143\" style=\"color:transparent; margin:auto;\" src=\"https://recruit.econovation.kr/images/econo-3d-logo.png\"> <h1 style=\"font-weight: 800; font-size:40px;\">에코노베이션 서류 합격 결과 안내</h1> <div style=\"font-size: 18px;\"> <div style=\"margin: 8px;\"></div> <div style=\"margin: 8px;\">안녕하세요 %NAME%님. 전남대학교 IT 개발 동아리 에코노베이션입니다.<br><br>먼저 에코노베이션 27기 신입 모집에 관심을 가지고 지원해주셔서 진심으로 감사드립니다.<br><br>혹시 이번 모집 과정 중 저희가 의도치 않게 불편을 드린 점은 없었는지 여러모로 마음이 쓰입니다.아쉽게도 이번에는 좋은 결과를 전해드리지 못하게 되었습니다.<br><br>열정을 가지고 지원해 주신 모든 분과 함께할 수 있기를 바라고 있습니다만, 선발 규모 대비 많은 분이 지원해 주셔서 모든 분께 기회를 드릴 수 없었던 점 양해 부탁드립니다.<br><br>앞으로도 에코노베이션에 많은 관심을 가져주시기 바라며, 더 좋은 기회에 다시 만나 뵐 수 있기를 바라겠습니다.<br><br>감사합니다.</section></body></html>";
        return template.replace("%NAME%", line[1]);
    }

    /**
     * 면접 합격자 이메일 템플릿
     *
     * @param line ( 번호, 이름, 합격여부, 이메일 오픈채팅방 입장 마감일), file ( 에코노베이션 포트폴리오 )
     */

    /**
     * 면접 탈락자 이메일 템플릿
     *
     * @param line ( 번호, 이름, 합격여부, 이메일)
     */
}
