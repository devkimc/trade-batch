package com.kr.economy.tradebatch.trade.infrastructure.repositories;

import com.kr.economy.tradebatch.trade.domain.constants.PriceTrendType;
import com.kr.economy.tradebatch.trade.domain.model.aggregates.ExecutionHistory;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.kr.economy.tradebatch.trade.domain.model.aggregates.QExecutionHistory.executionHistory;

@Repository
@RequiredArgsConstructor
//public class ExecutionHistoryRepositoryCustomImpl extends QuerydslRepositorySupport implements ExecutionHistoryRepositoryCustom {
public class ExecutionHistoryRepositoryCustomImpl implements ExecutionHistoryRepositoryCustom {

//    private final JPAQueryFactory jpaQueryFactory;

//    public ExecutionHistoryRepositoryCustomImpl(JPAQueryFactory jpaQueryFactory) {
//        super(ExecutionHistoryRepositoryCustomImpl.class);
//        this.jpaQueryFactory = jpaQueryFactory;
//    }

    @Override
    public List<ExecutionHistory> getRecentTrendHistory(String ticker) {
        return null;
//        return jpaQueryFactory
//                .selectFrom(executionHistory)
//                .where(
//                        executionHistory.ticker.eq(ticker)
//                                .and(executionHistory.priceTrendType.ne(PriceTrendType.NONE))
//                                .and(executionHistory.priceTrendType.ne(PriceTrendType.FREEZING))
//                )
//                .orderBy(executionHistory.createdDate.desc())
//                .limit(4)
//                .fetch();
    }
}
