package com.kr.economy.tradebatch.trade.application.commandservices;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kr.economy.tradebatch.common.util.KisUtil;
import com.kr.economy.tradebatch.trade.application.queryservices.KisAccountQueryService;
import com.kr.economy.tradebatch.trade.infrastructure.rest.DomesticStockOrderClient;
import com.kr.economy.tradebatch.trade.infrastructure.rest.dto.OrderInCashReqDto;
import com.kr.economy.tradebatch.trade.infrastructure.rest.dto.OrderInCashResDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.kr.economy.tradebatch.common.constants.KisStaticValues.*;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class OrderCommandService {

    @Value("${credential.kis.trade.app-key}")
    private String appKey;

    @Value("${credential.kis.trade.secret-key}")
    private String secretKey;

    @Value("${credential.kis.trade.account-no}")
    private String accountNo;

    @Value("${spring.profiles.active}")
    private String activeProfile;

    private final DomesticStockOrderClient domesticStockOrderClient;
    private final KisAccountQueryService kisAccountQueryService;

    public void order(String ticker, String orderDvsnCode) {
        String orderDvsnName = "B".equals(orderDvsnCode) ? "매수" : "매도";
        String trId = "";

        if ("B".equals(orderDvsnCode)) {
            if ("dev".equals(activeProfile) || "prod".equals(activeProfile)) {
                trId = TR_ID_TTTC0802U;
            } else {
                trId = TR_ID_VTTC0802U;
            }
        } else {
            if ("dev".equals(activeProfile) || "prod".equals(activeProfile)) {
                trId = TR_ID_TTTC0801U;
            } else {
                trId = TR_ID_VTTC0801U;
            }
        }

        String accessToken = kisAccountQueryService.getKisAccount("DEVKIMC").getAccessToken();
        log.info("[{} 주문] Oauth 토큰 : {}", orderDvsnName, accessToken);

        OrderInCashReqDto orderInCashReqDto = OrderInCashReqDto.builder()
                .cano(KisUtil.getCano(accountNo))
                .acntPrdtCd(KisUtil.getAcntPrdtCd(accountNo))
                .pdno(ticker)
                .ordDvsn("01")
                .ordQty("1")
                .ordUnpr("0")  // 시장가일 경우 0
                .build();
        log.info("[{} 주문] 요청: {}", orderDvsnName, orderInCashReqDto);

        OrderInCashResDto orderInCashResDto = domesticStockOrderClient.orderInCash(
                "application/json",
                "Bearer " + accessToken,
                appKey,
                secretKey,
                trId,
                "P",
                orderInCashReqDto
        );

        if (!"0".equals(orderInCashResDto.getRt_cd())) {
            throw new RuntimeException(orderInCashResDto.toString());
        }
    }
}
