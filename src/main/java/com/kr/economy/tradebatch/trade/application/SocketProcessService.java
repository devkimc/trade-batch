package com.kr.economy.tradebatch.trade.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kr.economy.tradebatch.config.SocketResultDto;
import com.kr.economy.tradebatch.trade.application.commandservices.*;
import com.kr.economy.tradebatch.trade.application.queryservices.*;
import com.kr.economy.tradebatch.trade.domain.constants.KisOrderDvsnCode;
import com.kr.economy.tradebatch.trade.domain.constants.OrderDvsnCode;
import com.kr.economy.tradebatch.trade.domain.model.aggregates.KisAccount;
import com.kr.economy.tradebatch.trade.domain.model.aggregates.Order;
import com.kr.economy.tradebatch.trade.domain.model.commands.CreateTradingHistoryProcessCommand;
import com.kr.economy.tradebatch.util.AES256;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.kr.economy.tradebatch.common.constants.KisStaticValues.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SocketProcessService {

    private final StockQuotesCommandService stockQuotesCommandService;
    private final KisAccountQueryService kisAccountQueryService;
    private final ObjectMapper objectMapper;
    private final OrderCommandService orderCommandService;
    private final OrderQueryService orderQueryService;
    private final KisAccountCommandService kisAccountCommandService;
    private final StockQuotesQueryService stockQuotesQueryService;
    private final TradingHistoryCommandService tradingHistoryCommandService;

    @Value("${spring.profiles.active}")
    private String activeProfile;

    // TODO 최종적으로 WebSocketClientEndPoint 로 이동하는 것으로 변경 검토
    public void processMessage(String message) {

        try {
            if (message == null || message.length() < 2) {
                log.warn("[Socket response] 메세지의 길이가 2자 미만입니다. message : " + message);
                return;
            }

            String[] resultBody = message.split("\\|");
            if (resultBody.length < 4) {
                SocketResultDto socketResultDto = objectMapper.readValue(message, SocketResultDto.class);

                if (socketResultDto == null) {
                    log.info("[Socket response] socketResultDto 미존재 : " + message);
                    return;
                }

                SocketResultDto.Body body = socketResultDto.getBody();

                if (body == null) {
                    SocketResultDto.Header header = socketResultDto.getHeader();

                    if (header != null && "PINGPONG".equals(header.getTr_id())) {
                        if (!"local".equals(activeProfile)) {
                            log.info("[Socket response] PINGPONG");
                        }
                    } else {
                        log.info("[Socket response] body 값 미존재 socketResultDto : {}", socketResultDto);
                    }
                    return;
                }

                // 복호화 정보 변경
                kisAccountCommandService.changeDecryptInfo(TEST_ID, body.getOutput().getIv(), body.getOutput().getKey());
                return;
            }

            String trId = resultBody[1];

            if (TR_ID_H0STCNT0.equals(trId)) {
                processRealTimeSharePrice(trId, message, resultBody);
//            } else if (TR_ID_H0STCNI0.equals(trId)) {           // 실전
//                tradeResultNoticeProcess(message, resultBody);
            } else if (TR_ID_H0STCNI9.equals(trId)) {           // 모의
                tradeResultNoticeProcess(trId, message, resultBody);
            } else {
                log.error("[Socket response] 존재하지 않는 tr_id : {}, message: {}", trId, message);
            }
        } catch (JsonProcessingException jpe) {
            log.error("[Socket response Json 파싱 에러] exception : {}, message: {}", jpe, jpe.getMessage());
        } catch (RuntimeException re) {
            log.error("[Socket response Json 파싱 Runtime 에러] exception : {}, message: {}", re, re.getMessage());
        }
    }

    // TODO 현재가 쿼리 서비스로 이동하는 것 검토
    private void processRealTimeSharePrice(String trId, String message, String[] resultBody) {
        
        String[] result = resultBody[3].split("\\^");
        if (result.length < 38) {
            log.info("[Socket response] message : " + message);
            return;
        }

        try {
            String ticker = result[0];
            String tradingTime = result[1];
            int quotedPrice = Integer.parseInt(result[2]);
            float bidAskBalanceRatio = Float.parseFloat(result[37]) / Float.parseFloat(result[36]);

            // 실시간 현재가 저장
            stockQuotesCommandService.createStockQuote(ticker, quotedPrice, bidAskBalanceRatio, tradingTime);

            Optional<Order> optLastOrder = orderQueryService.getLastOrder(TEST_ID, ticker);

            // 미체결 주문이 존재할 경우 주문하지 않음
            if (optLastOrder.isPresent() && optLastOrder.get().isNotTrading()) {
                return;
            }

            // 마지막 체결 내역이 매수일 경우에만 매도
            if (optLastOrder.isPresent() && optLastOrder.get().isCompletedBuyTrading()) {
                if (stockQuotesQueryService.getSellSignal(ticker, quotedPrice, optLastOrder.get().getOrderPrice(), tradingTime)) {
                    orderCommandService.order(TEST_ID, ticker, OrderDvsnCode.SELL, KisOrderDvsnCode.MARKET_ORDER, quotedPrice);
                }
            } else {
                if (stockQuotesQueryService.getBuySignal(ticker, quotedPrice, tradingTime, TEST_ID, message)) {
                    orderCommandService.order(TEST_ID, ticker, OrderDvsnCode.BUY, KisOrderDvsnCode.MARKET_ORDER, quotedPrice);
                }
            }
        } catch (DataAccessException dae) {
            log.error("[{} 실시간 체결가 수신 DB 에러] exception : {}, message: {}", trId, dae, dae.getMessage());
        } catch (RuntimeException re) {
            log.error("[{} 실시간 체결가 수신 런타임 에러] exception : {}, message: {}", trId, re, re.getMessage());
        }
    }

    /**
     * 실시간 체결 통보 응답 처리
     * @param message
     * @param resultBody
     */
    private void tradeResultNoticeProcess(String trId, String message, String[] resultBody) {

        try {
            // 계정 정보 조회
            KisAccount kisAccount = kisAccountQueryService.getKisAccount(TEST_ID);
            String tradeResult = new AES256().decrypt(resultBody[3], kisAccount.getSocketDecryptKey(), kisAccount.getSocketDecryptIv());

            String[] result = tradeResult.split("\\^");
            if (result.length < 15) {
                log.info("[Socket response] 실시간 체결 통보 응답값의 길이가 짧습니다. tradeResult : " + tradeResult);
                return;
            }

            log.info("[실시간 체결 통보 응답] : {}", tradeResult);
            String kisId = result[0];

            // 앞자리에 붙은 0 제거
            String kisOrderId = String.valueOf(Integer.parseInt(result[2]));
            String kisOrOrderId = result[3];
            String orderDvsnCode = result[4];
            String kisOrderDvsnCode = result[6];
            String ticker = result[8];
            String tradingQty = result[9];
            String tradingPrice = result[10];
            String tradingTime = result[11];
            String tradeResultCode = result[13];

            CreateTradingHistoryProcessCommand createTradingHistoryProcessCommand = CreateTradingHistoryProcessCommand.builder()
                    .ticker(ticker)
                    .orderDvsnCode(orderDvsnCode)
                    .tradingPrice(tradingPrice)
                    .tradingQty(tradingQty)
                    .kisOrderDvsnCode(kisOrderDvsnCode)
                    .tradingTime(tradingTime)
                    .kisId(kisId)
                    .kisOrderId(kisOrderId)
                    .kisOrOrderId(kisOrOrderId)
                    .tradeResultCode(tradeResultCode)
                    .tradeResult(tradeResult)
                    .accountId(kisAccount.getAccountId())
                    .build();

            // 체결 내역 저장
            tradingHistoryCommandService.createTradingHistoryProcess(createTradingHistoryProcessCommand);
        } catch (DataAccessException dae) {
            log.error("[{} 실시간 체결통보 수신 DB 에러] exception : {}, message: {}", trId, dae, message);
        } catch (RuntimeException re) {
            log.error("[{} 실시간 체결통보 수신 런타임 에러] exception : {}, message: {}", trId, re, message);
        }
    }
}
