package com.kr.economy.tradebatch.trade.domain.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderDvsnCode {

    BUY("B"),
    SELL("S");

    private String code;
}
