package com.kr.economy.tradebatch.trade.infrastructure.rest.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OauthTokenReqDto {
    private String grant_type;
    private String appkey;
    private String appsecret;
}
