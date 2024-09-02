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
import org.springframework.util.StringUtils;

import java.util.Optional;

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

    public String oauthToken(String accountId) {
        String initialAccessToken = kisAccountRepository.findById(accountId).get().getAccessToken();

        if (StringUtils.hasText(initialAccessToken)) {
            return initialAccessToken;
        }

        OauthTokenReqDto oauthTokenReqDto = OauthTokenReqDto.builder()
                .grant_type("client_credentials")
                .appkey(appKey)
                .appsecret(secretKey)
                .build();
        log.info("[Oauth 토큰 발급] {}", oauthTokenReqDto);

        OauthTokenResDto oauthTokenResDto = kisOauthClient.oauthToken(oauthTokenReqDto);

        KisAccount account = kisAccountRepository.findById(accountId).get();

        account.renewAccessToken(oauthTokenResDto.getAccess_token());

        KisAccount savedAccount = kisAccountRepository.saveAndFlush(account);
        log.info("[회원 정보] 결과: {}", savedAccount);

        return savedAccount.getAccessToken();
    }

    /**
     * 소켓키 발급
     * 한투 증권 만료 기준: 발급으로부터 24시간까지 유지
     * 내부 정책 만료 기준: 발급일까지 유지 (23:59)
     * @return
     */
    public OauthSocketResDto oauthSocket(String accountId) {
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
        KisAccount account = kisAccountRepository.findById(accountId).get();

        account.renewSocketKey(oauthSocketResDto.getApproval_key());

        KisAccount savedAccount = kisAccountRepository.saveAndFlush(account);
        log.info("[회원 정보] 결과: {}", savedAccount);

        return oauthSocketResDto;
    }
}
