package com.kr.economy.tradebatch.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kr.economy.tradebatch.trade.domain.aggregate.KisAccount;
import com.kr.economy.tradebatch.trade.domain.repositories.KisAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.kr.economy.tradebatch.common.constants.KisStaticValues.*;
import static com.kr.economy.tradebatch.common.constants.KisStaticValues.TICKER_SAMSUNG;

@Component
@RequiredArgsConstructor
public class MonitoringHandler implements WebSocketHandler {

    private List<WebSocketSession> sessionList = new ArrayList<>();

    private final ObjectMapper objectMapper;

    private final String TEST_ID = "DEVKIMC";
    private final KisAccountRepository kisAccountRepository;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("session = " + session);
        sessionList.add(session);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {

        String test = message.getPayload().toString();
        System.out.println("test = " + test);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.out.println("TestHandler.handleTransportError");
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        System.out.println("TestHandler.afterConnectionClosed");
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    public void sendMessage(String message) {
        try {
            WebSocketSession lastSession = sessionList.get(sessionList.size() - 1);
            System.out.println("lastSession = " + lastSession);

            lastSession.sendMessage(new TextMessage(message));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
