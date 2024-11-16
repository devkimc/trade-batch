package com.kr.economy.tradebatch.trade.application.commandservices;

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
public class KisAccountCommandService {

    private final KisOauthClient kisOauthClient;
    private final KisAccountRepository kisAccountRepository;

    @Value("${credential.kis.trade.app-key}")
    private String appKey;

    @Value("${credential.kis.trade.secret-key}")
    private String secretKey;

    public String oauthToken(String accountId) {
        OauthTokenReqDto oauthTokenReqDto = OauthTokenReqDto.builder()
                .grant_type("client_credentials")
                .appkey(appKey)
                .appsecret(secretKey)
                .build();

        OauthSocketReqDto oauthSocketReqDto = OauthSocketReqDto.builder()
                .grant_type("client_credentials")
                .appkey(appKey)
                .secretkey(secretKey)
                .build();

        OauthSocketResDto oauthSocketResDto;
        OauthTokenResDto oauthTokenResDto;

        try {
            oauthTokenResDto = kisOauthClient.oauthToken(oauthTokenReqDto);
            oauthSocketResDto = kisOauthClient.oauthSocket(oauthSocketReqDto);
        } catch (RuntimeException rex) {
            log.error("[웹소켓 접속키 발급] 실패 accountId: {}", accountId);
            return null;
        }

        KisAccount account = kisAccountRepository.findByIdForUpdate(accountId);
        log.info("[트레이딩 봇] - 토큰 발급 전 사용자 정보 : {}", account);

        account.renewAccessToken(oauthTokenResDto.getAccess_token());
        account.renewSocketKey(oauthSocketResDto.getApproval_key());

        KisAccount savedAccount = kisAccountRepository.saveAndFlush(account);

        log.info("[트레이딩 봇] 토큰 발급 완료: accountId: {}, token: {}, socketKey: {}",
                savedAccount.getAccountId(),
                savedAccount.getAccessToken(),
                savedAccount.getSocketKey());

        return savedAccount.getAccessToken();
    }
}
