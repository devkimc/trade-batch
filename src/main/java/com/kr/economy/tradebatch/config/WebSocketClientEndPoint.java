package com.kr.economy.tradebatch.config;

import com.kr.economy.tradebatch.trade.application.SocketProcessService;
import jakarta.websocket.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.nio.ByteBuffer;


/**
 * ChatServer Client
 */
@Slf4j
@Component
@ClientEndpoint
public class WebSocketClientEndPoint {

    private final SocketProcessService socketProcessService;

    Session userSession = null;

    @Autowired
    public WebSocketClientEndPoint(SocketProcessService socketProcessService) {
        try {
            this.socketProcessService = socketProcessService;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.setDefaultMaxBinaryMessageBufferSize(65536);
            container.setDefaultMaxTextMessageBufferSize(65536);

            // 실전
//            container.connectToServer(this, new URI("ws://ops.koreainvestment.com:21000"));

            // 모의
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
        log.info("opening websocket");
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
        socketProcessService.processMessage(message);
    }

    @OnMessage
    public void onMessage(ByteBuffer bytes) {
        log.info("bytes = {}", bytes);
    }

    /**
     * Send a message.
     *
     * @param message
     */
    public void sendMessage(String message) {
        this.userSession.getAsyncRemote().sendText(message);
    }
}
