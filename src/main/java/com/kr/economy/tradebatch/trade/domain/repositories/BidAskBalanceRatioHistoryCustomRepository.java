package com.kr.economy.tradebatch.trade.domain.repositories;

import com.kr.economy.tradebatch.trade.domain.aggregate.BidAskBalanceRatioHistory;

import java.util.List;

public interface BidAskBalanceRatioHistoryCustomRepository {

    List<BidAskBalanceRatioHistory> getRecentHistoryByBidAskBalance();
    List<BidAskBalanceRatioHistory> getRecentHistoryByAskingPrice();
}
