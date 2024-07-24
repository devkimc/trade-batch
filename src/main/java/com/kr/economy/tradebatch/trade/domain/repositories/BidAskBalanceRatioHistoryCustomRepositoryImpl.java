package com.kr.economy.tradebatch.trade.domain.repositories;

import com.kr.economy.tradebatch.trade.domain.constants.BidAskBalanceTrendType;
import com.kr.economy.tradebatch.trade.domain.model.aggregates.BidAskBalanceRatioHistory;
import com.kr.economy.tradebatch.trade.domain.model.aggregates.QBidAskBalanceRatioHistory;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class BidAskBalanceRatioHistoryCustomRepositoryImpl implements BidAskBalanceRatioHistoryCustomRepository {

//    private final JPAQueryFactory queryFactory;

    @Override
    public List<BidAskBalanceRatioHistory> getRecentHistoryByBidAskBalance(String ticker) {
        QBidAskBalanceRatioHistory history = QBidAskBalanceRatioHistory.bidAskBalanceRatioHistory;

        return null;
//        return queryFactory
//                .select(history)
//                .from(history)
//                .where(history.ticker.eq(ticker)
//                        .and(history.bidAskBalanceTrendType.ne(BidAskBalanceTrendType.NONE))
//                        .and(history.bidAskBalanceTrendType.ne(BidAskBalanceTrendType.FREEZING)))
//                .orderBy(history.createdDate.desc())
//                .limit(3)
//                .fetch();
    }
}
