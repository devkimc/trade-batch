package com.kr.economy.tradebatch.trade.infrastructure.repositories;

import com.kr.economy.tradebatch.trade.domain.model.aggregates.TradeReturn;
import com.kr.economy.tradebatch.trade.domain.model.valueObject.TradeReturnId;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TradeReturnRepository extends JpaRepository<TradeReturn, TradeReturnId> {
}
