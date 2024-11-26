package com.kr.economy.tradebatch.trade.application.commandservices;

import com.kr.economy.tradebatch.trade.application.queryservices.KisAccountQueryService;
import com.kr.economy.tradebatch.trade.domain.model.aggregates.KisAccount;
import com.kr.economy.tradebatch.trade.infrastructure.repositories.KisAccountRepository;
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


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class KisAccountCommandService {

    private final KisOauthClient kisOauthClient;
    private final KisAccountRepository kisAccountRepository;
    private final KisAccountQueryService kisAccountQueryService;

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

        account.renewAccessToken(oauthTokenResDto.getAccessToken());
        account.renewSocketKey(oauthSocketResDto.getApprovalKey());

        KisAccount savedAccount = kisAccountRepository.saveAndFlush(account);

        log.info("[트레이딩 봇] 토큰 발급 완료: accountId: {}, token: {}, socketKey: {}",
                savedAccount.getAccountId(),
                savedAccount.getAccessToken(),
                savedAccount.getSocketKey());

        return savedAccount.getAccessToken();
    }

    /**
     * 복호화 정보 변경
     * @param accountId
     * @param iv
     * @param key
     */
    public void changeDecryptInfo(String accountId, String iv, String key) {
        if (!StringUtils.hasText(iv) || !StringUtils.hasText(key)) {
            log.error("[Socket response] 복호화 값 미존재 iv : {}, key : {}", iv, key);
            return;
        }

        KisAccount kisAccount = kisAccountQueryService.getKisAccount(accountId);
        kisAccount.updateSocketDecryptKey(iv, key);
        kisAccountRepository.save(kisAccount);

//        log.info("[Socket response] 복호화 값 저장 성공 iv : {}, key : {}", kisAccount.getSocketDecryptIv(), kisAccount.getSocketDecryptKey());
    }
}
