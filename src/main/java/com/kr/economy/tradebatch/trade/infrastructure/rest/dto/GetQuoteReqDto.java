package com.kr.economy.tradebatch.trade.infrastructure.rest.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GetQuoteReqDto {

    private String tr_id;       // 거래ID
    private String tr_key;      // 구분값 - 종목번호 (6자리)
}
