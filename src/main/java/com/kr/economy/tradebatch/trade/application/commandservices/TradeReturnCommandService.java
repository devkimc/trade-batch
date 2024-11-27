package com.kr.economy.tradebatch.trade.application.commandservices;

import com.kr.economy.tradebatch.common.util.DateUtil;
import com.kr.economy.tradebatch.trade.domain.model.commands.CalculateTradeReturnCommand;
import com.kr.economy.tradebatch.trade.domain.constants.OrderDvsnCode;
import com.kr.economy.tradebatch.trade.domain.model.aggregates.TradeReturn;
import com.kr.economy.tradebatch.trade.domain.model.valueObject.TradeReturnId;
import com.kr.economy.tradebatch.trade.infrastructure.repositories.TradeReturnRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Transactional
@Service
@Slf4j
@RequiredArgsConstructor
public class TradeReturnCommandService {

    private final TradeReturnRepository tradeReturnRepository;

    /**
     * 수익 계산
     * @param command
     */
    public void calculateTradeReturn(CalculateTradeReturnCommand command) {
        try {
            TradeReturnId tradeReturnId = TradeReturnId.builder()
                    .accountId(command.getAccountId())
                    .ticker(command.getTicker())
                    .tradeDate(DateUtil.toNonHyphenDay(LocalDateTime.now()))
                    .build();

            // 종목별 매수, 매도 금액 조회
            TradeReturn tradeReturn = tradeReturnRepository.findById(tradeReturnId)
                    .orElseGet(
                            () -> TradeReturn.builder()
                                    .tradeReturnId(tradeReturnId)
                                    .totalBuyPrice(0)
                                    .totalSellPrice(0)
                                    .build()
                    );

            // 매수, 매도 금액 증가
            tradeReturn.addTradePrice(
                    OrderDvsnCode.find(command.getOrderDvsnCode()),
                    command.getTradingPrice());

            tradeReturnRepository.save(tradeReturn);
        } catch (DataAccessException dae) {
            log.error("[수익 계산 DB 에러] exception : {}, ticker: {}, tradingPrice: {}", dae, command.getTicker(), command.getTradingPrice());
        } catch (RuntimeException re) {
            log.error("[수익 계산 런타임 에러] exception : {}, ticker: {}, tradingPrice: {}", re, command.getTicker(), command.getTradingPrice());
        }
    }
}
