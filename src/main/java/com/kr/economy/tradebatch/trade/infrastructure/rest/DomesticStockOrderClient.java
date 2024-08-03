package com.kr.economy.tradebatch.trade.infrastructure.rest;

import com.kr.economy.tradebatch.config.OpenFeignConfig;
import com.kr.economy.tradebatch.trade.infrastructure.rest.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import static com.kr.economy.tradebatch.common.constants.StaticValues.*;
import static com.kr.economy.tradebatch.common.constants.Url.*;


@FeignClient(name = "DomesticStockOrder", url="${endpoint.kis.trade.host}", configuration = OpenFeignConfig.class)
public interface DomesticStockOrderClient {

    /**
     * 주식주문(현금)[v1_국내주식-001]
     * @param contentType
     * @param authorization
     * @param appKey
     * @param appSecret
     * @param trId
     * @param orderInCashReqDto
     * @return
     */
    @PostMapping(ORDER_IN_CASH_URL)
    OrderInCashResDto orderInCash(
            @RequestHeader(name = HEADER_CONTENT_TYPE, defaultValue = "application/json; charset=utf-8") String contentType,
            @RequestHeader(HEADER_AUTHORIZATION) String authorization,
            @RequestHeader(HEADER_APP_KEY) String appKey,
            @RequestHeader(HEADER_APP_SECRET) String appSecret,
            @RequestHeader(HEADER_TR_ID) String trId,
            @RequestBody OrderInCashReqDto orderInCashReqDto);
}
