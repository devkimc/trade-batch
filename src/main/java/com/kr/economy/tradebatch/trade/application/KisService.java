package com.kr.economy.tradebatch.trade.application;

import com.kr.economy.tradebatch.trade.infrastructure.rest.KisClient;
import com.kr.economy.tradebatch.trade.infrastructure.rest.dto.OauthSocketReqDto;
import com.kr.economy.tradebatch.trade.infrastructure.rest.dto.OauthSocketResDto;
import com.kr.economy.tradebatch.trade.infrastructure.rest.dto.OauthTokenReqDto;
import com.kr.economy.tradebatch.trade.infrastructure.rest.dto.OauthTokenResDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KisService {

    private final KisClient kisClient;

    @Value("${credential.kis.trade.app-key}")
    private String appKey;

    @Value("${credential.kis.trade.secret-key}")
    private String secretKey;

    public OauthTokenResDto oauthToken() {

        OauthTokenReqDto oauthTokenReqDto = OauthTokenReqDto.builder()
                .grant_type("client_credentials")
                .appkey(appKey)
                .appsecret(secretKey)
                .build();

        log.info("[토큰 발급] {}", oauthTokenReqDto);

        return null;
    }

    public OauthSocketResDto oauthSocket() {

        OauthSocketReqDto oauthSocketReqDto = OauthSocketReqDto.builder()
                .grant_type("client_credentials")
                .appkey(appKey)
                .secretkey(secretKey)
                .build();

        log.info("[웹소켓 접속키 발급] {}", oauthSocketReqDto);

        OauthSocketResDto oauthSocketResDto = kisClient.oauthSocket(oauthSocketReqDto);

        log.info("[웹소켓 접속키 발급] 결과: {}", oauthSocketResDto);

        return oauthSocketResDto;
    }
}
