package com.kr.economy.tradebatch.trade.infrastructure.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OauthSocketResDto {

    @JsonProperty("approval_key")
    private String approvalKey;
}
