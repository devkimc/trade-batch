package com.kr.economy.tradebatch.trade.application.commandservices;

import com.kr.economy.tradebatch.trade.application.CreateTradingHistoryCommand;
import com.kr.economy.tradebatch.trade.domain.constants.KisOrderDvsnCode;
import com.kr.economy.tradebatch.trade.domain.constants.OrderDvsnCode;
import com.kr.economy.tradebatch.trade.domain.constants.TradingResultType;
import com.kr.economy.tradebatch.trade.domain.model.aggregates.TradingHistory;
import com.kr.economy.tradebatch.trade.domain.repositories.TradingHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@Slf4j
@RequiredArgsConstructor
public class TradingHistoryCommandService {

    private final TradingHistoryRepository tradingHistoryRepository;

    public void createTradingHistory(CreateTradingHistoryCommand createTradingHistoryCommand) {
        TradingHistory tradingHistory = TradingHistory
                .builder()
                .ticker(createTradingHistoryCommand.getTicker())
                .orderDvsnCode(OrderDvsnCode.find(createTradingHistoryCommand.getOrderDvsnCode()))
                .tradingPrice(createTradingHistoryCommand.getTradingPrice())
                .tradingQty(createTradingHistoryCommand.getTradingQty())
                .tradingResultType(TradingResultType.find(createTradingHistoryCommand.getTradingResultType()))
                .kisOrderDvsnCode(KisOrderDvsnCode.find(createTradingHistoryCommand.getKisOrderDvsnCode()))
                .tradingTime(createTradingHistoryCommand.getTradingTime())
                .kisId(createTradingHistoryCommand.getKisId())
                .kisOrderId(createTradingHistoryCommand.getKisOrderId())
                .kisOrOrderId(createTradingHistoryCommand.getKisOrOrderId())
                .build();
        tradingHistoryRepository.save(tradingHistory);
    }
}
