package com.kr.economy.tradebatch.trade.domain.model.aggregates;

import com.kr.economy.tradebatch.trade.domain.constants.PriceTrendType;
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
public class ExecutionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;                        // 순번

    @Column(nullable = false)
    private String ticker;                  // 종목 코드

    @Column
    private Float sharePrice;               // 주가 (현재가, 체결가)

    @Column
    private PriceTrendType priceTrendType;  // 가격 추세 유형

    @Column
    private boolean buySign;                // 매수 신호

    @Column(name = "crt_dtm")
    @CreatedDate
    private LocalDateTime createdDate;      // 등록 시간

    @Column(name = "chn_dtm")
    @LastModifiedDate
    private LocalDateTime lastModifiedDate; // 수정 시간

    /**
     * 초기 값
     * @param
     */
    public ExecutionHistory(String ticker, Float sharePrice) {
        this.ticker = ticker;
        this.sharePrice = sharePrice;
        this.priceTrendType = PriceTrendType.NONE;
    }


    /**
     * 호가 증감 추이 확인
     * @param sharePrice
     * @return
     */
    public PriceTrendType getNextPriceTrendType(Float sharePrice) {
        PriceTrendType nextPriceTrend;

        if (sharePrice == null) {
            throw new RuntimeException("getNextStatus 실패");
        }

        if (sharePrice > this.sharePrice) {
            nextPriceTrend = PriceTrendType.INCREASE;
        } else if (sharePrice < this.sharePrice) {
            nextPriceTrend = PriceTrendType.DECREASE;
        } else {
            nextPriceTrend = PriceTrendType.FREEZING;
        }

//        log.info("[호가 증감 추이] 이전 가격: {}, 현재 가격: {}, 증감 여부: {}", this.askingPrice, nextAskingPrice, nextPriceTrend);

        return nextPriceTrend;
    }

    public void setBuySign() {
        this.buySign = true;
    }
}
