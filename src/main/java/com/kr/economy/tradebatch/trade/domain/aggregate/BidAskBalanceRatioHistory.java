package com.kr.economy.tradebatch.trade.domain.aggregate;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
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
public class BidAskBalanceRatioHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;                                        // 순번

    @Column
    private Float askingPrice;                              // 현재가 (매수 1호가)

    @Column
    private AskingPriceIncStatus askingPriceIncStatus;      // 호가 증가 추이

    @Column
    private Float bidAskBalanceRatio;                       // 매수매도잔량비

    @Column
    private BidAskBalanceIncStatus bidAskBalanceIncStatus;  // 매수배도잔량비 증가 추이

    @Column
    private boolean buySign;                                // 매수 신호

    @Column
    @CreatedDate
    private LocalDateTime regDate;                          // 등록 시간

    @Column
    @LastModifiedDate
    private LocalDateTime modDate;                          // 수정 시간

    /**
     * 초기 값
     *
     * @param bidAskBalanceRatio
     */
    public BidAskBalanceRatioHistory(Float bidAskBalanceRatio, Float askingPrice) {
        this.bidAskBalanceRatio = bidAskBalanceRatio;
        this.bidAskBalanceIncStatus = BidAskBalanceIncStatus.NONE;
        this.askingPrice = askingPrice;
    }

    // TODO 네이밍 리팩토링
    public BidAskBalanceIncStatus getNextStatus(Float nextBidAskBalanceRatio) {
        BidAskBalanceIncStatus nextStatus;

        if (bidAskBalanceRatio == null) {
            throw new RuntimeException("getNextStatus 실패");
        }

        if (nextBidAskBalanceRatio > this.bidAskBalanceRatio) {
            nextStatus = BidAskBalanceIncStatus.INCREASE;
        } else if (nextBidAskBalanceRatio < this.bidAskBalanceRatio) {
            nextStatus = BidAskBalanceIncStatus.DECREASE;
        } else {
            nextStatus = BidAskBalanceIncStatus.FREEZING;
        }

        return nextStatus;
    }


    /**
     * 호가 증감 추이 확인
     * @param nextAskingPrice
     * @return
     */
    public AskingPriceIncStatus getNextAskingPriceStatus(Float nextAskingPrice) {
        AskingPriceIncStatus nextStatus;

        if (nextAskingPrice == null) {
            throw new RuntimeException("getNextStatus 실패");
        }

        if (nextAskingPrice > this.askingPrice) {
            nextStatus = AskingPriceIncStatus.INCREASE;
        } else if (nextAskingPrice < this.askingPrice) {
            nextStatus = AskingPriceIncStatus.DECREASE;
        } else {
            nextStatus = AskingPriceIncStatus.FREEZING;
        }

//        log.info("[호가 증감 추이] 이전 가격: {}, 현재 가격: {}, 증감 여부: {}", this.askingPrice, nextAskingPrice, nextStatus);

        return nextStatus;
    }

    public void setBuySign() {
        this.buySign = true;
    }
}
