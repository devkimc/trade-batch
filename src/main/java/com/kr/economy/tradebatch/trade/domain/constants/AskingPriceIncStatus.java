package com.kr.economy.tradebatch.trade.domain.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AskingPriceIncStatus {

    NONE("0"),
    INCREASE("1"),
    DECREASE("2"),
    FREEZING("3");

    private String code;
}
