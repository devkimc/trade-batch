package com.kr.economy.tradebatch.trade.domain.model.aggregates;

import com.kr.economy.tradebatch.trade.domain.constants.KisOrderDvsnCode;
import com.kr.economy.tradebatch.trade.domain.constants.OrderDvsnCode;
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
    public boolean isSellSignal(int sharePrice, String currentTradingTime) {
        LocalDateTime now = LocalDateTime.now();

        boolean isHighPoint = sharePrice >= tradingPrice + 300;
        boolean isLowPoint = sharePrice <= tradingPrice - 300;

        // 오후 3시 25분일 경우 모두 매도
        boolean isClosingTime = now.getHour() == 15 && now.getMinute() >= 25;

        if (isClosingTime) {
            log.info("[매도 신호] 장 마감 시간 임박 : {}", now);
        }

        // 매수 후 13분 초과 시 매도 로직 중단
//        String mm = String.valueOf(now.getMonth().getValue());
//        String dd = String.valueOf(now.getDayOfMonth());
//
//        if (mm.length() == 1) {
//            mm = "0" + mm;
//        }
//
//        if (dd.length() == 1) {
//            dd = "0" + dd;
//        }
//
//        LocalDate date = LocalDate.parse(now.getYear() + "-" + mm + "-" + dd);
//        LocalDateTime tradingLdt = date.atTime(Integer.parseInt(tradingTime.substring(0, 2)), Integer.parseInt(tradingTime.substring(2, 4)), Integer.parseInt(tradingTime.substring(4, 6)));
//        LocalDateTime currentTradingLdt = date.atTime(Integer.parseInt(currentTradingTime.substring(0, 2)), Integer.parseInt(currentTradingTime.substring(2, 4)), Integer.parseInt(currentTradingTime.substring(4, 6)));
//
//        // 매수 후 13분 초과 시 매도
//        boolean isLimitTimeout = currentTradingLdt.isAfter(tradingLdt.plusMinutes(13));

        // TODO 테스트 후 주석 제거
//        if (isLimitTimeout) {
//            log.info("[매도 신호] 매수 후 13분 초과 - 매수 시간 : {}", tradingTime);
//        }

        return isHighPoint || isLowPoint || isClosingTime ;
    }

    /**
     * 매수 체결 여부
     * @return
     */
    public boolean isBuyTrade() {
        return OrderDvsnCode.BUY.equals(orderDvsnCode);
    }
}
