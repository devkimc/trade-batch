package com.kr.economy.tradebatch.trade.domain.aggregate;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BidAskBalanceIncStatus {

    NONE("0"),
    INCREASE("1"),
    DECREASE("2"),
    FREEZING("3");

    private String code;
}
