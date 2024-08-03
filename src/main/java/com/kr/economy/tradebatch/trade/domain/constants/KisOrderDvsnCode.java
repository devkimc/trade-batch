package com.kr.economy.tradebatch.trade.domain.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum KisOrderDvsnCode {

    MARKET_ORDER("00"),
    LIMIT_ORDER("01");

    private String code;
}
