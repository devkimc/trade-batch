package com.kr.economy.tradebatch.trade.application.queryservices;

import com.kr.economy.tradebatch.trade.application.commandservices.BidAskBalanceCommandService;
import com.kr.economy.tradebatch.trade.domain.aggregate.AskingPriceIncStatus;
import com.kr.economy.tradebatch.trade.domain.aggregate.BidAskBalanceIncStatus;
import com.kr.economy.tradebatch.trade.domain.aggregate.BidAskBalanceRatioHistory;
import com.kr.economy.tradebatch.trade.domain.repositories.BidAskBalanceRatioHistoryCustomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BidAskBalanceQueryService {

    private final BidAskBalanceRatioHistoryCustomRepository bidAskBalanceRatioHistoryCustomRepository;
    private final BidAskBalanceCommandService bidAskBalanceCommandService;

    /**
     * 매수 신호 조회
     * @return
     */
    public boolean getBuySignal() {
        boolean result;

        // 최근 매수 추이 이력 조회
        List<BidAskBalanceRatioHistory> recentHistoryByBidAskBalance = bidAskBalanceRatioHistoryCustomRepository.getRecentHistoryByBidAskBalance();

        if (recentHistoryByBidAskBalance.size() < 3) {
            log.info("데이터 부족");
            return false;
        }

        // 매수매도잔량비 추이가 4회 연속 증가일 경우 매수
        result = recentHistoryByBidAskBalance.stream().allMatch(
                h -> BidAskBalanceIncStatus.INCREASE.equals(h.getBidAskBalanceIncStatus())
        );

        if (!result) {
            return false;
        }

        List<BidAskBalanceRatioHistory> recentHistoryByAskingPrice = bidAskBalanceRatioHistoryCustomRepository.getRecentHistoryByAskingPrice();

        if (recentHistoryByAskingPrice.size() < 4) {
            log.info("데이터 부족");
            return false;
        }

        // 현재가 추이가 4회 연속 감소일 경우 매수
        result = recentHistoryByAskingPrice.stream().allMatch(
                h -> AskingPriceIncStatus.DECREASE.equals(h.getAskingPriceIncStatus())
        );

//        log.info("[현재가 추이] 1: {}, 2: {}", recentHistoryByAskingPrice.get(0).getAskingPriceIncStatus(), recentHistoryByAskingPrice.get(1).getAskingPriceIncStatus());

        if (!result) {
            return false;
        }

        BidAskBalanceRatioHistory currentHistory = recentHistoryByAskingPrice.get(1);
        log.info("[매수 신호] 현재가: {}", currentHistory.getAskingPrice());

        currentHistory.setBuySign();
        bidAskBalanceCommandService.updateBidAskBalanceRatioHistory(currentHistory);

        return true;
    }
}
