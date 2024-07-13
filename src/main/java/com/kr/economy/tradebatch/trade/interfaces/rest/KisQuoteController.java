package com.kr.economy.tradebatch.trade.interfaces.rest;

import com.kr.economy.tradebatch.trade.application.KisOauthService;
import com.kr.economy.tradebatch.trade.application.KisQuoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/kis/quote")
@RequiredArgsConstructor
public class KisQuoteController {

    private final KisQuoteService kisQuoteService;

//    @PostMapping("/real-time")
//    public ResponseEntity<Object> getRealTimeQuote() {
//        return new ResponseEntity<>(kisQuoteService.getRealTimeQuoteResDto(), HttpStatus.OK);
//    }
}
