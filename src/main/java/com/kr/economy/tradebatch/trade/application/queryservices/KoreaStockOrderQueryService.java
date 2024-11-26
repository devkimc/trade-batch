package com.kr.economy.tradebatch.trade.application.queryservices;

import com.kr.economy.tradebatch.common.util.DateUtil;
import com.kr.economy.tradebatch.trade.domain.model.aggregates.StockQuotes;
import com.kr.economy.tradebatch.trade.domain.model.aggregates.StockItemInfo;
import com.kr.economy.tradebatch.trade.domain.model.aggregates.TradeReturn;
import com.kr.economy.tradebatch.trade.infrastructure.repositories.StockQuotesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.kr.economy.tradebatch.common.util.DateUtil.getTodayLocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class KoreaStockOrderQueryService {

    private final StockItemInfoQueryService stockItemInfoQueryService;
    private final StockQuotesRepository stockQuotesRepository;
    private final TradeReturnQueryService tradeReturnQueryService;

    /**
     * 매수 신호 조회
     * @param ticker 종목 코드
     * @return  매수 여부
     */
    public boolean getBuySignal(String ticker, int quotedPrice, String tradingTime, String accountId, String message) {

        try {
            // 최근 주가 변동 내역 조회
            List<StockQuotes> recentStockQuotes = stockQuotesRepository.getRecentTrendHistory(ticker);

            if (recentStockQuotes.size() < 3) {
                log.debug("데이터 부족");
                return false;
            }

            // 마지막 내역의 매수매도 잔량비가 증가일 경우에만 매수
            if (!recentStockQuotes.get(recentStockQuotes.size() - 1).isIncreasedBidAskBalanceRatio()) {
                return false;
            }

            // 현재가 추이가 3회 연속 감소일 경우 매수
            if (!recentStockQuotes.stream().allMatch(StockQuotes::isDecreasedPrice)) {
                return false;
            }

            // 주식 상품 정보 조회
            StockItemInfo stockItemInfo = stockItemInfoQueryService.getStockItemInfo(ticker);

            // 당일 최대 손실금액 한도 확인
            Optional<TradeReturn> optTradeReturn = tradeReturnQueryService.getTradeReturn(accountId, ticker, DateUtil.toNonHyphenDay(LocalDateTime.now()));

            // 손실 한도 확인
            if (optTradeReturn.isPresent() && optTradeReturn.get().isLossLimit(stockItemInfo.getDailyLossLimitPrice())) {
                return false;
            }

            // 현재가 추이가 2회 연속 감소이지만, 그 사이에 동결인 데이터가 30건 미만인 경우 매수
//            long idGap = recentSharePriceHistory.get(0).getId() - recentSharePriceHistory.get(1).getId();
//
//            if (idGap >= 30) {
//                return false;
//            }
            List<StockQuotes> top2ByTickerOrderByIdDesc = stockQuotesRepository.findTop2ByTickerOrderByIdDesc(ticker);

            // TODO 제거 해도 될지 검토 필요
            Float top1ByIdDesc = top2ByTickerOrderByIdDesc.get(top2ByTickerOrderByIdDesc.size() - 2).getBidAskBalanceRatio();
            Float top2ByIdDesc = top2ByTickerOrderByIdDesc.get(top2ByTickerOrderByIdDesc.size() - 1).getBidAskBalanceRatio();
            float bidAskBalanceRatioGap = Math.round((top1ByIdDesc - top2ByIdDesc) * 100 / 100.0);

            Float reTop1ByDesc = recentStockQuotes.get(recentStockQuotes.size() - 3).getBidAskBalanceRatio();
            Float reTop2ByDesc = recentStockQuotes.get(recentStockQuotes.size() - 2).getBidAskBalanceRatio();
            float reBidAskBalanceRatioGap = Math.round((reTop1ByDesc - reTop2ByDesc) * 100 / 100.0);

            if (bidAskBalanceRatioGap <= 0 || reBidAskBalanceRatioGap <= 0) {
                return false;
            }

            // 거래 시간 확인
            if (!isTradeTime()) {
                return false;
            }

            log.info(message);

        } catch (RuntimeException re) {
            throw new RuntimeException("[매수 신호 조회 실패]: {}", re);
        }

        return true;
    }

    /**
     * 매도 신호 여부
     * @param quotedPrice
     * @return
     */
    public boolean getSellSignal(String ticker, int quotedPrice, int buyPrice, String tradingTime) {
        StockItemInfo stockItemInfo = stockItemInfoQueryService.getStockItemInfo(ticker);
        return stockItemInfo.haveToSell(buyPrice, quotedPrice);
    }

    private boolean isTradeTime() {

        // TODO 테스트 필요
        if (LocalDateTime.now().isBefore(getTodayLocalDateTime(9, 1 ,0))) {
            log.info("[매수 신호 조회] 장 시작 후 1분 이내 - 매수 X");
            return false;
        }

        if (LocalDateTime.now().isAfter(getTodayLocalDateTime(15, 25, 0))) {
            log.info("[매수 신호 조회] 장 마감 전 5분 이내 - 매수 X");
            return false;
        }

        return true;
    }
}
