package com.kr.economy.tradebatch.trade.domain.repositories;


import com.kr.economy.tradebatch.trade.domain.model.aggregates.ExecutionHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExecutionHistoryRepository extends JpaRepository<ExecutionHistory, Long> {

    /**
     * 마지막 체결 이력 조회
     * @param ticker
     * @return
     */
    Optional<ExecutionHistory> findTopByTickerOrderByCreatedDateDesc(String ticker);
}
