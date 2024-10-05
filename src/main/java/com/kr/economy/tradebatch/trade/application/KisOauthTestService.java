package com.kr.economy.tradebatch.trade.application;

import com.kr.economy.tradebatch.trade.domain.repositories.KisAccountRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class KisOauthTestService {

    private final KisOauthService kisOauthService;
    private final KisAccountRepository kisAccountRepository;

//    /**
//     * 소켓키 발급
//     *      1. 소켓키 발급 내역이 존재하지 않은 경우
//     *      2. 소켓키가 만료되지 않은 경우
//     * @param accountId
//     * @return socketKey
//     */
//    public String issueSocketKey(String accountId) {
//        Optional<KisAccount> optionalAccount = kisAccountRepository.findById(accountId);
//
//        if (optionalAccount.isEmpty()) {
//            log.info("[Socket key 발급 - 첫 발급]");
//        } else {
//            KisAccount account = optionalAccount.get();
//            log.info("[Socket key 발급 - 재발급] 발급 시간: {}, 만료 시간: {}", account.getModDate(), account.getExpirationTime());
//        }
//
//        // 소켓키 발급
//        return kisOauthService.oauthSocket().getApproval_key();
//    }

    public void test(String accountId) {
        try {
            // TODO 비관적락 구현
//            KisAccount accountForUpdate = kisAccountRepository.findByIdForUpdate(accountId);
//            log.info("[KIS Oauth] LOCK: {}", accountForUpdate);

//            OauthSocketResDto oauthSocketResDto = kisOauthService.oauthSocket(accountId);
//            log.info("[KIS Oauth] approval_key: {}", oauthSocketResDto.getApproval_key());

            String oauthToken = kisOauthService.oauthToken(accountId);
            log.info("[KIS Oauth] oauthToken: {}", oauthToken);
        } catch (RuntimeException re) {
            log.error("[KIS Oauth error] {}", re);
        }
    }
}
