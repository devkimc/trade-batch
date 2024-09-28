package com.kr.economy.tradebatch.trade.domain.model.aggregates;

import com.kr.economy.tradebatch.trade.application.OrderInCashCommand;
import com.kr.economy.tradebatch.trade.domain.constants.KisOrderDvsnCode;
import com.kr.economy.tradebatch.trade.domain.constants.OrderDvsnCode;
import com.kr.economy.tradebatch.trade.domain.constants.OrderStatus;
import com.kr.economy.tradebatch.trade.infrastructure.rest.dto.OrderInCashResDto;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

// TODO 지금 당장은 주문 내역이 필요하지 않은 것을 보임, 추후에 다른 사용자도 이용할 경우 필요하지 않을가?
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
    private String accountId;                   // 트레이딩 봇 계정

    @Column(nullable = false)
    private String ticker;                      // 종목 코드

    @Column(nullable = false)
    private OrderStatus orderStatus;            // 주문 상태

    @Column(nullable = false)
    private OrderDvsnCode orderDvsnCode;        // 주문 구분 코드 (매수 / 매도)

    @Column(nullable = false)
    private Float sharePrice;                   // 주문 시 주가

    @Column
    private Float orderPrice;                   // 시장가 주문의 경우 null

    @Column(nullable = false)
    private int qty;                          // 주문 수량

    @Column(nullable = false)
    private KisOrderDvsnCode kisOrderDvsnCode;  // 한투 주문 구분 코드  (시장가 주문, 지정가 주문 등)

    @Column
    private String kisOrderId;                  // 한투 주문 번호 (한투에 주문 요청 후 받는 응답)

    @Column
    private String resultCode;                  // 결과 코드 (한투에 주문 요청 후 받는 응답)

    @Column
    private String resultMsg;                   // 결과 메시지 (한투에 주문 요청 후 받는 응답)

    @Column(name = "crt_dtm")
    @CreatedDate
    private LocalDateTime createdDate;          // 등록 시간

    @Column(name = "chn_dtm")
    @LastModifiedDate
    private LocalDateTime lastModifiedDate;     // 수정 시간

    /**
     * 현금 주문 정보
     * @param orderInCashCommand
     */
    public Order (OrderInCashCommand orderInCashCommand) {
        this.accountId = orderInCashCommand.getAccountId();
        this.ticker = orderInCashCommand.getTicker();
        this.orderDvsnCode = orderInCashCommand.getOrderDvsnCode();
        this.sharePrice = orderInCashCommand.getSharePrice();
        this.orderPrice = orderInCashCommand.getOrderPrice();
        this.qty = orderInCashCommand.getQty();
        this.kisOrderDvsnCode = orderInCashCommand.getKisOrderDvsnCode();
    }

    /**
     * 주문 요청 후 결과를 업데이트 한다.
     * @param orderInCashResDto
     */
    public void updateOrderResult(OrderInCashResDto orderInCashResDto) {
        String resultCode = orderInCashResDto.getRt_cd();
        String resultMsg = orderInCashResDto.getMsg1();

        if ("0".equals(resultCode)) {
            this.orderStatus = OrderStatus.SUCCESS;
        } else {
            this.orderStatus = OrderStatus.FAIL;
            log.error("[주문 요청 실패] - 응답 코드: {}, 응답 메시지 {}", resultCode, resultMsg);
        }

        this.kisOrderId = orderInCashResDto.getOutput().getODNO();
        this.resultCode = resultCode;
        this.resultMsg = resultMsg;
    }
}
