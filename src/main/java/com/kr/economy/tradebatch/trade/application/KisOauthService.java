package com.kr.economy.tradebatch.trade.application;

import com.kr.economy.tradebatch.trade.domain.model.aggregates.KisAccount;
import com.kr.economy.tradebatch.trade.domain.repositories.KisAccountRepository;
import com.kr.economy.tradebatch.trade.infrastructure.rest.KisOauthClient;
import com.kr.economy.tradebatch.trade.infrastructure.rest.dto.OauthSocketReqDto;
import com.kr.economy.tradebatch.trade.infrastructure.rest.dto.OauthSocketResDto;
import com.kr.economy.tradebatch.trade.infrastructure.rest.dto.OauthTokenReqDto;
import com.kr.economy.tradebatch.trade.infrastructure.rest.dto.OauthTokenResDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class KisOauthService {

    private final KisOauthClient kisOauthClient;
    private final KisAccountRepository kisAccountRepository;

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
        log.info("[Oauth 토큰 발급] {}", oauthTokenReqDto);

        OauthTokenResDto oauthTokenResDto = kisOauthClient.oauthToken(oauthTokenReqDto);
        log.info("[Oauth 토큰 발급] 결과: {}", oauthTokenResDto);

        KisAccount kisAccount = KisAccount.builder()
                .accountId("DEVKIMC")
                .accessToken(oauthTokenResDto.getAccess_token())
                .build();
        kisAccountRepository.save(kisAccount);

        return oauthTokenResDto;
    }

    /**
     * 소켓키 발급
     * 한투 증권 만료 기준: 발급으로부터 24시간까지 유지
     * 내부 정책 만료 기준: 발급일까지 유지 (23:59)
     * @return
     */
    public OauthSocketResDto oauthSocket() {
        OauthSocketReqDto oauthSocketReqDto = OauthSocketReqDto.builder()
                .grant_type("client_credentials")
                .appkey(appKey)
                .secretkey(secretKey)
                .build();
        log.info("[웹소켓 접속키 발급] 요청 데이터: {}", oauthSocketReqDto);

        OauthSocketResDto oauthSocketResDto;

        try {
            oauthSocketResDto = kisOauthClient.oauthSocket(oauthSocketReqDto);
        } catch (RuntimeException rex) {
            log.error("[웹소켓 접속키 발급] 실패 appKey: {}", appKey);
            return null;
        }

        log.info("[웹소켓 접속키 발급] 결과: {}", oauthSocketResDto);

        KisAccount account = KisAccount.builder()
                .accountId("DEVKIMC")
                .socketKey(oauthSocketResDto.getApproval_key())
                .build();
        kisAccountRepository.save(account);

        return oauthSocketResDto;
    }
}
