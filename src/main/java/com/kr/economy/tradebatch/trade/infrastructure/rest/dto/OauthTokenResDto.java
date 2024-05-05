package com.kr.economy.tradebatch.trade.infrastructure.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OauthTokenResDto {
    private String approval_key;
    private String token_type;
    private String expires_in;
    private String access_token_token_expired;
}
