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

    /**
     * 미체결 주문 내역 존재 유무 조회
     * @param accountId
     * @param ticker
     * @return
     */
//    public boolean existsNotTradingOrder(String accountId, String ticker) {
//        Optional<Order> lastOrder = orderRepository.getLastOrder(accountId, ticker);
//
//        if (lastOrder.isPresent() && lastOrder.get().isNotTrading()) {
//            return true;
//        } else {
//            return false;
//        }
//    }

    public Optional<Order> getLastOrder(String accountId, String ticker) {
        return orderRepository.getLastOrder(accountId, ticker);
    }
}
