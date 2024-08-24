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

    // TODO: 오늘 날짜만 조회하도록 하는 조건 추가되어야 함
    @Override
    public Optional<TradingHistory> getLastTradingHistory(String ticker) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(tradingHistory)
                        .where(
                                tradingHistory.ticker.eq(ticker)
                        )
                        .orderBy(tradingHistory.Id.desc())
                        .fetchFirst()
        );
    }
}
