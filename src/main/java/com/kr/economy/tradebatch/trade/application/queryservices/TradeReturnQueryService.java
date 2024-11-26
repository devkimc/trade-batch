package com.kr.economy.tradebatch.trade.application.queryservices;

import com.kr.economy.tradebatch.trade.domain.model.aggregates.TradeReturn;
import com.kr.economy.tradebatch.trade.domain.model.valueObject.TradeReturnId;
import com.kr.economy.tradebatch.trade.infrastructure.repositories.TradeReturnRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@Slf4j
@RequiredArgsConstructor
public class TradeReturnQueryService {
    private final TradeReturnRepository tradeReturnRepository;

    public Optional<TradeReturn> getTradeReturn(String accountId, String ticker, String tradeDate) {
        TradeReturnId tradeReturnId = TradeReturnId.builder()
                .accountId(accountId)
                .ticker(ticker)
                .tradeDate(tradeDate)
                .build();
        return tradeReturnRepository.findById(tradeReturnId);
    }

}
