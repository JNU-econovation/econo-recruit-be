package com.econovation.recruitinfrastructure.ncp;

import com.econovation.recruitinfrastructure.ses.SendRawEmailDto;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "NcpClient", url = "https://mail.apigw.gov-ntruss.com", configuration = NcpConfig.class)
@Headers("Content-Type: application/json; charset=UTF-8")
public interface NcpClient {
    @PostMapping(
            path = "/api/v1/mails",
            consumes = "application/json; charset=UTF-8")
    ResponseEntity<Response> sendMail(
            @RequestHeader("x-ncp-iam-access-key") String accessKey,
            @RequestHeader("x-ncp-apigw-timestamp") String timestamp,
            @RequestHeader("x-ncp-apigw-signature-v2") String signature,
            @RequestBody SendRawEmailDto sendRawEmailDto);
}