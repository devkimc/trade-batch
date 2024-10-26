package com.kr.economy.tradebatch.trade.infrastructure.repositories;

import com.kr.economy.tradebatch.trade.domain.model.aggregates.Order;

import java.util.Optional;

public interface OrderRepositoryCustom {
    Optional<Order> getLastOrder(String accountId, String ticker);
}
