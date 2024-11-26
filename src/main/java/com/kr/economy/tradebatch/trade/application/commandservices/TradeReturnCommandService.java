package com.kr.economy.tradebatch.trade.application.commandservices;

import com.kr.economy.tradebatch.common.util.DateUtil;
import com.kr.economy.tradebatch.trade.domain.model.commands.CalculateTradeReturnCommand;
import com.kr.economy.tradebatch.trade.domain.constants.OrderDvsnCode;
import com.kr.economy.tradebatch.trade.domain.model.aggregates.TradeReturn;
import com.kr.economy.tradebatch.trade.domain.model.valueObject.TradeReturnId;
import com.kr.economy.tradebatch.trade.infrastructure.repositories.TradeReturnRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Transactional
@Service
@Slf4j
@RequiredArgsConstructor
public class TradeReturnCommandService {

    private final TradeReturnRepository tradeReturnRepository;

    public void calculateTradeReturn(CalculateTradeReturnCommand calculateTradeReturnCommand) {
        TradeReturnId tradeReturnId = TradeReturnId.builder()
                .accountId(calculateTradeReturnCommand.getAccountId())
                .ticker(calculateTradeReturnCommand.getTicker())
                .tradeDate(DateUtil.toNonHyphenDay(LocalDateTime.now()))
                .build();

        TradeReturn tradeReturn = tradeReturnRepository.findById(tradeReturnId)
                .orElseGet(
                        () -> TradeReturn.builder()
                                .tradeReturnId(tradeReturnId)
                                .totalBuyPrice(0)
                                .totalSellPrice(0)
                                .build()
                );

        tradeReturn.addTradePrice(
                OrderDvsnCode.find(calculateTradeReturnCommand.getOrderDvsnCode()),
                calculateTradeReturnCommand.getTradingPrice());

        tradeReturnRepository.save(tradeReturn);
    }
}
