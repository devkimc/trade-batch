package com.kr.economy.tradebatch.trade.application.queryservices;

import com.kr.economy.tradebatch.trade.domain.constants.BidAskBalanceTrendType;
import com.kr.economy.tradebatch.trade.domain.constants.PriceTrendType;
import com.kr.economy.tradebatch.trade.domain.model.aggregates.SharePriceHistory;
import com.kr.economy.tradebatch.trade.infrastructure.repositories.SharePriceHistoryRepositoryCustom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class KoreaStockOrderQueryService {

    private final SharePriceHistoryRepositoryCustom sharePriceHistoryRepositoryCustom;

    /**
     * 매수 신호 조회
     * @param ticker 종목 코드
     * @return  매수 여부
     */
    public boolean getBuySignal(String ticker, String tradingTime) {
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

        } catch (RuntimeException re) {
            throw new RuntimeException("[매수 신호 조회 실패]: {}", re);
        }

        return true;
    }
}
