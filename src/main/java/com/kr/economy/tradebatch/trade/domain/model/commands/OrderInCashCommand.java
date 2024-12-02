package com.kr.economy.tradebatch.trade.domain.model.commands;

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
    private Integer quotedPrice;
    private Integer orderPrice;
    private Integer qty;
    private KisOrderDvsnCode kisOrderDvsnCode;
}
