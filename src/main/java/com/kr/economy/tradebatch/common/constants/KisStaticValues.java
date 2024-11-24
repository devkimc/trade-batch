package com.kr.economy.tradebatch.common.constants;

public class KisStaticValues {

    public static final String TEST_ID = "DEVKIMC";

    // 거래 ID: [실전/모의투자] 실시간 주식 체결가
    public static final String TR_ID_H0STCNT0 = "H0STCNT0";

    // 거래 ID: [실전투자] 실시간 주식 체결통보
    public static final String TR_ID_H0STCNI0 = "H0STCNI0";

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
//    public static final String TICKER_SAMSUNG_ELECTRONICS = "005930";
//    public static final String TICKER_SK_HYNIX = "000660";
//    public static final String TICKER_KODEX200_FUTURES_INVERSE_2X = "252670";   // KODEX 200선물인버스2X
//    public static final String TICKER_TIGER_USA_SP500 = "360750";               // TIGER 미국S&P500
//    public static final String TICKER_SDI = "006400";

    public static final String TICKER_HD_CAR = "005380";                                // 현대 차
    public static final String TICKER_SAMSUNG_ELECTRONICS_PREFERRED = "005935";         // 삼성 전자 우
    public static final String TICKER_HD_MOBIS = "012330";                              // 현대 모비스
    public static final String TICKER_HD_HEAVY_INDUSTRY = "329180";                     // 현대 중공업
    public static final String TICKER_KOREA_ELECTRIC_POWER = "015760";                  // 한국 전력
    public static final String TICKER_DOOSAN_ENERBILITY = "034020";                     // 두산 에너빌리티
    public static final String TICKER_KAKAO_BANK = "323410";                            // 카카오 뱅크
    public static final String TICKER_KOREAN_AIR = "003490";                            // 대한 항공
    public static final String TICKER_HYBE = "352820";                                  // 하이브
    public static final String TICKER_LIG_NEX1 = "079550";                              // LIG 넥스원
    public static final String TICKER_HANWHA_SYSTEMS = "272210";                        // 한화 시스템
    public static final String TICKER_NC_SOFT = "036570";                               // 엔씨 소프트

    // 실시간 주가 조회 종목 목록
    public static final String[] STOCK_QUOTE_REQUEST_TICKER_LIST = {
            TICKER_HD_CAR,
            TICKER_SAMSUNG_ELECTRONICS_PREFERRED,
            TICKER_HD_MOBIS,
            TICKER_HD_HEAVY_INDUSTRY,
            TICKER_KOREA_ELECTRIC_POWER,
            TICKER_DOOSAN_ENERBILITY,
            TICKER_KAKAO_BANK,
            TICKER_KOREAN_AIR,
            TICKER_HYBE,
            TICKER_LIG_NEX1,
            TICKER_HANWHA_SYSTEMS,
            TICKER_NC_SOFT
    };

    /* 고객 타입 */
    public static final String CUST_TYPE_PERSONAL = "P";

    /* 거래 타입 */
    public static final String TRADE_TYPE_REGISTRATION = "1";

    /* 실시간 체결 통보 응답 - 주문 접수 */
    public static final String TRADE_RES_CODE_ORDER_TRANSMISSION = "1";

    /* 실시간 체결 통보 응답 - 체결 완료 */
    public static final String TRADE_RES_CODE_TRADE_COMPLETION = "2";
}
