package com.kr.economy.tradebatch.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
@Slf4j
public class WebSocketConfig implements WebSocketConfigurer {

    @Value("${endpoint.kis.trade.socket.host}")
    private String socketUrl;

    private final MonitoringHandler monitoringHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {

        WebSocketClient webSocketClient = new StandardWebSocketClient();

        WebSocketConnectionManager cm = new WebSocketConnectionManager(
                webSocketClient, monitoringHandler, socketUrl);

        cm.setAutoStartup(true);
        cm.start();
    }

    /**
     * buffer 사이즈를 증가시키기 위해 추가
     * @return
     */
    @Bean
    public ServletServerContainerFactoryBean createServletServerContainerFactoryBean() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(32768);
        container.setMaxBinaryMessageBufferSize(32768);
        log.info("Websocket factory returned");
        return container;
    }
}
