package com.kr.economy.tradebatch.trade.domain.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum TradingResultType {

    APPROVE("0"),
    REFUSE("1");

    private String code;

    private static Map<String, TradingResultType> map = Arrays.stream(TradingResultType.values())
            .collect(Collectors.toMap(TradingResultType::getCode, e -> e));

    public static TradingResultType find(String code) {
        return map.get(code);
    }
}
