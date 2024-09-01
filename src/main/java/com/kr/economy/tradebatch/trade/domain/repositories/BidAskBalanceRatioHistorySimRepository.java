package com.kr.economy.tradebatch.trade.domain.repositories;

import com.kr.economy.tradebatch.trade.domain.model.aggregates.BidAskBalanceRatioHistory;
import com.kr.economy.tradebatch.trade.domain.model.aggregates.BidAskBalanceRatioHistorySim;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BidAskBalanceRatioHistorySimRepository extends JpaRepository<BidAskBalanceRatioHistorySim, Long> {
}
