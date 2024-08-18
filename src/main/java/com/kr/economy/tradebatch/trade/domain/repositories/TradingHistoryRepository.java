package com.kr.economy.tradebatch.trade.domain.repositories;

import com.kr.economy.tradebatch.trade.domain.model.aggregates.TradingHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TradingHistoryRepository extends JpaRepository<TradingHistory, Long> {
}
