package com.kr.economy.tradebatch.trade.infrastructure.repositories;

import com.kr.economy.tradebatch.trade.domain.constants.PriceTrendType;
import com.kr.economy.tradebatch.trade.domain.model.aggregates.StockQuotes;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.kr.economy.tradebatch.trade.domain.model.aggregates.QStockQuotes.stockQuotes;


@Repository
public class StockQuotesRepositoryCustomImpl extends QuerydslRepositorySupport implements StockQuotesRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public StockQuotesRepositoryCustomImpl(JPAQueryFactory jpaQueryFactory) {
        super(StockQuotesRepositoryCustomImpl.class);
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public List<StockQuotes> getRecentTrendHistory(String ticker) {
        return jpaQueryFactory
                .selectFrom(stockQuotes)
                .where(
                        stockQuotes.ticker.eq(ticker)
                                .and(stockQuotes.priceTrendType.ne(PriceTrendType.NONE))
                                .and(stockQuotes.priceTrendType.ne(PriceTrendType.FREEZING))
                )
                .orderBy(stockQuotes.Id.desc())
                .limit(3)
                .fetch();
    }
}
