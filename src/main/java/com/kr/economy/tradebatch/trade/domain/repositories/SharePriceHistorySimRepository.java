package com.kr.economy.tradebatch.trade.domain.repositories;


import com.kr.economy.tradebatch.trade.domain.model.aggregates.SharePriceHistory;
import com.kr.economy.tradebatch.trade.domain.model.aggregates.SharePriceHistorySim;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SharePriceHistorySimRepository extends JpaRepository<SharePriceHistorySim, Long> {
}
