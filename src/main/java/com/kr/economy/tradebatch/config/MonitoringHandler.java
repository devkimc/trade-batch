package com.kr.economy.tradebatch.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kr.economy.tradebatch.trade.application.KisQuoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class MonitoringHandler implements WebSocketHandler {

    private WebSocketSession session;
    private final ObjectMapper objectMapper;
    private final KisQuoteService kisQuoteService;
    private final String SOCKET_RESULT_PING_PONG = "PINGPONG";

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("[Socket 연결 성공] session: {}", session);
        this.session = session;

        sendMessage();
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {

        String response = message.getPayload().toString();

        try {
            System.out.println("response = " + response);
            String[] result = response.split("\\|");

            if (result.length >= 4) {
                String[] quoteData = result[3].split("\\^");
                kisQuoteService.chkChance(quoteData);
            }
        } catch (RuntimeException re) {
            log.error(re.toString());
        }

//        SocketResultDto socketResultDto = objectMapper.readValue(response, SocketResultDto.class);
//        String status = socketResultDto.getHeader().getTr_id();

//        if (SOCKET_RESULT_PING_PONG.equals(status)) {
//            // TODO: PONG
//        }
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
     * 실시간 호가 조회 전송
     */
    public void sendMessage() {
        try {
            session.sendMessage(new TextMessage(kisQuoteService.getRealTimeQuote()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
