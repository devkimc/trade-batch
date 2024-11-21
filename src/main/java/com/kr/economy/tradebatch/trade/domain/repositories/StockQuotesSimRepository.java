package com.kr.economy.tradebatch.trade.domain.repositories;


import com.kr.economy.tradebatch.trade.domain.model.aggregates.StockQuotesSim;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockQuotesSimRepository extends JpaRepository<StockQuotesSim, Long> {
}
