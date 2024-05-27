package com.kr.economy.tradebatch.trade.infrastructure.rest;

import com.kr.economy.tradebatch.config.OpenFeignConfig;
import com.kr.economy.tradebatch.trade.infrastructure.rest.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import static com.kr.economy.tradebatch.common.constants.Url.GET_REAL_TIME_QUOTE_URL;


//@FeignClient(name = "KisQuote", url="${endpoint.kis.trade.web-socket}", configuration = OpenFeignConfig.class)
//public interface KisQuoteClient {
//
//    @PostMapping(GET_REAL_TIME_QUOTE_URL)
//    GetRealTimeQuoteResDto getRealTimeQuote(
//            @RequestHeader String approval_key,
//            @RequestHeader String custtype,
//            @RequestHeader String tr_type,
//            @RequestBody GetRealTimeQuoteReqDto getRealTimeQuoteReqDto);
//}
