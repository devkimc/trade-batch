package com.kr.economy.tradebatch.trade.application;

import com.kr.economy.tradebatch.trade.domain.constants.KisOrderDvsnCode;
import com.kr.economy.tradebatch.trade.domain.constants.OrderDvsnCode;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class OrderInCashCommand {

    private String accountId;
    private String accountNumber;
    private String authorization;
    private String ticker;
    private String trId;
    private OrderDvsnCode orderDvsnCode;
    private Float sharePrice;
    private Float orderPrice;
    private int qty;
    private KisOrderDvsnCode kisOrderDvsnCode;
}
