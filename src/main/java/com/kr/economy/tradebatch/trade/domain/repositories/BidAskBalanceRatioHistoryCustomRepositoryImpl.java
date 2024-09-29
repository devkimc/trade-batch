package com.kr.economy.tradebatch.trade.domain.repositories;

import com.kr.economy.tradebatch.trade.domain.constants.BidAskBalanceTrendType;
import com.kr.economy.tradebatch.trade.domain.model.aggregates.BidAskBalanceRatioHistory;
import com.kr.economy.tradebatch.trade.domain.model.aggregates.QBidAskBalanceRatioHistory;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BidAskBalanceRatioHistoryCustomRepositoryImpl extends QuerydslRepositorySupport implements BidAskBalanceRatioHistoryCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public BidAskBalanceRatioHistoryCustomRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        super(BidAskBalanceRatioHistoryCustomRepositoryImpl.class);
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public List<BidAskBalanceRatioHistory> getRecentHistoryByBidAskBalance(String ticker) {
        QBidAskBalanceRatioHistory history = QBidAskBalanceRatioHistory.bidAskBalanceRatioHistory;

        return jpaQueryFactory
                .select(history)
                .from(history)
                .where(history.ticker.eq(ticker)
                        .and(history.bidAskBalanceTrendType.ne(BidAskBalanceTrendType.NONE))
                        .and(history.bidAskBalanceTrendType.ne(BidAskBalanceTrendType.FREEZING)))
                .orderBy(history.Id.desc())
                .limit(1)
                .fetch();
    }
}
