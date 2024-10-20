package com.kr.economy.tradebatch.trade.domain.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderStatus {

    REQUEST("R"),
    ORDER_SUCCESS("OS"),
    TRADE_SUCCESS("TS"),
    FAIL("F");

    private String code;
}
