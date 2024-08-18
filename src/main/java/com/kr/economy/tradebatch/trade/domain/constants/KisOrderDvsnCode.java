package com.kr.economy.tradebatch.trade.domain.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum KisOrderDvsnCode {

    MARKET_ORDER("00"),
    LIMIT_ORDER("01");

    private String code;

    private static Map<String, KisOrderDvsnCode> map = Arrays.stream(KisOrderDvsnCode.values())
            .collect(Collectors.toMap(KisOrderDvsnCode::getCode, e -> e));

    public static KisOrderDvsnCode find(String code) {
        return map.get(code);
    }
}
