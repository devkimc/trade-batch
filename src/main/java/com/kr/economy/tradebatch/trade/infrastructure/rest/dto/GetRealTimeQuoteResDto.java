package com.kr.economy.tradebatch.trade.infrastructure.rest.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GetRealTimeQuoteResDto {

    private String MKSC_SHRN_ISCD;	        // 유가증권 단축 종목코드	String	Y	9
    private String BSOP_HOUR;	            // 영업 시간	String	Y	6
    private String HOUR_CLS_CODE;	        // 시간 구분 코드	String	Y	1	0 : 장중
    private String ASKP1;	                // 매도호가1	Number	Y	4
    private String ASKP2;	                // 매도호가2	Number	Y	4
    private String ASKP3;	                // 매도호가3	Number	Y	4
    private String ASKP4;	                // 매도호가4	Number	Y	4
    private String ASKP5;	                // 매도호가5	Number	Y	4
    private String ASKP6;	                // 매도호가6	Number	Y	4
    private String ASKP7;	                // 매도호가7	Number	Y	4
    private String ASKP8;	                // 매도호가8	Number	Y	4
    private String ASKP9;	                // 매도호가9	Number	Y	4
    private String ASKP10;	                // 매도호가10	Number	Y	4
    private String BIDP1;	                // 매수호가1	Number	Y	4
    private String BIDP2;	                // 매수호가2	Number	Y	4
    private String BIDP3;	                // 매수호가3	Number	Y	4
    private String BIDP4;	                // 매수호가4	Number	Y	4
    private String BIDP5;	                // 매수호가5	Number	Y	4
    private String BIDP6;	                // 매수호가6	Number	Y	4
    private String BIDP7;	                // 매수호가7	Number	Y	4
    private String BIDP8;	                // 매수호가8	Number	Y	4
    private String BIDP9;	                // 매수호가9	Number	Y	4
    private String BIDP10;	                // 매수호가10	Number	Y	4
    private String ASKP_RSQN1;	            // 매도호가 잔량1	Number	Y	8
    private String ASKP_RSQN2;	            // 매도호가 잔량2	Number	Y	8
    private String ASKP_RSQN3;	            // 매도호가 잔량3	Number	Y	8
    private String ASKP_RSQN4;	            // 매도호가 잔량4	Number	Y	8
    private String ASKP_RSQN5;	            // 매도호가 잔량5	Number	Y	8
    private String ASKP_RSQN6;	            // 매도호가 잔량6	Number	Y	8
    private String ASKP_RSQN7;	            // 매도호가 잔량7	Number	Y	8
    private String ASKP_RSQN8;	            // 매도호가 잔량8	Number	Y	8
    private String ASKP_RSQN9;	            // 매도호가 잔량9	Number	Y	8
    private String ASKP_RSQN10;	            // 매도호가 잔량10	Number	Y	8
    private String BIDP_RSQN1;	            // 매수호가 잔량1	Number	Y	8
    private String BIDP_RSQN2;	            // 매수호가 잔량2	Number	Y	8
    private String BIDP_RSQN3;	            // 매수호가 잔량3	Number	Y	8
    private String BIDP_RSQN4;	            // 매수호가 잔량4	Number	Y	8
    private String BIDP_RSQN5;	            // 매수호가 잔량5	Number	Y	8
    private String BIDP_RSQN6;	            // 매수호가 잔량6	Number	Y	8
    private String BIDP_RSQN7;	            // 매수호가 잔량7	Number	Y	8
    private String BIDP_RSQN8;	            // 매수호가 잔량8	Number	Y	8
    private String BIDP_RSQN9;	            // 매수호가 잔량9	Number	Y	8
    private String BIDP_RSQN10;	            // 매수호가 잔량10	Number	Y	8
    private String TOTAL_ASKP_RSQN;	        // 총 매도호가 잔량	Number	Y	8
    private String TOTAL_BIDP_RSQN;	        // 총 매수호가 잔량	Number	Y	8
    private String OVTM_TOTAL_ASKP_RSQN;    // 시간외 총 매도호가 잔량	Number	Y	8
    private String OVTM_TOTAL_BIDP_RSQN;    // 시간외 총 매수호가 잔량	Number	Y	8
    private String ANTC_CNPR;               // 예상 체결가	Number	Y	4	동시호가 등 특정 조건하에서만 발생
    private String ANTC_CNQN;               // 예상 체결량	Number	Y	8	동시호가 등 특정 조건하에서만 발생
    private String ANTC_VOL;                // 예상 거래량	Number	Y	8	동시호가 등 특정 조건하에서만 발생
    private String ANTC_CNTG_VRSS;          // 예상 체결 대비	Number	Y	4	동시호가 등 특정 조건하에서만 발생
    private String ANTC_CNTG_VRSS_SIGN;     // 예상 체결 대비 부호	String	Y	1	동시호가 등 특정 조건하에서만 발생
    private String ANTC_CNTG_PRDY_CTRT;     // 예상 체결 전일 대비율	Number	Y	8
    private String ACML_VOL;                // 누적 거래량	Number	Y	8
    private String TOTAL_ASKP_RSQN_ICDC;    // 총 매도호가 잔량 증감	Number	Y	4
    private String TOTAL_BIDP_RSQN_ICDC;    // 총 매수호가 잔량 증감	Number	Y	4
    private String OVTM_TOTAL_ASKP_ICDC;    // 시간외 총 매도호가 증감	Number	Y	4
    private String OVTM_TOTAL_BIDP_ICDC;    // 시간외 총 매수호가 증감	Number	Y	4
    private String STCK_DEAL_CLS_CODE;      // 주식 매매 구분 코드	String	Y	2	사용 X (삭제된 값)
}
