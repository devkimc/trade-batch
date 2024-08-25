package com.kr.economy.tradebatch.trade.domain.model.aggregates;

import com.kr.economy.tradebatch.trade.domain.constants.KisOrderDvsnCode;
import com.kr.economy.tradebatch.trade.domain.constants.OrderDvsnCode;
import com.kr.economy.tradebatch.trade.domain.constants.PriceTrendType;
import com.kr.economy.tradebatch.trade.domain.constants.TradingResultType;
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
public class TradingHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;                        // 순번

    @Column(nullable = false)
    private String ticker;                  // 종목 코드

    @Column(nullable = false)
    private OrderDvsnCode orderDvsnCode;        // 주문 구분 코드 (매수 / 매도)

    @Column
    private int tradingPrice;                 // 체결단가

    @Column(nullable = false)
    private int tradingQty;                          // 체결 수량

    // 거부여부
    @Column(nullable = false)
    private TradingResultType tradingResultType;

    // 주문 종류
    @Column(nullable = false)
    private KisOrderDvsnCode kisOrderDvsnCode;  // 한투 주문 구분 코드  (시장가 주문, 지정가 주문 등)

    // 체결 시간 (HHMMSS)
    @Column
    private String tradingTime;

    // 고객 ID
    @Column
    private String kisId;                   // 한투 ID

    // 주문번호
    @Column
    private String kisOrderId;                  // 한투 주문 번호

    // 원주문번호
    @Column
    private String kisOrOrderId;                  // 한투 원주문 번호

    // 등록 시간
    @Column(name = "crt_dtm")
    @CreatedDate
    private LocalDateTime createdDate;      // 등록 시간

    /**
     * 매도 신호 여부
     * @param sharePrice
     * @return
     */
    public boolean isSellSignal(int sharePrice) {
        boolean isHighPoint = sharePrice >= tradingPrice + 300;
        boolean isLowPoint = sharePrice <= tradingPrice - 600;
        boolean isClosingTime = LocalDateTime.now().getHour() == 3 && LocalDateTime.now().getMinute() == 29;

        return isHighPoint || isLowPoint || isClosingTime;
    }

    /**
     * 매수 체결 여부
     * @return
     */
    public boolean isBuyTrade() {
        return OrderDvsnCode.BUY.equals(orderDvsnCode);
    }
}
