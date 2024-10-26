package com.kr.economy.tradebatch.trade.infrastructure.repositories;

import com.kr.economy.tradebatch.trade.domain.model.aggregates.Order;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.kr.economy.tradebatch.trade.domain.model.aggregates.QOrder.*;

@Repository
public class OrderRepositoryCustomImpl extends QuerydslRepositorySupport implements OrderRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public OrderRepositoryCustomImpl(JPAQueryFactory jpaQueryFactory) {
        super(OrderRepositoryCustomImpl.class);
        this.jpaQueryFactory = jpaQueryFactory;
    }

    public Optional<Order> getLastOrder(String accountId, String ticker) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(order)
                        .where(
                                order.ticker.eq(ticker)
                                        .and(order.accountId.eq(accountId))
                        )
                        .orderBy(order.Id.desc())
                        .fetchFirst()
        );
    }
}
