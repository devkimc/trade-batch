package com.kr.economy.tradebatch.trade.domain.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SocketTrType {

    TRADING_RESULT("H0STCNI0", "[실전투자] 실시간 주식 체결통보"),
    TRADING_PRICE("H0STCNT0", "[실전/모의투자] 실시간 주식 체결가"),
    ;

    private String id;
    private String desc;
}
