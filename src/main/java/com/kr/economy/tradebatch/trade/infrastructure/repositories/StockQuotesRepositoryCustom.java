package com.kr.economy.tradebatch.trade.infrastructure.repositories;

import com.kr.economy.tradebatch.trade.domain.model.aggregates.StockQuotes;

import java.util.List;

public interface StockQuotesRepositoryCustom {
    List<StockQuotes> getRecentTrendHistory(String ticker);
}
