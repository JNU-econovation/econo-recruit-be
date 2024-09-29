package com.econovation.recruitinfrastructure.apache;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommonsEmailSender {
    private final CommonsEmailProperties commonsEmailProperties;

    @Value("${econovation.year}")
    private Integer year;

    public void send(String toEmail, String applicantId, LocalDateTime passedDate) {
        HtmlEmail email = new HtmlEmail();
        email.setCharset("euc-kr");
        email.setHostName(commonsEmailProperties.getHost());
        email.setSmtpPort(465);
        email.setAuthentication(
                commonsEmailProperties.getSenderAddress(), commonsEmailProperties.getPassword());
        email.setSSLOnConnect(true);
        email.setStartTLSEnabled(true);
        try {
            email.addTo(toEmail);
            email.setFrom(
                    commonsEmailProperties.getSenderAddress(),
                    commonsEmailProperties.getSenderName());
            email.setSubject("에코노베이션 지원서 접수 확인");
            email.setHtmlMsg(generateHtml(applicantId, passedDate));
            email.send();
        } catch (EmailException e) {
            log.error("Email send error", e);
            throw new IllegalArgumentException(e);
        }
    }

    @Retryable(value = EmailException.class, maxAttempts = 3, backoff = @Backoff(delay = 3000))
    public void send(String toEmail, String subject, String htmlMessage) {
        HtmlEmail email = new HtmlEmail();
        email.setCharset("euc-kr");
        email.setHostName(commonsEmailProperties.getHost());
        email.setSmtpPort(465);
        email.setAuthentication(
                commonsEmailProperties.getSenderAddress(), commonsEmailProperties.getPassword());
        email.setSSLOnConnect(true);
        email.setStartTLSEnabled(true);
        try {
            email.addTo(toEmail);
            email.setFrom(
                    commonsEmailProperties.getSenderAddress(),
                    commonsEmailProperties.getSenderName());
            email.setSubject(subject);
            email.setHtmlMsg(htmlMessage);
            email.send();
        } catch (EmailException e) {
            log.error("Email send error", e);
            throw new IllegalArgumentException(e);
        }
    }

    @Retryable(value = EmailException.class, maxAttempts = 3, backoff = @Backoff(delay = 3000))
    public boolean sendEmail(String toEmail, String subject, String htmlMessage) {
        HtmlEmail email = new HtmlEmail();
        email.setCharset("euc-kr");
        email.setHostName(commonsEmailProperties.getHost());
        email.setSmtpPort(465);
        email.setAuthentication(
                commonsEmailProperties.getSenderAddress(), commonsEmailProperties.getPassword());
        email.setSSLOnConnect(true);
        email.setStartTLSEnabled(true);

        try {
            email.addTo(toEmail);
            email.setFrom(
                    commonsEmailProperties.getSenderAddress(),
                    commonsEmailProperties.getSenderName());
            email.setSubject(subject);
            email.setHtmlMsg(htmlMessage);
            email.send();
            return true; // 이메일 발송 성공 시 true 반환
        } catch (EmailException e) {
            log.error("Email send error", e);
            return false; // 예외 발생 시 false 반환
        }
    }

    @Retryable(value = EmailException.class, maxAttempts = 3, backoff = @Backoff(delay = 3000))
    public boolean sendEmailWithAttachment(
            String toEmail, String subject, String htmlMessage, File attachment) {
        HtmlEmail email = new HtmlEmail();
        email.setCharset("euc-kr");
        email.setHostName(commonsEmailProperties.getHost());
        email.setSmtpPort(465);
        email.setAuthentication(
                commonsEmailProperties.getSenderAddress(), commonsEmailProperties.getPassword());
        email.setSSLOnConnect(true);
        email.setStartTLSEnabled(true);

        try {
            email.addTo(toEmail);
            email.setFrom(
                    commonsEmailProperties.getSenderAddress(),
                    commonsEmailProperties.getSenderName());
            email.setSubject(subject);
            email.setHtmlMsg(htmlMessage);
            email.attach(attachment);
            email.send();
            return true; // 이메일 발송 성공 시 true 반환
        } catch (EmailException e) {
            log.error("Email send error", e);
            return false; // 예외 발생 시 false 반환
        }
    }

    private String generateHtml(String applicantId, LocalDateTime passedDate) {
        String html =
                "<!DOCTYPE html><html lang=\"ko\"><head> <meta charset=\"utf-8\"> <meta name=\"viewport\" content=\"width=device-width,initial-scale=1\"> <title>Econovation Recruit</title> <meta name=\"description\" content=\"Recruit site for econovation\"> <style>.hover-a{text-decoration: none;} .hover-a:hover{text-decoration: underline;}</style></head><body style=\"width:fit-content; height: 100vh; max-width: 1920px; margin: auto; display: block; text-align: center;\"> <section style=\"padding-top: 100px;\"><img alt=\"econo-3d-logo\" width=\"114\" height=\"143\" style=\"color:transparent; margin:auto;\" src=\"https://recruit.econovation.kr/images/econo-3d-logo.png\"> <h1 style=\"font-weight: 800; font-size:40px;\">신입모집 지원 완료!</h1> <div style=\"font-size: 18px;\"> <div style=\"margin: 8px;\">에코노베이션 %YEAR%기 지원서가 정상적으로 업로드 되었습니다</div> <div style=\"margin: 8px;\">서류 합격 결과는 %PASSED_DATE%에 개인 메일으로 공지 될 예정입니다.</div> <div style=\"margin: 8px;\"><a class=\"hover-a\" style=\"color: #2160FF; font-weight: 700;\" href=\"https://recruit.econovation.kr/applicant/pdf-viewer?id=%APPLICANT_ID%\">여기</a>에서 지원서를 확인하실 수 있습니다.</div> <div style=\"margin: 8px;\">지원해주셔서 감사합니다.</div> </div> </section></body></html>";
        html = html.replace("%YEAR%", year.toString());
        html =
                html.replace(
                        "%PASSED_DATE%",
                        passedDate
                                .format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 / HH:mm:ss"))
                                .toString());
        return html.replace("%APPLICANT_ID%", applicantId);
    }
}
