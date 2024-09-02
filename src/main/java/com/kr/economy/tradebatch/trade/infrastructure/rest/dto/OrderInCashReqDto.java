package com.kr.economy.tradebatch.trade.infrastructure.rest.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrderInCashReqDto {

    private String CANO;            // 종합 계좌번호 계좌번호 체계(8-2)의 앞 8자리
    private String ACNT_PRDT_CD;    // 계좌 상품코드 계좌번호 체계(8-2)의 뒤 2자리
    private String PDNO;            // 종목 코드 (6자리)
    private String ORD_DVSN;        // 주문 구분
    private String ORD_QTY;         // 주문 수량
    private String ORD_UNPR;        // 주문 단가
}
