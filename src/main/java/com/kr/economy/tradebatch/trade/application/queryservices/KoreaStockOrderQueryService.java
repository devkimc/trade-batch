package com.kr.economy.tradebatch.trade.application.queryservices;

import com.kr.economy.tradebatch.trade.domain.constants.BidAskBalanceTrendType;
import com.kr.economy.tradebatch.trade.domain.constants.PriceTrendType;
import com.kr.economy.tradebatch.trade.domain.model.aggregates.SharePriceHistory;
import com.kr.economy.tradebatch.trade.domain.model.aggregates.StockItemInfo;
import com.kr.economy.tradebatch.trade.domain.repositories.SharePriceHistoryRepository;
import com.kr.economy.tradebatch.trade.infrastructure.repositories.SharePriceHistoryRepositoryCustom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class KoreaStockOrderQueryService {

    private final SharePriceHistoryRepositoryCustom sharePriceHistoryRepositoryCustom;
    private final StockItemInfoQueryService stockItemInfoQueryService;
    private final SharePriceHistoryRepository sharePriceHistoryRepository;

    /**
     * 매수 신호 조회
     * @param ticker 종목 코드
     * @return  매수 여부
     */
    public boolean getBuySignal(String ticker, int sharePrice, String tradingTime) {
        boolean isBuySignal;

        try {
            // 최근 주가 변동 내역 조회
            List<SharePriceHistory> recentSharePriceHistory = sharePriceHistoryRepositoryCustom.getRecentTrendHistory(ticker);

            if (recentSharePriceHistory.size() < 3) {
                log.info("데이터 부족");
                return false;
            }

            // 마지막 내역의 매수매도 잔량비가 증가일 경우에만 매수
            SharePriceHistory lastSharePriceHistory = recentSharePriceHistory.get(recentSharePriceHistory.size() - 1);
            isBuySignal = BidAskBalanceTrendType.INCREASE.equals(lastSharePriceHistory.getBidAskBalanceTrendType());

            if (!isBuySignal) {
                return false;
            }

            // 현재가 추이가 3회 연속 감소일 경우 매수
            isBuySignal = recentSharePriceHistory.stream().allMatch(
                    h -> PriceTrendType.DECREASE.equals(h.getPriceTrendType())
            );

            if (!isBuySignal) {
                return false;
            }

            // 오후 15시 25분 이전일 경우
            int hour = Integer.parseInt(tradingTime.substring(0, 2));
            int minute = Integer.parseInt(tradingTime.substring(2, 4));
            int second = Integer.parseInt(tradingTime.substring(4, 6));

            if (hour >= 15 && minute >= 25) {
                if (hour == 15 && minute == 25 && second == 0) {
                    log.info("[매수 신호 조회] 장 마감 5분 전 - 매매 종료");
                }

                return false;
            }

            // 현재가 추이가 2회 연속 감소이지만, 그 사이에 동결인 데이터가 30건 미만인 경우 매수
            // TODO 테스트 후 주석 제거
//            long idGap = recentSharePriceHistory.get(0).getId() - recentSharePriceHistory.get(1).getId();
//
//            if (idGap >= 30) {
//                return false;
//            }
            List<SharePriceHistory> top2ByTickerOrderByIdDesc = sharePriceHistoryRepository.findTop2ByTickerOrderByIdDesc(ticker);

            Float top1ByIdDesc = top2ByTickerOrderByIdDesc.get(top2ByTickerOrderByIdDesc.size() - 2).getBidAskBalanceRatio();
            Float top2ByIdDesc = top2ByTickerOrderByIdDesc.get(top2ByTickerOrderByIdDesc.size() - 1).getBidAskBalanceRatio();
            float bidAskBalanceRatioGap = Math.round((top1ByIdDesc - top2ByIdDesc) * 100 / 100.0);

            Float reTop1ByDesc = recentSharePriceHistory.get(recentSharePriceHistory.size() - 3).getBidAskBalanceRatio();
            Float reTop2ByDesc = recentSharePriceHistory.get(recentSharePriceHistory.size() - 2).getBidAskBalanceRatio();
            float reBidAskBalanceRatioGap = Math.round((reTop1ByDesc - reTop2ByDesc) * 100 / 100.0);

            Float re3Top1ByDesc = recentSharePriceHistory.get(recentSharePriceHistory.size() - 3).getBidAskBalanceRatio();
            Float re3Top2ByDesc = recentSharePriceHistory.get(recentSharePriceHistory.size() - 1).getBidAskBalanceRatio();
            float re3BidAskBalanceRatioGap = Math.round((re3Top1ByDesc - re3Top2ByDesc) * 100 / 100.0);

            if (bidAskBalanceRatioGap <= 0 || reBidAskBalanceRatioGap <= 0) {
                return false;
            }

            log.info("[매수] 잔1 : {} | 잔2 : {} | 잔3 : {} | 체결 가격 : {} | 거래 시간 : {}", bidAskBalanceRatioGap, reBidAskBalanceRatioGap, re3BidAskBalanceRatioGap, sharePrice + 100, tradingTime);

        } catch (RuntimeException re) {
            throw new RuntimeException("[매수 신호 조회 실패]: {}", re);
        }

        return true;
    }



    /**
     * 매도 신호 여부
     * @param sharePrice
     * @return
     */
    public boolean getSellSignal(String ticker, int sharePrice, int buyPrice, String currentTradingTime) {

        StockItemInfo stockItemInfo = stockItemInfoQueryService.getStockItemInfo(ticker);

        boolean isHighPoint = sharePrice >= buyPrice + stockItemInfo.getParValue() * 3;
        boolean isLowPoint = sharePrice <= buyPrice - stockItemInfo.getParValue() * 3;

        LocalDateTime now = LocalDateTime.now();

        // 오후 3시 25분일 경우 모두 매도
        boolean isClosingTime = now.getHour() == 15 && now.getMinute() >= 25;

        if (isClosingTime) {
            log.info("[매도 신호] 장 마감 시간 임박 : {}", now);
            return false;
        }

        // 매수 후 13분 초과 시 매도 로직 중단
//        String mm = String.valueOf(now.getMonth().getValue());
//        String dd = String.valueOf(now.getDayOfMonth());
//
//        if (mm.length() == 1) {
//            mm = "0" + mm;
//        }
//
//        if (dd.length() == 1) {
//            dd = "0" + dd;
//        }
//
//        LocalDate date = LocalDate.parse(now.getYear() + "-" + mm + "-" + dd);
//        LocalDateTime tradingLdt = date.atTime(Integer.parseInt(tradingTime.substring(0, 2)), Integer.parseInt(tradingTime.substring(2, 4)), Integer.parseInt(tradingTime.substring(4, 6)));
//        LocalDateTime currentTradingLdt = date.atTime(Integer.parseInt(currentTradingTime.substring(0, 2)), Integer.parseInt(currentTradingTime.substring(2, 4)), Integer.parseInt(currentTradingTime.substring(4, 6)));
//
//        // 매수 후 13분 초과 시 매도
//        boolean isLimitTimeout = currentTradingLdt.isAfter(tradingLdt.plusMinutes(13));

        // TODO 테스트 후 주석 제거
//        if (isLimitTimeout) {
//            log.info("[매도 신호] 매수 후 13분 초과 - 매수 시간 : {}", tradingTime);
//        }

        boolean isSellSignal = isHighPoint || isLowPoint || isClosingTime;

        String word = "";

        if (isHighPoint) {
            word = "이익";
        } else if (isLowPoint){
            word = "손실";
        }

        if (isSellSignal) {
            log.warn("[매도] [{}] 체결 가격 : {} | 거래 시간 : {}", word, sharePrice - 100, currentTradingTime);
        }

        return isSellSignal;
    }
}
