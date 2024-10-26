package com.kr.economy.tradebatch.common.constants;

public class KisStaticValues {

    // 거래 ID: [실전/모의투자] 실시간 주식 체결가
    public static final String TR_ID_H0STCNT0 = "H0STCNT0";

    // 거래 ID: [실전투자] 실시간 주식 체결통보
//    public static final String TR_ID_H0STCNI0 = "H0STCNI0";

    // 거래 ID: [모의투자] 실시간 주식 체결통보
    public static final String TR_ID_H0STCNI9 = "H0STCNI9";

    // 거래 ID: [모의투자] 주식 현금 매수 주문
    public static final String TR_ID_VTTC0802U = "VTTC0802U";

    // 거래 ID: [모의투자] 주식 현금 매도 주문
    public static final String TR_ID_VTTC0801U = "VTTC0801U";

    // 거래 ID: [실전투자] 주식 현금 매수 주문
    public static final String TR_ID_TTTC0802U = "TTTC0802U";

    // 거래 ID: [실전투자] 주식 현금 매도 주문
    public static final String TR_ID_TTTC0801U = "TTTC0801U";

    /* 종목 코드 */
    public static final String TICKER_SAMSUNG = "005930";
    public static final String TICKER_SK_HYNIX = "000660";

    /* 고객 타입 */
    public static final String CUST_TYPE_PERSONAL = "P";

    /* 거래 타입 */
    public static final String TRADE_TYPE_REGISTRATION = "1";

    /* 실시간 체결 통보 응답 - 주문 접수 */
    public static final String TRADE_RES_CODE_ORDER_TRANSMISSION = "1";

    /* 실시간 체결 통보 응답 - 체결 완료 */
    public static final String TRADE_RES_CODE_TRADE_COMPLETION = "2";
}
