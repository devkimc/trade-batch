package com.kr.economy.tradebatch.trade.infrastructure.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrderInCashResDto {
    @JsonProperty("rt_cd")
    private String resultCode;      // 성공 실패 여부
                                    // 0 : 성공
                                    // 0 이외의 값 : 실패
    @JsonProperty("msg_cd")
    private String msgCode;         // 응답 코드
    @JsonProperty("msg1")
    private String msg;             // 메시지
    @JsonProperty("output")
    private OutPut output;          // 응답 상세

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class OutPut {
        @JsonProperty("KRX_FWDG_ORD_ORGNO")
        private String krxFwdgOrdOrgno;  // 한국거래소 전송주문 조직번호
        @JsonProperty("ODNO")
        private String odno;                // 주문 번호
        @JsonProperty("ORD_TMD")
        private String ordTmd;             // 주문 시각 (HHMMSS)
    }
}
