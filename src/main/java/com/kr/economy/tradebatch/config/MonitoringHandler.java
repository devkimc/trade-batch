//package com.kr.economy.tradebatch.config;
//
//import com.kr.economy.tradebatch.trade.application.KisQuoteService;
//import com.kr.economy.tradebatch.trade.domain.constants.SocketTrType;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//import org.springframework.util.StringUtils;
//import org.springframework.web.socket.*;
//
//import java.io.IOException;
//
//import static com.kr.economy.tradebatch.common.constants.KisStaticValues.TR_ID_H0STCNI0;
//import static com.kr.economy.tradebatch.common.constants.KisStaticValues.TR_ID_H0STCNT0;
//import static com.kr.economy.tradebatch.trade.domain.constants.SocketTrType.TRADING_PRICE;
//import static com.kr.economy.tradebatch.trade.domain.constants.SocketTrType.TRADING_RESULT;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class MonitoringHandler implements WebSocketHandler {
//
//    private WebSocketSession session;
//    private final KisQuoteService kisQuoteService;
//
//    @Override
//    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
//        log.info("[Socket 연결 성공] session: {}", session);
//        this.session = session;
//
//        sendMessage();
//    }
//
//    @Override
//    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
//        try {
//            String response = getResponseBySocketMessage(message);
//
//            log.info("response" + response);
//            String[] result = response.split("\\|");
//
//            if (result.length < 4) return;
//
//            String trId = result[1];
//
//            if (TRADING_RESULT.getId().equals(trId)) {
//                log.info(TRADING_RESULT.getDesc());
//            } else if (TRADING_PRICE.getId().equals(trId)) {
//                log.info(TRADING_PRICE.getDesc());
//            }
//
//            String[] quoteData = result[3].split("\\^");
//        } catch (RuntimeException re) {
//            log.error("[Socket 응답값 객체 파싱 실패]: {}, {}", re.toString());
//        }
//    }
//
//    @Override
//    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
//        log.info("[Socket 연결 에러] session: {}, exception: {}", session, exception);
//    }
//
//    @Override
//    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
//        log.info("[Socket 연결 종료] session: {}, closeStatus: {}", session, closeStatus);
//    }
//
//    @Override
//    public boolean supportsPartialMessages() {
//        return false;
//    }
//
//    /**
//     * 실시간 조회 전송
//     */
//    public void sendMessage() {
//
//        // 1. 실시간 체결가 조회
//        try {
//            session.sendMessage(new TextMessage(kisQuoteService.getRealTimeInfo(TR_ID_H0STCNT0)));
//        } catch (IOException e) {
//            throw new RuntimeException("[실시간 조회 실패 - 체결가]" + e);
//        }
//
//        // 2. 실시간 체결 통보
//        try {
//            session.sendMessage(new TextMessage(kisQuoteService.getRealTimeInfo(TR_ID_H0STCNI0)));
//        } catch (IOException e) {
//            throw new RuntimeException("[실시간 조회 실패 - 체결통보]" + e);
//        }
//    }
//
//    /**
//     * 소켓 메시지로부터 문자열 응답값을 추출
//      * @param message
//     * @return
//     */
//    private String getResponseBySocketMessage(WebSocketMessage<?> message) {
//        String response = "";
//
//        if (message == null) {
//            log.warn("message 미존재");
//            return response;
//        }
//
//        if (message.getPayload() == null) {
//            log.warn("message.getPayload() 미존재");
//            return response;
//        }
//
//        response = message.getPayload().toString();
//
//        if (!StringUtils.hasLength(response)) {
//            throw new RuntimeException(message.toString());
//        }
//
//        return response;
//    }
//}
