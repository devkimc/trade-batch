package com.kr.economy.tradebatch.trade.domain.model.aggregates;

import com.kr.economy.tradebatch.trade.domain.constants.OrderDvsnCode;
import com.kr.economy.tradebatch.trade.domain.model.valueObject.TradeReturnId;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EntityListeners(AuditingEntityListener.class)
@Slf4j
public class TradeReturn {

    @EmbeddedId
    private TradeReturnId tradeReturnId;    // 순번

    @Column
    private Integer totalBuyPrice;              // 총 매수 금액

    @Column
    private Integer totalSellPrice;             // 총 매도 금액

    @Column
    private Float tradingFee;             // 수수료 + 증권거래세

    @Column(name = "crt_dtm")
    @CreatedDate
    private LocalDateTime createdDate;      // 등록 시간

    @Column(name = "chn_dtm")
    @LastModifiedDate
    private LocalDateTime lastModifiedDate; // 수정 시간

    // 체결 금액 추가
    public void changeTradePrice(OrderDvsnCode orderDvsnCode, int price) {
        if (OrderDvsnCode.BUY.equals(orderDvsnCode)) {
            this.totalBuyPrice = this.totalBuyPrice + price;
        } else if (OrderDvsnCode.SELL.equals(orderDvsnCode)) {
            this.totalSellPrice = this.totalSellPrice + price;
        } else {
            throw new RuntimeException("[수익 계산 실패] 주문 구문 코드를 확인하세요.");
        }
    }

    // 손실 금액 반환
    public float getLossPrice() {
        return Math.abs(this.totalSellPrice - this.totalBuyPrice - this.tradingFee);
    }

    // 손실 여부 반환
    public boolean isSufferedLoss() {
        return totalSellPrice - totalBuyPrice < 0;
    }

    /**
     * 손실 한도 여부
     * @param dailyLossLimitPrice
     * @return
     */
    public boolean isLossLimit(int dailyLossLimitPrice) {

        // 당일 손실 여부 && 당일 손실 금액 >= 당일 최대 손실 금액
        return this.isSufferedLoss() && this.getLossPrice() >= dailyLossLimitPrice;
    }

    // 수수료 계산
    public void changeTradingFee(OrderDvsnCode orderDvsnCode, int price) {
        if (OrderDvsnCode.SELL.equals(orderDvsnCode)) {
            this.tradingFee = (float) (this.tradingFee + (price * 0.0018));
        }
    }
}
