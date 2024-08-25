package com.kr.economy.tradebatch.trade.infrastructure.repositories;

import com.kr.economy.tradebatch.trade.domain.model.aggregates.TradingHistory;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.kr.economy.tradebatch.trade.domain.model.aggregates.QTradingHistory.tradingHistory;

@Repository
public class TradingHistoryRepositoryCustomImpl extends QuerydslRepositorySupport implements TradingHistoryRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public TradingHistoryRepositoryCustomImpl(JPAQueryFactory jpaQueryFactory) {
        super(TradingHistoryRepositoryCustomImpl.class);
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public Optional<TradingHistory> getLastTradingHistory(String ticker) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(tradingHistory)
                        .where(tradingHistory.ticker.eq(ticker))
                        .orderBy(tradingHistory.Id.desc())
                        .fetchFirst()
        );
    }
}
