package com.kr.economy.tradebatch.trade.application;

import com.kr.economy.tradebatch.common.util.KisUtil;
import com.kr.economy.tradebatch.trade.application.commandservices.BidAskBalanceCommandService;
import com.kr.economy.tradebatch.trade.application.commandservices.SharePriceHistoryCommandService;
import com.kr.economy.tradebatch.trade.application.commandservices.TradingHistoryCommandService;
import com.kr.economy.tradebatch.trade.application.queryservices.KisAccountQueryService;
import com.kr.economy.tradebatch.trade.application.queryservices.KoreaStockOrderQueryService;
import com.kr.economy.tradebatch.trade.application.queryservices.TradingHistoryQueryService;
import com.kr.economy.tradebatch.trade.domain.model.aggregates.TradingHistory;
import com.kr.economy.tradebatch.trade.infrastructure.rest.DomesticStockOrderClient;
import com.kr.economy.tradebatch.trade.infrastructure.rest.dto.OrderInCashReqDto;
import com.kr.economy.tradebatch.trade.infrastructure.rest.dto.OrderInCashResDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.kr.economy.tradebatch.common.constants.KisStaticValues.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SocketProcessService {

    @Value("${credential.kis.trade.app-key}")
    private String appKey;

    @Value("${credential.kis.trade.secret-key}")
    private String secretKey;

    @Value("${credential.kis.trade.account-no}")
    private String accountNo;

    @Value("${spring.profiles.active}")
    private String activeProfile;

    private final SharePriceHistoryCommandService sharePriceHistoryCommandService;
    private final BidAskBalanceCommandService bidAskBalanceCommandService;
    private final TradingHistoryCommandService tradingHistoryCommandService;
    private final TradingHistoryQueryService tradingHistoryQueryService;
    private final KoreaStockOrderQueryService koreaStockOrderQueryService;
    private final DomesticStockOrderClient domesticStockOrderClient;
    private final KisAccountQueryService kisAccountQueryService;
    private final KisOauthService kisOauthService;

    public void socketProcess(String message) {
        if (message == null || message.length() < 2) {
            log.warn("[Socket response] message : " + message);
            return;
        }

        String[] resultBody = message.split("\\|");
        if (resultBody.length < 4) {
            log.info("[Socket response] message : " + message);
            return;
        }

        String trId = resultBody[1];

        if (TR_ID_H0STCNT0.equals(trId)) {
            sharePriceProcess(message, resultBody);
        } else if (TR_ID_H0STCNI0.equals(trId) || TR_ID_H0STCNI9.equals(trId)) {
            tradeResultNoticeProcess(message, resultBody);
        }
    }

    private void sharePriceProcess(String message, String[] resultBody) {
        
        String[] result = resultBody[3].split("\\^");
        if (result.length < 38) {
            log.info("[Socket response] message : " + message);
            return;
        }

        try {
            String tradingTime = result[1];
            int sharePrice = Integer.parseInt(result[2]);
            float bidAskBalanceRatio = Float.parseFloat(result[37]) / Float.parseFloat(result[36]);

            // 실시간 현재가 저장
            sharePriceHistoryCommandService.createSharePriceHistory(TICKER_SAMSUNG, sharePrice, tradingTime);

            // 실시간 매수매도 잔량비 저장
            bidAskBalanceCommandService.createBidAskBalanceRatioHistory(TICKER_SAMSUNG, bidAskBalanceRatio, tradingTime);

            // 당일 마지막 체결 내역 조회
            Optional<TradingHistory> lastTradingHistory = tradingHistoryQueryService.getLastHistoryOfToday(TICKER_SAMSUNG);

            // 마지막 체결 내역이 매수일 경우에만 매도
            if (lastTradingHistory.isPresent() && lastTradingHistory.get().isBuyTrade()) {
                if (lastTradingHistory.get().isSellSignal(sharePrice, tradingTime)) {
                    order("S");
                }
            } else {
                if (koreaStockOrderQueryService.getBuySignal(TICKER_SAMSUNG)) {
                    order("B");
                }
            }
        } catch (DataAccessException dae) {
            log.error("[Socket response DB 에러] exception : {}, stackTrace: {}" , dae, dae.getStackTrace().toString());
            log.error("[Socket response DB 에러] message : " + message);
        } catch (RuntimeException re) {
            log.error("[Socket response 런타임 에러] exception : {}, stackTrace: {}" , re, re.getStackTrace().toString());
            log.error("[Socket response 런타임 에러] message : {}" , message);
        }
    }

    private void tradeResultNoticeProcess(String message, String[] resultBody) {
        String[] result = resultBody[3].split("\\^");
        if (result.length < 23) {
            log.info("[Socket response] message : " + message);
            return;
        }

        log.info("[Socket response temporary check] message : " + message);

        try {
            String kisId = result[0];
            String kisOrderId = result[2];
            String kisOrOrderID = result[3];
            String orderDvsnCode = result[4];
            String kisOrderDvsnCode = result[6];
            String ticker = result[8];
            String tradingQty = result[9];
            String tradingPrice = result[10];
            String tradingTime = result[11];
            String refuseCode = result[12];
            String tradeResultCode = result[13];
            String tradingResultType = "0".equals(refuseCode) && "1".equals(tradeResultCode) ? "0" : "1";

            CreateTradingHistoryCommand createTradingHistoryCommand = CreateTradingHistoryCommand.builder()
                    .ticker(ticker)
                    .orderDvsnCode(orderDvsnCode)
                    .tradingPrice(Integer.parseInt(tradingPrice))
                    .tradingQty(Integer.parseInt(tradingQty))
                    .tradingResultType(tradingResultType)
                    .kisOrderDvsnCode(kisOrderDvsnCode)
                    .kisId(kisId)
                    .tradingTime(tradingTime)
                    .kisOrderId(kisOrderId)
                    .kisOrOrderId(kisOrOrderID)
                    .build();
            tradingHistoryCommandService.createTradingHistory(createTradingHistoryCommand);
        } catch (DataAccessException dae) {
            log.error("[Socket response DB 에러] exception : " + dae);
            log.error("[Socket response DB 에러] message : " + message);
        } catch (RuntimeException re) {
            log.error("[Socket response 런타임 에러] exception : " + re);
            log.error("[Socket response 런타임 에러] message : " + message);
        }
    }

    private void order(String orderDvsnCode) {
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
                .pdno(TICKER_SAMSUNG)
                .ordDvsn("01")
                .ordQty("10")
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
