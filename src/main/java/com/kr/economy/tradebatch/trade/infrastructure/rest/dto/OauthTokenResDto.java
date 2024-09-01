package com.kr.economy.tradebatch.trade.infrastructure.rest.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OauthTokenResDto {
    private String access_token;
    private String token_type;
    private String expires_in;
    private String access_token_token_expired;
}
