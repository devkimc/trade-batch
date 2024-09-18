package com.kr.economy.tradebatch.config;

import feign.Logger;
import feign.RequestInterceptor;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;

import static com.kr.economy.tradebatch.common.constants.ResponseCode.SERVER_ERROR;
import static com.kr.economy.tradebatch.common.constants.StaticValues.HEADER_CONTENT_TYPE;


@Slf4j
@Configuration
public class OpenFeignConfig {

    @Value("${feign.client.config.default.retry-period}")
    private int retryPeriod;

    @Value("${feign.client.config.default.retry-max-period}")
    private int retryMaxPeriod;

    @Value("${feign.client.config.default.max-attempts}")
    private int maxAttempts;

    /**
     * Debug log level
     */
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    /**
     * Custom Header 및 공통 헤더 설정
     */
    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            System.out.println("requestTemplate = " + requestTemplate);
        };
    }

    @Bean
    public Retryer retryer() {
        return new Retryer.Default(retryPeriod, retryMaxPeriod, maxAttempts);
    }
}
