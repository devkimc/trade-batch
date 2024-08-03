package com.kr.economy.tradebatch.trade.domain.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderStatus {

    REQUEST("R"),
    SUCCESS("S"),
    FAIL("F");

    private String code;
}
