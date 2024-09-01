package com.kr.economy.tradebatch.trade.domain.model.aggregates;

import com.kr.economy.tradebatch.trade.domain.constants.BidAskBalanceTrendType;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EntityListeners(AuditingEntityListener.class)
@Slf4j
public class BidAskBalanceRatioHistorySim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;                                        // 순번

    @Column(nullable = false)
    private String ticker;                                  // 종목 코드

    @Column
    private Float bidAskBalanceRatio;                       // 매수매도잔량비

    @Column
    private BidAskBalanceTrendType bidAskBalanceTrendType;  // 매수배도잔량비 증가 추이

    @Column
    private String tradingTime;                             // 체결 시간

    @Column(name = "crt_dtm")
    @CreatedDate
    private LocalDateTime createdDate;                      // 등록 시간

    /**
     * 초기 값
     *
     * @param bidAskBalanceRatio
     */
    public BidAskBalanceRatioHistorySim(String ticker, Float bidAskBalanceRatio) {
        this.ticker = ticker;
        this.bidAskBalanceRatio = bidAskBalanceRatio;
        this.bidAskBalanceTrendType = BidAskBalanceTrendType.NONE;
    }

    public BidAskBalanceTrendType getNextBalanceTrendType(Float nextBidAskBalanceRatio) {
        BidAskBalanceTrendType nextStatus;

        if (bidAskBalanceRatio == null) {
            throw new RuntimeException("getNextStatus 실패");
        }

        if (nextBidAskBalanceRatio > this.bidAskBalanceRatio) {
            nextStatus = BidAskBalanceTrendType.INCREASE;
        } else if (nextBidAskBalanceRatio < this.bidAskBalanceRatio) {
            nextStatus = BidAskBalanceTrendType.DECREASE;
        } else {
            nextStatus = BidAskBalanceTrendType.FREEZING;
        }

        return nextStatus;
    }
}
