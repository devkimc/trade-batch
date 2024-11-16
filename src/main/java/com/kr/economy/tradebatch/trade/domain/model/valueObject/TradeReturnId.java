package com.kr.economy.tradebatch.trade.domain.model.valueObject;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class TradeReturnId implements Serializable {

    @Column(nullable = false)
    private String accountId;               // 트레이딩 봇 ID

    @Column(nullable = false)
    private String ticker;                  // 종목 코드

    @Column(nullable = false)
    private String tradeDate;               // 트레이딩 봇 ID
}
