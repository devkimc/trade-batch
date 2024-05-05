package com.kr.economy.tradebatch.trade.interfaces.rest;

import com.kr.economy.tradebatch.trade.application.KisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/kis")
@RequiredArgsConstructor
public class KisController {

    private final KisService kisService;

    @PostMapping("/oauth/token")
    public ResponseEntity<Object> oauthToken() {
        return new ResponseEntity<>(kisService.oauthToken(), HttpStatus.OK);
    }

    @PostMapping("/oauth/socket")
    public ResponseEntity<Object> oauthSocket() {
        return new ResponseEntity<>(kisService.oauthSocket(), HttpStatus.OK);
    }
}
