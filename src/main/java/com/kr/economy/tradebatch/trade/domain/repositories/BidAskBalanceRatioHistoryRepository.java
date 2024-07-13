package com.kr.economy.tradebatch.trade.domain.repositories;

import com.kr.economy.tradebatch.trade.domain.aggregate.BidAskBalanceRatioHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BidAskBalanceRatioHistoryRepository extends JpaRepository<BidAskBalanceRatioHistory, Long> {

    Optional<BidAskBalanceRatioHistory> findTopByOrderByRegDateDesc();
}
