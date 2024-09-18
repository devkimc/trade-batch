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

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class KoreaStockOrderQueryService {

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

        try {
            // 최근 매수 추이 이력 조회
            List<BidAskBalanceRatioHistory> recentHistoryByBidAskBalance = bidAskBalanceRatioHistoryCustomRepository.getRecentHistoryByBidAskBalance(ticker);

            if (recentHistoryByBidAskBalance.size() < 4) {
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

            // 오후 15시 25분 이전일 경우
            LocalDateTime now = LocalDateTime.now();
            boolean isClosingTime = now.getHour() == 15 && now.getMinute() >= 25;

            if (isClosingTime) {
                return false;
            }

            // 현재가 추이가 2회 연속 감소이지만, 그 사이에 동결인 데이터가 30건 미만인 경우 매수
            // TODO 테스트 후 주석 제거
//            long idGap = recentPriceTrendHistory.get(0).getId() - recentPriceTrendHistory.get(1).getId();
//
//            if (idGap >= 30) {
//                return false;
//            }

        } catch (RuntimeException re) {
            throw new RuntimeException("[매수 신호 조회 실패]: {}", re);
        }

        return true;
    }
}
