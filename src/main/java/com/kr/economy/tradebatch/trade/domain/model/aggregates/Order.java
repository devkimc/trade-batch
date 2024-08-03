package com.kr.economy.tradebatch.trade.domain.model.aggregates;

import com.kr.economy.tradebatch.trade.domain.constants.KisOrderDvsnCode;
import com.kr.economy.tradebatch.trade.domain.constants.OrderDvsnCode;
import com.kr.economy.tradebatch.trade.domain.constants.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
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

    // TODO: Q. 주문한 수량이 여러개일 경우 각각 다른 가격에 체결되었다면, 각각 주문번호를 새로 등록할 것인지?
    // TODO: A. 체결 내역이 각각 저장 된다. 주문 응답 후 체결 내역 조회 요청 필요
    @Column(nullable = false)
    private Float qty;                          // 주문 수량

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
     * 주문 요청 후 결과를 업데이트 한다.
     * @param kisOrderId
     * @param resultCode
     * @param resultMsg
     */
    public void updateOrderResult(String kisOrderId,
                                  String resultCode,
                                  String resultMsg) {
        this.kisOrderId = kisOrderId;
        this.resultCode = resultCode;
        this.resultMsg = resultMsg;
    }
}
