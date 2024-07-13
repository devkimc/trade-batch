package com.kr.economy.tradebatch.trade.domain.repositories;

import com.kr.economy.tradebatch.trade.domain.aggregate.AskingPriceIncStatus;
import com.kr.economy.tradebatch.trade.domain.aggregate.BidAskBalanceIncStatus;
import com.kr.economy.tradebatch.trade.domain.aggregate.BidAskBalanceRatioHistory;
import com.kr.economy.tradebatch.trade.domain.aggregate.QBidAskBalanceRatioHistory;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class BidAskBalanceRatioHistoryCustomRepositoryImpl implements BidAskBalanceRatioHistoryCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<BidAskBalanceRatioHistory> getRecentHistoryByBidAskBalance() {
        QBidAskBalanceRatioHistory history = QBidAskBalanceRatioHistory.bidAskBalanceRatioHistory;

        return queryFactory
                .select(history)
                .from(history)
                .where(history.bidAskBalanceIncStatus.ne(BidAskBalanceIncStatus.NONE)
                        .and(history.bidAskBalanceIncStatus.ne(BidAskBalanceIncStatus.FREEZING)))
                .limit(3)
                .orderBy(history.regDate.desc())
                .fetch();
    }

    @Override
    public List<BidAskBalanceRatioHistory> getRecentHistoryByAskingPrice() {
        QBidAskBalanceRatioHistory history = QBidAskBalanceRatioHistory.bidAskBalanceRatioHistory;

        return queryFactory
                .select(history)
                .from(history)
                .where(history.askingPriceIncStatus.ne(AskingPriceIncStatus.NONE)
                        .and(history.askingPriceIncStatus.ne(AskingPriceIncStatus.FREEZING)))
                .limit(4)
                .orderBy(history.regDate.desc())
                .fetch();
    }
}
