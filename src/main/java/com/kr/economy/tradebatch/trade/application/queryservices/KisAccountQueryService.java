package com.kr.economy.tradebatch.trade.application.queryservices;

import com.kr.economy.tradebatch.trade.domain.model.aggregates.KisAccount;
import com.kr.economy.tradebatch.trade.domain.repositories.KisAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KisAccountQueryService {

    private final KisAccountRepository kisAccountRepository;

    public KisAccount getKisAccount(String accountId) {
        KisAccount kisAccount = kisAccountRepository.findById(accountId).orElseThrow(() -> new RuntimeException("[Access Token 조회 실패] 회원 정보 미존재"));
        return kisAccount;
    }
}
