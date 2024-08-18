package com.kr.economy.tradebatch.trade.application;

import com.kr.economy.tradebatch.trade.domain.constants.KisOrderDvsnCode;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CreateTradingHistoryCommand {

    private String ticker;                  // 종목 코드
    private String orderDvsnCode;        // 주문 구분 코드 (매수 / 매도)
    private int tradingPrice;                 // 체결단가
    private int tradingQty;                          // 체결 수량
    private String tradingResultType;
    private String kisOrderDvsnCode;  // 한투 주문 구분 코드  (시장가 주문, 지정가 주문 등)
    private String tradingTime;
    private String kisId;                   // 한투 ID
    private String kisOrderId;                  // 한투 주문 번호
    private String kisOrOrderId;                  // 한투 원주문 번호
}
