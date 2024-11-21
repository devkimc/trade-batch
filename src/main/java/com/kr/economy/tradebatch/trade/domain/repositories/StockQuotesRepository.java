package com.kr.economy.tradebatch.trade.domain.repositories;


import com.kr.economy.tradebatch.trade.domain.model.aggregates.StockQuotes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StockQuotesRepository extends JpaRepository<StockQuotes, Long> {

    /**
     * 마지막 체결 이력 조회
     * @param ticker
     * @return
     */
    Optional<StockQuotes> findTopByTickerOrderByIdDesc(String ticker);


    List<StockQuotes> findTop2ByTickerOrderByIdDesc(String ticker);
}
