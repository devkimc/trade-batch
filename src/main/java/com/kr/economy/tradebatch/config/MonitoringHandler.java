package com.kr.economy.tradebatch.config;

import com.kr.economy.tradebatch.trade.application.KisQuoteService;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.io.IOException;

import static com.kr.economy.tradebatch.common.constants.KisStaticValues.TR_ID_H0STCNT0;

@Slf4j
@Component
@RequiredArgsConstructor
public class MonitoringHandler implements WebSocketHandler {

    private WebSocketSession session;
    private final KisQuoteService kisQuoteService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("[Socket 연결 성공] session: {}", session);
        this.session = session;

        sendMessage();
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        try {
            if (message == null) {
                log.warn("message 미존재");
                return;
            }

            if (message.getPayload() == null) {
                log.warn("message.getPayload() 미존재");
                return;
            }

            String response = message.getPayload().toString();

            if (StringUtils.isEmpty(response)) {
                return;
            }

            log.info("response" + response);
            String[] result = response.split("\\|");

            if (result.length >= 4) {
                String[] quoteData = result[3].split("\\^");
            }
        } catch (RuntimeException re) {
            log.error("[Socket 응답값 객체 파싱 실패]: {}, {}", message, re.toString());
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.info("[Socket 연결 에러] session: {}, exception: {}", session, exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        log.info("[Socket 연결 종료] session: {}, closeStatus: {}", session, closeStatus);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    /**
     * 실시간 조회 전송
     */
    public void sendMessage() {

        // 1. 실시간 체결가 조회
        try {
            session.sendMessage(new TextMessage(kisQuoteService.getRealTimeInfo(TR_ID_H0STCNT0)));
        } catch (IOException e) {
            throw new RuntimeException("[실시간 조회 실패 - 체결가]" + e);
        }

        // 2. 실시간 호가 조회
//        try {
//            session.sendMessage(new TextMessage(kisQuoteService.getRealTimeQuote(TR_ID_H0STASP0)));
//        } catch (IOException e) {
//            throw new RuntimeException("[실시간 조회 실패 - 호가]" + e);
//        }
    }
}
