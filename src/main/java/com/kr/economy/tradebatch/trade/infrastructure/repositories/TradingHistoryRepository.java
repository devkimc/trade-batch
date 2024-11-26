package com.kr.economy.tradebatch.trade.infrastructure.repositories;

import com.kr.economy.tradebatch.trade.domain.model.aggregates.TradingHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TradingHistoryRepository extends JpaRepository<TradingHistory, Long>, TradingHistoryRepositoryCustom {

    List<TradingHistory> findByTicker(String ticker);
}
