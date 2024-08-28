package com.kr.economy.tradebatch.trade.application;

import com.kr.economy.tradebatch.trade.application.commandservices.BidAskBalanceCommandService;
import com.kr.economy.tradebatch.trade.application.commandservices.SharePriceHistoryCommandService;
import com.kr.economy.tradebatch.trade.application.commandservices.TradingHistoryCommandService;
import com.kr.economy.tradebatch.trade.application.queryservices.KoreaStockOrderQueryService;
import com.kr.economy.tradebatch.trade.application.queryservices.TradingHistoryQueryService;
import com.kr.economy.tradebatch.trade.domain.model.aggregates.TradingHistory;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.kr.economy.tradebatch.common.constants.KisStaticValues.TICKER_SAMSUNG;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SocketProcessService {

    private final SharePriceHistoryCommandService sharePriceHistoryCommandService;
    private final BidAskBalanceCommandService bidAskBalanceCommandService;
    private final TradingHistoryCommandService tradingHistoryCommandService;
    private final TradingHistoryQueryService tradingHistoryQueryService;
    private final KoreaStockOrderQueryService koreaStockOrderQueryService;

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
            bidAskBalanceCommandService.createBidAskBalanceRatioHistory(TICKER_SAMSUNG, bidAskBalanceRatio);

            // 당일 마지막 체결 내역 조회
            Optional<TradingHistory> lastTradingHistory = tradingHistoryQueryService.getLastHistoryOfToday(TICKER_SAMSUNG);

            // 마지막 체결 내역이 매수일 경우에만 매도
            if (lastTradingHistory.isPresent() && lastTradingHistory.get().isBuyTrade()) {
                if (lastTradingHistory.get().isSellSignal(sharePrice)) {
                    CreateTradingHistoryCommand createTradingHistoryCommand = CreateTradingHistoryCommand.builder()
                            .ticker(TICKER_SAMSUNG)
                            .orderDvsnCode("01")
                            .tradingPrice(sharePrice - 100)
                            .tradingQty(1)
                            .tradingResultType("0")
                            .kisOrderDvsnCode("00")
                            .kisId("") // TODO 제거하기
                            .tradingTime("") // TODO 제거하기
                            .build();
                    tradingHistoryCommandService.createTradingHistory(createTradingHistoryCommand);
                    System.out.println("************ " + tradingTime + " : " + (sharePrice - 100) + " 매도 체결 ************");
                }
            } else {
                if (koreaStockOrderQueryService.getBuySignal(TICKER_SAMSUNG)) {
                    CreateTradingHistoryCommand createTradingHistoryCommand = CreateTradingHistoryCommand.builder()
                            .ticker(TICKER_SAMSUNG)
                            .orderDvsnCode("02")
                            .tradingPrice(sharePrice + 100)
                            .tradingQty(1)
                            .tradingResultType("0")
                            .kisOrderDvsnCode("00")
                            .kisId("") // TODO 제거하기
                            .tradingTime("") // TODO 제거하기
                            .build();
                    tradingHistoryCommandService.createTradingHistory(createTradingHistoryCommand);
                    System.out.println("************ " + tradingTime + " : " + (sharePrice + 100) + " 매수 체결 ************");
                }
            }
        } catch (DataAccessException dae) {
            log.error("[Socket response DB 에러] exception : " + dae);
            log.error("[Socket response DB 에러] message : " + message);
        } catch (RuntimeException re) {
            log.error("[Socket response 런타임 에러] exception : " + re);
            log.error("[Socket response 런타임 에러] message : " + message);
        }
    }
}
