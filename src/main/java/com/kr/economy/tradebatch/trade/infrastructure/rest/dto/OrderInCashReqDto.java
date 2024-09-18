package com.kr.economy.tradebatch.trade.infrastructure.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrderInCashReqDto {

    @JsonProperty("CANO")
    private String cano;            // 종합 계좌번호 계좌번호 체계(8-2)의 앞 8자리

    @JsonProperty("ACNT_PRDT_CD")
    private String acntPrdtCd;    // 계좌 상품코드 계좌번호 체계(8-2)의 뒤 2자리

    @JsonProperty("PDNO")
    private String pdno;            // 종목 코드 (6자리)

    @JsonProperty("ORD_DVSN")
    private String ordDvsn;        // 주문 구분

    @JsonProperty("ORD_QTY")
    private String ordQty;         // 주문 수량

    @JsonProperty("ORD_UNPR")
    private String ordUnpr;        // 주문 단가
}
