package com.kr.economy.tradebatch.trade.infrastructure.rest.dto;

import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrderInCashResDto {
    private String rt_cd;           // 성공 실패 여부
                                        // 0 : 성공
                                        // 0 이외의 값 : 실패
    private String msg_cd;          // 응답 코드
    private String msg1;            // 메시지
    private OutPut output;    // 응답 상세

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class OutPut {
        private String KRX_FWDG_ORD_ORGNO;  // 한국거래소 전송주문 조직번호
        private String ODNO;                // 주문 번호
        private String ORD_TMD;             // 주문 시각 (HHMMSS)
    }
}
