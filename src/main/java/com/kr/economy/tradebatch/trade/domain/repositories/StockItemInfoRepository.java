package com.kr.economy.tradebatch.trade.domain.repositories;


import com.kr.economy.tradebatch.trade.domain.model.aggregates.StockItemInfo;
import org.springframework.data.jpa.repository.JpaRepository;


public interface StockItemInfoRepository extends JpaRepository<StockItemInfo, String> {
}
