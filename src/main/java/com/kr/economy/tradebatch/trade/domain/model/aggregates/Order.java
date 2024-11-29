package com.kr.economy.tradebatch.trade.domain.model.aggregates;

import com.kr.economy.tradebatch.trade.domain.constants.KisOrderDvsnCode;
import com.kr.economy.tradebatch.trade.domain.constants.OrderDvsnCode;
import com.kr.economy.tradebatch.trade.domain.constants.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Table(name = "orders")
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EntityListeners(AuditingEntityListener.class)
@Slf4j
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;                            // 순번

    @Column(nullable = false)
    private String accountId;                   // 트레이딩 봇 ID

    @Column(nullable = false)
    private String ticker;                      // 종목 코드

    @Column(nullable = false)
    private OrderStatus orderStatus;            // 주문 상태

    @Column
    private OrderDvsnCode orderDvsnCode;        // 주문 구분 코드 (매수 / 매도)

    @Column
    private int quotedPrice;                     // 주문 시 주가

    @Column
    private int orderPrice;                     // 시장가 주문의 경우 0

    @Column(nullable = false)
    private int orderQty;                            // 주문 수량

    @Column(nullable = false)
    private KisOrderDvsnCode kisOrderDvsnCode;  // 한투 주문 구분 코드  (시장가 주문, 지정가 주문 등)

    @Column
    private String kisOrderNo;                  // 주문 번호

    @Column
    private String kisOriginOrderNo;            // 한투 원주문 번호

    @Column(name = "crt_dtm")
    @CreatedDate
    private LocalDateTime createdDate;          // 등록 시간

    @Column(name = "chn_dtm")
    @LastModifiedDate
    private LocalDateTime lastModifiedDate;     // 수정 시간

    public void trade() {
        this.orderStatus = OrderStatus.TRADE_SUCCESS;
    }

    /**
     * 거래된 주문인지 확인
     * @return
     */
    public boolean isTraded() {
        return OrderStatus.TRADE_SUCCESS.equals(this.orderStatus);
    }

    public boolean existsNotTradedStock(int tradedQty) {
        boolean exists = this.orderQty != tradedQty;

        if (exists) {
            log.info("[주문, 체결 수량 비교] - 체결되지 않은 주문이 존재합니다. 주문번호: {}, 주문수량: {}, 체결수량 합: {}", this.getKisOrderNo(), this.getOrderQty(), tradedQty);
        }

        return exists;
    }
}
