package com.kr.economy.tradebatch.config;

import com.kr.economy.tradebatch.trade.application.KisQuoteService;
import jakarta.websocket.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketMessage;

import java.net.URI;
import java.nio.ByteBuffer;

/**
 * ChatServer Client
 */
@Slf4j
@Component
@ClientEndpoint
public class WebsocketClientEndpoint {

    @Value("${endpoint.kis.trade.socket.host}")
    private String socketUrl;

    Session userSession = null;
    private KisQuoteService kisQuoteService;

    public WebsocketClientEndpoint() {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.setDefaultMaxBinaryMessageBufferSize(65536);
            container.setDefaultMaxTextMessageBufferSize(65536);
            container.connectToServer(this, new URI("ws://ops.koreainvestment.com:31000"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Callback hook for Connection open events.
     *
     * @param userSession the userSession which is opened.
     */
    @OnOpen
    public void onOpen(Session userSession) {
        System.out.println("opening websocket");
        this.userSession = userSession;
    }

    /**
     * Callback hook for Connection close events.
     *
     * @param userSession the userSession which is getting closed.
     * @param reason the reason for connection close
     */
    @OnClose
    public void onClose(Session userSession, CloseReason reason) {
        log.info("[Socket 연결 종료] session: {}, closeStatus: {}", userSession, reason);
        this.userSession = null;
    }

    /**
     * Callback hook for Message Events. This method will be invoked when a client send a message.
     *
     * @param message The text message
     */
    @OnMessage
    public void onMessage(String message) {
        System.out.println("message = " + message);
    }

    @OnMessage
    public void onMessage(ByteBuffer bytes) {
        System.out.println("bytes = " + bytes);
    }

    /**
     * Send a message.
     *
     * @param message
     */
    public void sendMessage(String message) {
        this.userSession.getAsyncRemote().sendText(message);
    }

    /**
     * 소켓 메시지로부터 문자열 응답값을 추출
     * @param message
     * @return
     */
    private String getResponseBySocketMessage(WebSocketMessage<?> message) {
        String response = "";

        if (message == null) {
            log.warn("message 미존재");
            return response;
        }

        if (message.getPayload() == null) {
            log.warn("message.getPayload() 미존재");
            return response;
        }

        response = message.getPayload().toString();

        if (!StringUtils.hasLength(response)) {
            throw new RuntimeException(message.toString());
        }

        return response;
    }
}
