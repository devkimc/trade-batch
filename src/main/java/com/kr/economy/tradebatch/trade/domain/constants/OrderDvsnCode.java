package com.kr.economy.tradebatch.trade.domain.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum OrderDvsnCode {

    SELL("01", "S"),
    BUY("02", "B");

    private String code;
    private String value;

    private static Map<String, OrderDvsnCode> map = Arrays.stream(OrderDvsnCode.values())
            .collect(Collectors.toMap(OrderDvsnCode::getCode, e -> e));

    public static OrderDvsnCode find(String code) {
        return map.get(code);
    }
}
