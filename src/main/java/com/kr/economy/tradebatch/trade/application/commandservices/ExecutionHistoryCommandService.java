package com.kr.economy.tradebatch.trade.application.commandservices;

import com.kr.economy.tradebatch.trade.domain.model.aggregates.ExecutionHistory;
import com.kr.economy.tradebatch.trade.domain.repositories.ExecutionHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ExecutionHistoryCommandService {
    private final ExecutionHistoryRepository executionHistoryRepository;

    public void createExecutionHistory(String ticker, Float sharePrice) {
        Optional<ExecutionHistory> optionalLastExecution = executionHistoryRepository.findTopByTickerOrderByCreatedDateDesc(ticker);

        optionalLastExecution.ifPresentOrElse(
                h -> {
                    ExecutionHistory executionHistory = ExecutionHistory.builder()
                            .ticker(ticker)
                            .sharePrice(sharePrice)
                            .priceTrendType(h.getNextPriceTrendType(sharePrice))
                            .build();
                    executionHistoryRepository.save(executionHistory);
                }, () ->
                {
                    ExecutionHistory initialExecutionHistory = new ExecutionHistory(ticker, sharePrice);
                    executionHistoryRepository.save(initialExecutionHistory);
                }
        );
    }
}
