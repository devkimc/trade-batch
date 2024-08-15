package com.kr.economy.tradebatch.trade.infrastructure.repositories;

import com.kr.economy.tradebatch.trade.domain.constants.PriceTrendType;
import com.kr.economy.tradebatch.trade.domain.model.aggregates.QSharePriceHistory;
import com.kr.economy.tradebatch.trade.domain.model.aggregates.SharePriceHistory;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.kr.economy.tradebatch.trade.domain.model.aggregates.QSharePriceHistory.*;

@Repository
//@RequiredArgsConstructor
public class SharePriceHistoryRepositoryCustomImpl extends QuerydslRepositorySupport implements SharePriceHistoryRepositoryCustom {
//public class SharePriceHistoryRepositoryCustomImpl implements SharePriceHistoryRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public SharePriceHistoryRepositoryCustomImpl(JPAQueryFactory jpaQueryFactory) {
        super(SharePriceHistoryRepositoryCustomImpl.class);
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public List<SharePriceHistory> getRecentTrendHistory(String ticker) {
        return jpaQueryFactory
                .selectFrom(sharePriceHistory)
                .where(
                        sharePriceHistory.ticker.eq(ticker)
                                .and(sharePriceHistory.priceTrendType.ne(PriceTrendType.NONE))
                                .and(sharePriceHistory.priceTrendType.ne(PriceTrendType.FREEZING))
                )
                .orderBy(sharePriceHistory.createdDate.desc())
                .limit(4)
                .fetch();
    }
}
