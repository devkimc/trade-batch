package com.kr.economy.tradebatch.trade.application;

import com.kr.economy.tradebatch.trade.domain.repositories.KisAccountRepository;
import com.kr.economy.tradebatch.trade.infrastructure.rest.dto.OauthSocketResDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class KisSocketTestService {

    private final KisOauthService kisOauthService;
    private final KisAccountRepository kisAccountRepository;

    public void test(String accountId) {
        try {
            OauthSocketResDto oauthSocketResDto = kisOauthService.oauthSocket(accountId);
            log.info("[KIS Oauth] approval_key: {}", oauthSocketResDto.getApproval_key());
        } catch (RuntimeException re) {
            log.error("[KIS Oauth error] {}", re);
        }
    }
}
