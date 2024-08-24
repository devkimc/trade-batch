package com.kr.economy.tradebatch.trade.infrastructure.repositories;

import com.kr.economy.tradebatch.trade.domain.model.aggregates.TradingHistory;

import java.util.Optional;

public interface TradingHistoryRepositoryCustom {
    Optional<TradingHistory> getLastTradingHistory(String ticker);
}
