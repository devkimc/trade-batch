package com.kr.economy.tradebatch.trade.infrastructure.rest;

import com.kr.economy.tradebatch.config.OpenFeignConfig;
import com.kr.economy.tradebatch.trade.infrastructure.rest.dto.OauthSocketReqDto;
import com.kr.economy.tradebatch.trade.infrastructure.rest.dto.OauthSocketResDto;
import com.kr.economy.tradebatch.trade.infrastructure.rest.dto.OauthTokenReqDto;
import com.kr.economy.tradebatch.trade.infrastructure.rest.dto.OauthTokenResDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import static com.kr.economy.tradebatch.common.constants.Url.OAUTH_SOCKET_URL;
import static com.kr.economy.tradebatch.common.constants.Url.OAUTH_TOKEN_URL;


@FeignClient(name = "KisOauth", url="${endpoint.kis.trade.host}", configuration = OpenFeignConfig.class)
public interface KisOauthClient {

    @PostMapping(OAUTH_TOKEN_URL)
    OauthTokenResDto oauthToken(@RequestBody OauthTokenReqDto oauthTokenReqDto);

    @PostMapping(OAUTH_SOCKET_URL)
    OauthSocketResDto oauthSocket(@RequestBody OauthSocketReqDto oauthSocketReqDto);
}
