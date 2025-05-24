package com.kr.economy.tradebatch.trade.application.queryservices;


import com.kr.economy.tradebatch.trade.domain.model.aggregates.Order;
import com.kr.economy.tradebatch.trade.infrastructure.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class OrderQueryService {
    private final OrderRepository orderRepository;

    public Optional<Order> getLastOrder(String accountId, String ticker) {
        return orderRepository.getLastOrder(accountId, ticker);
    }

    public Optional<Order> getOrderByOrderNo(String orderNo) {
        return orderRepository.findByKisOrderNo(orderNo);
    }
}
