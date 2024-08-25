package com.kr.economy.tradebatch.config;

import com.kr.economy.tradebatch.trade.application.CreateTradingHistoryCommand;
import com.kr.economy.tradebatch.trade.application.KisQuoteService;
import com.kr.economy.tradebatch.trade.application.commandservices.BidAskBalanceCommandService;
import com.kr.economy.tradebatch.trade.application.commandservices.SharePriceHistoryCommandService;
import com.kr.economy.tradebatch.trade.application.commandservices.TradingHistoryCommandService;
import com.kr.economy.tradebatch.trade.application.queryservices.KoreaStockOrderQueryService;
import com.kr.economy.tradebatch.trade.application.queryservices.TradingHistoryQueryService;
import com.kr.economy.tradebatch.trade.domain.model.aggregates.TradingHistory;
import jakarta.websocket.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketMessage;

import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Optional;

import static com.kr.economy.tradebatch.common.constants.KisStaticValues.TICKER_SAMSUNG;

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
    private SharePriceHistoryCommandService sharePriceHistoryCommandService;
    private BidAskBalanceCommandService bidAskBalanceCommandService;
    private TradingHistoryQueryService tradingHistoryQueryService;
    private TradingHistoryCommandService tradingHistoryCommandService;
    private KoreaStockOrderQueryService koreaStockOrderQueryService;

    public WebsocketClientEndpoint() {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.setDefaultMaxBinaryMessageBufferSize(65536);
            container.setDefaultMaxTextMessageBufferSize(65536);
            container.connectToServer(this, new URI("ws://ops.koreainvestment.com:21000"));
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
        String responseByMessage = getResponseBySocketMessage(message);

        if (responseByMessage.length() < 2) return;
        String[] resultBody = responseByMessage.split("\\|");

        if (resultBody.length < 4) return;
        String[] result = resultBody[3].split("\\^");

        System.out.println("message = " + message);

        String tradingTime = result[1];
        int sharePrice = Integer.parseInt(result[2]);
        float bidAskBalanceRatio = Float.parseFloat(result[37]) / Float.parseFloat(result[36]);

        // 실시간 현재가 저장
        sharePriceHistoryCommandService.createSharePriceHistory(TICKER_SAMSUNG, sharePrice, tradingTime);

        // 실시간 매수매도 잔량비 저장
        bidAskBalanceCommandService.createBidAskBalanceRatioHistory(TICKER_SAMSUNG, bidAskBalanceRatio);

        // 당일 마지막 체결 내역 조회
        Optional<TradingHistory> lastTradingHistory = tradingHistoryQueryService.getLastHistoryOfToday(TICKER_SAMSUNG);

        // 마지막 체결 내역이 매수일 경우에만 매도
        if (lastTradingHistory.isPresent() && lastTradingHistory.get().isBuyTrade()) {
            if (lastTradingHistory.get().isSellSignal(sharePrice)) {
                CreateTradingHistoryCommand createTradingHistoryCommand = CreateTradingHistoryCommand.builder()
                        .ticker(TICKER_SAMSUNG)
                        .orderDvsnCode("01")
                        .tradingPrice(sharePrice - 100)
                        .tradingQty(1)
                        .tradingResultType("0")
                        .kisOrderDvsnCode("00")
                        .kisId("") // TODO 제거하기
                        .tradingTime("") // TODO 제거하기
                        .build();
                tradingHistoryCommandService.createTradingHistory(createTradingHistoryCommand);
                System.out.println("********************************************* " + tradingTime + " : " + (sharePrice - 100) + " 매도 체결 ********************************************* ");
            }
        } else {
            if (koreaStockOrderQueryService.getBuySignal(TICKER_SAMSUNG)) {
                CreateTradingHistoryCommand createTradingHistoryCommand = CreateTradingHistoryCommand.builder()
                        .ticker(TICKER_SAMSUNG)
                        .orderDvsnCode("02")
                        .tradingPrice(sharePrice + 100)
                        .tradingQty(1)
                        .tradingResultType("0")
                        .kisOrderDvsnCode("00")
                        .kisId("") // TODO 제거하기
                        .tradingTime("") // TODO 제거하기
                        .build();
                tradingHistoryCommandService.createTradingHistory(createTradingHistoryCommand);
                System.out.println("********************************************* " + tradingTime + " : " + (sharePrice + 100) + " 매수 체결 ********************************************* ");
            }
        }
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
    private String getResponseBySocketMessage(String message) {
        String response = "";

        if (message == null || !StringUtils.hasLength(message)) {
            log.warn("[Socket Response] 메시지 미존재 message : " + message);
        }

        return response;
    }
}
