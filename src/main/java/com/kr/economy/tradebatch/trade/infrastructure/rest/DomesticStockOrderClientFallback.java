package com.kr.economy.tradebatch.trade.infrastructure.rest;

import com.kr.economy.tradebatch.trade.infrastructure.rest.dto.OrderInCashReqDto;
import com.kr.economy.tradebatch.trade.infrastructure.rest.dto.OrderInCashResDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class DomesticStockOrderClientFallback implements DomesticStockOrderClient{

    // TODO Feign Hystrix Support 설정 필요한지 확인
    @Override
    public OrderInCashResDto orderInCash(String contentType,
                                         String authorization,
                                         String appKey,
                                         String appSecret,
                                         String trId,
                                         String custType,
                                         OrderInCashReqDto orderInCashReqDto) {

        log.error("[DomesticStockOrderClient 에러] fallback called - contentType: {}, " +
                        "authorization: {}, " +
                        "appKey: {}, " +
                        "appSecret: {}, " +
                        "trId: {}, " +
                        "orderInCashReqDto: {}",
                contentType,
                appKey,
                appSecret,
                trId,
                orderInCashReqDto);

        OrderInCashResDto.OutPut defaultOutput = OrderInCashResDto.OutPut
                .builder()
                .ODNO("")
                .build();

        OrderInCashResDto orderInCashResDto = OrderInCashResDto.builder()
                .output(List.of(defaultOutput))
                .rt_cd("-1")
                .msg1("[DomesticStockOrderClient 에러] fallback called")
                .build();

        return orderInCashResDto;
    }
}
