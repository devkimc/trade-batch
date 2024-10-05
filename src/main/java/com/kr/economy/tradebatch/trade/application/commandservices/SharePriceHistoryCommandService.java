package com.kr.economy.tradebatch.trade.application.commandservices;

import com.kr.economy.tradebatch.trade.domain.model.aggregates.SharePriceHistory;
import com.kr.economy.tradebatch.trade.domain.repositories.SharePriceHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SharePriceHistoryCommandService {
    private final SharePriceHistoryRepository sharePriceHistoryRepository;

    /**
     * 주식 현재가 저장
     * @param ticker
     * @param sharePrice
     */
    public void createSharePriceHistory(String ticker, int sharePrice, Float bidAskBalanceRatio, String tradingTime) {

        // 1. 마지막 이력 조회
        Optional<SharePriceHistory> optLastSharePrice = sharePriceHistoryRepository.findTopByTickerOrderByIdDesc(ticker);

        // 2. 현재가 이력 저장
        optLastSharePrice.ifPresentOrElse(
                history -> {
                    SharePriceHistory sharePriceHistory = SharePriceHistory.builder()
                            .ticker(ticker)
                            .sharePrice(sharePrice)
                            .priceTrendType(history.getNextPriceTrendType(sharePrice))
                            .bidAskBalanceRatio(bidAskBalanceRatio)
                            .bidAskBalanceTrendType(history.getNextBalanceTrendType(bidAskBalanceRatio))
                            .tradingTime(tradingTime)
                            .build();
                    sharePriceHistoryRepository.save(sharePriceHistory);
                }, () -> {
                    SharePriceHistory initialHistory = new SharePriceHistory(ticker, sharePrice, bidAskBalanceRatio, tradingTime);
                    sharePriceHistoryRepository.save(initialHistory);
                }
        );
    }

    /**
     * 현재가 내역 초기화
     */
    public void deleteHistory() {
        sharePriceHistoryRepository.deleteAll();
    }
}
