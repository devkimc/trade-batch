package com.kr.economy.tradebatch.trade.application.commandservices;

import com.kr.economy.tradebatch.trade.domain.model.aggregates.BidAskBalanceRatioHistory;
import com.kr.economy.tradebatch.trade.domain.repositories.BidAskBalanceRatioHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BidAskBalanceCommandService {
    private final BidAskBalanceRatioHistoryRepository bidAskBalanceRatioHistoryRepository;

    /**
     * 매수매도잔량비 이력 등록
     * @param nextBidAskBalanceRatio
     */
    public void createBidAskBalanceRatioHistory(String ticker, Float nextBidAskBalanceRatio) {

        // 1. 마지막 이력 조회
        Optional<BidAskBalanceRatioHistory> optionalLastBalanceRatio = bidAskBalanceRatioHistoryRepository.findTopByTickerOrderByCreatedDateDesc(ticker);

        // 2. 다음 이력 저장
        optionalLastBalanceRatio.ifPresentOrElse(
                present -> {
                    BidAskBalanceRatioHistory nextBidAskBalanceHistory = BidAskBalanceRatioHistory.builder()
                            .ticker(ticker)
                            .bidAskBalanceRatio(nextBidAskBalanceRatio)
                            .bidAskBalanceTrendType(present.getNextBalanceTrendType(nextBidAskBalanceRatio))
                            .build();

                    bidAskBalanceRatioHistoryRepository.save(nextBidAskBalanceHistory);
                },
                () -> {
                    bidAskBalanceRatioHistoryRepository.save(new BidAskBalanceRatioHistory(ticker, nextBidAskBalanceRatio));
                }
        );
    }

    /**
     * 매수 매도 잔량비 내역 초기화
     */
    public void deleteHistory() {
        bidAskBalanceRatioHistoryRepository.deleteAll();
    }
}
