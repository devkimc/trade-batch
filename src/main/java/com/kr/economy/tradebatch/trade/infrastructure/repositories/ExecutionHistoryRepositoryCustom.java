package com.kr.economy.tradebatch.trade.infrastructure.repositories;

import com.kr.economy.tradebatch.trade.domain.model.aggregates.ExecutionHistory;

import java.util.List;

public interface ExecutionHistoryRepositoryCustom {
    List<ExecutionHistory> getRecentTrendHistory(String ticker);
}
