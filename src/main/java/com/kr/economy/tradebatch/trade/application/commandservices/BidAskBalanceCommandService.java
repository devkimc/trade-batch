package com.kr.economy.tradebatch.trade.application.commandservices;

import com.kr.economy.tradebatch.trade.domain.aggregate.BidAskBalanceRatioHistory;
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
    public void createBidAskBalanceRatioHistory(Float nextBidAskBalanceRatio, Float askingPrice) {

        // 1. 마지막 이력 조회
        Optional<BidAskBalanceRatioHistory> optionalLastBalanceRatio = bidAskBalanceRatioHistoryRepository.findTopByOrderByRegDateDesc();

        // 2. 다음 이력 저장
        optionalLastBalanceRatio.ifPresentOrElse(
                present -> {
                    BidAskBalanceRatioHistory nextBidAskBalanceHistory = BidAskBalanceRatioHistory.builder()
                            .bidAskBalanceRatio(nextBidAskBalanceRatio)
                            .bidAskBalanceIncStatus(present.getNextStatus(nextBidAskBalanceRatio))
                            .askingPrice(askingPrice)
                            .askingPriceIncStatus(present.getNextAskingPriceStatus(askingPrice))
                            .build();

                    bidAskBalanceRatioHistoryRepository.save(nextBidAskBalanceHistory);
                },
                () -> {
                    bidAskBalanceRatioHistoryRepository.save(new BidAskBalanceRatioHistory(nextBidAskBalanceRatio, askingPrice));
                }
        );
    }

    public void updateBidAskBalanceRatioHistory(BidAskBalanceRatioHistory bidAskBalanceRatioHistory) {
        bidAskBalanceRatioHistoryRepository.save(bidAskBalanceRatioHistory);
    }
}
