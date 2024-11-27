package com.kr.economy.tradebatch.trade.domain.model.commands;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CreateTradingHistoryProcessCommand {

    private String ticker;              // 종목 코드
    private String orderDvsnCode;       // 주문 구분 코드 (매수 / 매도)
    private String tradingPrice;        // 체결 단가
    private String tradingQty;          // 체결 수량
    private String kisOrderDvsnCode;    // 한투 주문 구분 코드  (시장가 주문, 지정가 주문 등)
    private String tradingTime;         // 체결 시간
    private String kisId;               // 한투 ID
    private String kisOrderId;          // 한투 주문 번호
    private String kisOrOrderId;        // 한투 원주문 번호
    private String tradeResultCode;     // 한투 원주문 번호
    private String tradeResult;
    private String accountId;
}
