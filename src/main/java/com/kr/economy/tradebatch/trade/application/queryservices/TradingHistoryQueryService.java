package com.kr.economy.tradebatch.trade.application.queryservices;

import com.kr.economy.tradebatch.trade.domain.model.aggregates.TradingHistory;
import com.kr.economy.tradebatch.trade.infrastructure.repositories.TradingHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TradingHistoryQueryService {
    private final TradingHistoryRepository tradingHistoryRepository;

    /**
     * 마지막 체결 내역 조회
     * @param ticker
     * @return
     */
    public Optional<TradingHistory> getLastHistoryOfToday(String ticker) {
        return tradingHistoryRepository.getLastTradingHistory(ticker);
    }

    /**
     * 종목 체결 내역 조회
     * @param ticker
     * @return
     */
    public List<TradingHistory> getTradingHistoryList(String ticker) {
        return tradingHistoryRepository.findByTicker(ticker);
    }

    public List<TradingHistory> getTradingHistoryList(String ticker, String kisOrderId) {
        List<TradingHistory> tradingHistoryList = tradingHistoryRepository.findByTickerAndKisOrderId(ticker, kisOrderId);

        log.info("[체결 내역 조회] - 주문번호: {}, 체결 건수: {}건", kisOrderId, tradingHistoryList.size());
        return tradingHistoryList;
    }
}
