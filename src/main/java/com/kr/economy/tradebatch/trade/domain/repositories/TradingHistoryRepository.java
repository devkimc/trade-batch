package com.kr.economy.tradebatch.trade.domain.repositories;

import com.kr.economy.tradebatch.trade.domain.model.aggregates.TradingHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TradingHistoryRepository extends JpaRepository<TradingHistory, Long> {

    List<TradingHistory> findByTicker(String ticker);
}
