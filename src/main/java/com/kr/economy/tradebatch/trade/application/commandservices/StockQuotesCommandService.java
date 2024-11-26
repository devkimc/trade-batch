package com.kr.economy.tradebatch.trade.application.commandservices;

import com.kr.economy.tradebatch.trade.domain.model.aggregates.StockQuotes;
import com.kr.economy.tradebatch.trade.infrastructure.repositories.StockQuotesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class StockQuotesCommandService {
    private final StockQuotesRepository stockQuotesRepository;

    /**
     * 주식 현재가 저장
     * @param ticker
     * @param quotedPrice
     */
    public void createStockQuote(String ticker, int quotedPrice, Float bidAskBalanceRatio, String tradingTime) {

        // 1. 마지막 이력 조회
        Optional<StockQuotes> optLastStockQuotes = stockQuotesRepository.findTopByTickerOrderByIdDesc(ticker);

        // 2. 현재가 이력 저장
        optLastStockQuotes.ifPresentOrElse(
                history -> {
                    StockQuotes stockQuotes = StockQuotes.builder()
                            .ticker(ticker)
                            .quotedPrice(quotedPrice)
                            .priceTrendType(history.getNextPriceTrendType(quotedPrice))
                            .bidAskBalanceRatio(bidAskBalanceRatio)
                            .bidAskBalanceTrendType(history.getNextBalanceTrendType(bidAskBalanceRatio))
                            .tradingTime(tradingTime)
                            .build();
                    stockQuotesRepository.save(stockQuotes);
                }, () -> {
                    StockQuotes initialHistory = new StockQuotes(ticker, quotedPrice, bidAskBalanceRatio, tradingTime);
                    stockQuotesRepository.save(initialHistory);
                }
        );
    }

    /**
     * 체결가 내역 초기화
     */
    public void deleteHistory() {
        stockQuotesRepository.deleteAll();
        log.info("[트레이딩 봇] - 체결가 내역 초기화 완료");
    }
}
