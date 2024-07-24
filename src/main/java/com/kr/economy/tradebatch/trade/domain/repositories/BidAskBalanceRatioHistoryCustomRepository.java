package com.kr.economy.tradebatch.trade.domain.repositories;

import com.kr.economy.tradebatch.trade.domain.model.aggregates.BidAskBalanceRatioHistory;

import java.util.List;

public interface BidAskBalanceRatioHistoryCustomRepository {

    List<BidAskBalanceRatioHistory> getRecentHistoryByBidAskBalance(String ticker);
}
