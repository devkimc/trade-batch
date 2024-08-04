package com.kr.economy.tradebatch.trade.infrastructure.repositories;

import com.kr.economy.tradebatch.trade.domain.model.aggregates.ExecutionHistory;
import com.kr.economy.tradebatch.trade.domain.model.aggregates.SharePriceHistory;

import java.util.List;

public interface SharePriceHistoryRepositoryCustom {
    List<SharePriceHistory> getRecentTrendHistory(String ticker);
}
