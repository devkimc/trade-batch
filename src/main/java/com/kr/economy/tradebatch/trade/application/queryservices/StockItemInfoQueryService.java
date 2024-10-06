package com.kr.economy.tradebatch.trade.application.queryservices;

import com.kr.economy.tradebatch.trade.domain.model.aggregates.StockItemInfo;
import com.kr.economy.tradebatch.trade.domain.repositories.StockItemInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockItemInfoQueryService {

    private final StockItemInfoRepository stockItemInfoRepository;

    public StockItemInfo getStockItemInfo(String ticker) {
        return stockItemInfoRepository.findById(ticker).orElseThrow(() -> new RuntimeException("[주식 종목 정보 조회 실패] 존재하지 않는 종목입니다. 종목 코드 : " + ticker));
    }
}
