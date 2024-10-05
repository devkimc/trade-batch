package com.kr.economy.tradebatch.trade.domain.model.aggregates;

import com.kr.economy.tradebatch.trade.domain.constants.BidAskBalanceTrendType;
import com.kr.economy.tradebatch.trade.domain.constants.PriceTrendType;
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
public class SharePriceHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;                        // 순번

    @Column(nullable = false)
    private String ticker;                  // 종목 코드

    @Column
    private int sharePrice;                 // 주가 (현재가, 체결가)

    @Column
    private PriceTrendType priceTrendType;  // 가격 추세 유형

    @Column
    private Float bidAskBalanceRatio;       // 매수매도잔량비

    @Column
    private BidAskBalanceTrendType bidAskBalanceTrendType;  // 매수배도잔량비 증가 추이

    @Column
    private String tradingTime;             // 체결 시간

    @Column(name = "crt_dtm")
    @CreatedDate
    private LocalDateTime createdDate;      // 등록 시간

    /**
     * 초기 값
     * @param
     */
    public SharePriceHistory(String ticker, int sharePrice, Float bidAskBalanceRatio, String tradingTime) {
        this.ticker = ticker;
        this.sharePrice = sharePrice;
        this.priceTrendType = PriceTrendType.NONE;
        this.bidAskBalanceRatio = bidAskBalanceRatio;
        this.bidAskBalanceTrendType = BidAskBalanceTrendType.NONE;
        this.tradingTime = tradingTime;
    }


    /**
     * 주가 증감 추이 확인
     * @param curSharePrice
     * @return
     */
    public PriceTrendType getNextPriceTrendType(int curSharePrice) {
        PriceTrendType nextPriceTrend;

        if (curSharePrice == 0) {
            throw new RuntimeException("[주가 증감 추이 확인 에러] - 주가 0원");
        }

        if (curSharePrice > this.sharePrice) {
            nextPriceTrend = PriceTrendType.INCREASE;
        } else if (curSharePrice < this.sharePrice) {
            nextPriceTrend = PriceTrendType.DECREASE;
        } else {
            nextPriceTrend = PriceTrendType.FREEZING;
        }

        return nextPriceTrend;
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
