package com.kr.economy.tradebatch.trade.interfaces.rest;

import com.kr.economy.tradebatch.trade.application.KisOauthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/kis/oauth")
@RequiredArgsConstructor
public class KisOauthController {
//
//    private final KisOauthService kisOauthService;
//
//    @PostMapping("/token")
//    public ResponseEntity<Object> oauthToken() {
//        return new ResponseEntity<>(kisOauthService.oauthToken(), HttpStatus.OK);
//    }
//
//    @PostMapping("/socket")
//    public ResponseEntity<Object> oauthSocket() {
//
//        return new ResponseEntity<>(kisOauthService.oauthSocket(), HttpStatus.OK);
//    }
}
