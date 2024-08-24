package com.kr.economy.tradebatch.trade.application.queryservices;

import com.kr.economy.tradebatch.trade.domain.constants.BidAskBalanceTrendType;
import com.kr.economy.tradebatch.trade.domain.constants.PriceTrendType;
import com.kr.economy.tradebatch.trade.domain.model.aggregates.BidAskBalanceRatioHistory;
import com.kr.economy.tradebatch.trade.domain.model.aggregates.SharePriceHistory;
import com.kr.economy.tradebatch.trade.domain.repositories.BidAskBalanceRatioHistoryCustomRepository;
import com.kr.economy.tradebatch.trade.domain.repositories.SharePriceHistoryRepository;
import com.kr.economy.tradebatch.trade.infrastructure.repositories.SharePriceHistoryRepositoryCustom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class KoreaStockOrderQueryService {

    private final SharePriceHistoryRepository sharePriceHistoryRepository;
    private final SharePriceHistoryRepositoryCustom sharePriceHistoryRepositoryCustom;
    private final BidAskBalanceRatioHistoryCustomRepository bidAskBalanceRatioHistoryCustomRepository;

    /**
     * 매수 신호 조회
     * @param ticker 종목 코드
     * @return  매수 여부
     */
    @Transactional
    public boolean getBuySignal(String ticker) {
        boolean isBuySignal;
        SharePriceHistory lastPriceTrendHistory;

        try {
            // 최근 매수 추이 이력 조회
            List<BidAskBalanceRatioHistory> recentHistoryByBidAskBalance = bidAskBalanceRatioHistoryCustomRepository.getRecentHistoryByBidAskBalance(ticker);

            if (recentHistoryByBidAskBalance.size() < 3) {
                log.info("데이터 부족");
                return false;
            }

            // 매수매도잔량비 추이가 3회 연속 증가일 경우 매수
            isBuySignal = recentHistoryByBidAskBalance.stream().allMatch(
                    h -> BidAskBalanceTrendType.INCREASE.equals(h.getBidAskBalanceTrendType())
            );

            if (!isBuySignal) {
                return false;
            }

            List<SharePriceHistory> recentPriceTrendHistory = sharePriceHistoryRepositoryCustom.getRecentTrendHistory(ticker);

            // 현재가 추이가 2회 연속 감소일 경우 매수
            isBuySignal = recentPriceTrendHistory.stream().allMatch(
                    h -> PriceTrendType.DECREASE.equals(h.getPriceTrendType())
            );

            if (!isBuySignal) {
                return false;
            }

            lastPriceTrendHistory = recentPriceTrendHistory.get(0);

        } catch (RuntimeException re) {
            throw new RuntimeException("[매수 신호 조회 실패]: {}", re);
        }

        lastPriceTrendHistory.setBuySign();
        sharePriceHistoryRepository.save(lastPriceTrendHistory);

        return true;
    }
}
