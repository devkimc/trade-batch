package com.kr.economy.tradebatch.trade.domain.model.commands;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CalculateTradeReturnCommand {

    private String accountId;           // 트레이딩 봇 ID
    private String ticker;              // 종목 코드
    private String orderDvsnCode;       // 주문 구분 코드 (매수 / 매도)
    private Integer tradingPrice;           // 체결단가
    private Integer tradingQty;             // 체결 수량
}
