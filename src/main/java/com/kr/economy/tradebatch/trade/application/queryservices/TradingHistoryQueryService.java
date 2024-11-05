package com.kr.economy.tradebatch.trade.application.queryservices;

import com.kr.economy.tradebatch.trade.domain.model.aggregates.TradingHistory;
import com.kr.economy.tradebatch.trade.domain.repositories.TradingHistoryRepository;
import com.kr.economy.tradebatch.trade.infrastructure.repositories.TradingHistoryRepositoryCustom;
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
    private final TradingHistoryRepositoryCustom tradingHistoryRepositoryCustom;

    /**
     * 마지막 체결 내역 조회
     * @param ticker
     * @return
     */
    public Optional<TradingHistory> getLastHistoryOfToday(String ticker) {
        return tradingHistoryRepositoryCustom.getLastTradingHistory(ticker);
    }

    /**
     * 종목 체결 내역 조회
     * @param ticker
     * @return
     */
    public List<TradingHistory> getTradingHistoryList(String ticker) {
        return tradingHistoryRepository.findByTicker(ticker);
    }
}
