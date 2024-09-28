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
    public void createSharePriceHistory(String ticker, int sharePrice, String tradingTime) {
        Optional<SharePriceHistory> optLastSharePrice = sharePriceHistoryRepository.findTopByTickerOrderByIdDesc(ticker);

        optLastSharePrice.ifPresentOrElse(
                h -> {
                    SharePriceHistory sharePriceHistory = SharePriceHistory.builder()
                            .ticker(ticker)
                            .sharePrice(sharePrice)
                            .priceTrendType(h.getNextPriceTrendType(sharePrice))
                            .tradingTime(tradingTime)
                            .build();
                    sharePriceHistoryRepository.save(sharePriceHistory);
                }, () -> {
                    SharePriceHistory initialSharePriceHistory = new SharePriceHistory(ticker, sharePrice, tradingTime);
                    sharePriceHistoryRepository.save(initialSharePriceHistory);
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
