package com.kr.economy.tradebatch.trade.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kr.economy.tradebatch.config.SocketResultDto;
import com.kr.economy.tradebatch.trade.application.commandservices.OrderCommandService;
import com.kr.economy.tradebatch.trade.application.commandservices.SharePriceHistoryCommandService;
import com.kr.economy.tradebatch.trade.application.commandservices.TradingHistoryCommandService;
import com.kr.economy.tradebatch.trade.application.queryservices.KisAccountQueryService;
import com.kr.economy.tradebatch.trade.application.queryservices.KoreaStockOrderQueryService;
import com.kr.economy.tradebatch.trade.application.queryservices.OrderQueryService;
import com.kr.economy.tradebatch.trade.application.queryservices.TradingHistoryQueryService;
import com.kr.economy.tradebatch.trade.domain.constants.KisOrderDvsnCode;
import com.kr.economy.tradebatch.trade.domain.constants.OrderDvsnCode;
import com.kr.economy.tradebatch.trade.domain.constants.OrderStatus;
import com.kr.economy.tradebatch.trade.domain.model.aggregates.KisAccount;
import com.kr.economy.tradebatch.trade.domain.model.aggregates.Order;
import com.kr.economy.tradebatch.trade.domain.model.aggregates.TradingHistory;
import com.kr.economy.tradebatch.trade.domain.repositories.KisAccountRepository;
import com.kr.economy.tradebatch.trade.domain.repositories.OrderRepository;
import com.kr.economy.tradebatch.util.AES256;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

import static com.kr.economy.tradebatch.common.constants.KisStaticValues.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SocketProcessService {

    private final SharePriceHistoryCommandService sharePriceHistoryCommandService;
    private final TradingHistoryCommandService tradingHistoryCommandService;
    private final TradingHistoryQueryService tradingHistoryQueryService;
    private final KoreaStockOrderQueryService koreaStockOrderQueryService;
    private final KisAccountQueryService kisAccountQueryService;
    private final KisAccountRepository kisAccountRepository;
    private final ObjectMapper objectMapper;
    private final OrderCommandService orderCommandService;
    private final OrderQueryService orderQueryService;
    private final OrderRepository orderRepository;

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
                        log.info("[Socket response] PINGPONG");
                    } else {
                        log.info("[Socket response] body 값 미존재 socketResultDto : {}", socketResultDto);
                    }
                    return;
                }
                SocketResultDto.OutPut output = body.getOutput();

                if (!StringUtils.hasText(output.getIv()) || !StringUtils.hasText(output.getKey())) {
                    log.info("[Socket response] 복호화 값 미존재 iv : {}, key : {}", output.getIv(), output.getIv());
                    return;
                }

                KisAccount kisAccount = kisAccountQueryService.getKisAccount(TEST_ID);
                log.info("[트레이딩 봇] - 복호화 값 저장 전 사용자 정보 : {}", kisAccount);

                kisAccount.updateSocketDecryptKey(output.getIv(), output.getKey());
                kisAccountRepository.save(kisAccount);
                log.info("[Socket response] 복호화 값 저장 성공 iv : {}, key : {}", kisAccount.getSocketDecryptIv(), kisAccount.getSocketDecryptKey());
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
            log.error("[Socket response Json 파싱 에러] message : {}", message);
        } catch (RuntimeException re) {
            log.error("[Socket response Json 파싱 Runtime 에러] exception : {}, message: {}", re, re.getMessage());
            log.error("[Socket response Json 파싱 Runtime 에러] message : {}", message);
        }
    }

    private void processRealTimeSharePrice(String trId, String message, String[] resultBody) {
        
        String[] result = resultBody[3].split("\\^");
        if (result.length < 38) {
            log.info("[Socket response] message : " + message);
            return;
        }

        try {
            String ticker = result[0];
            String tradingTime = result[1];
            int sharePrice = Integer.parseInt(result[2]);
            float bidAskBalanceRatio = Float.parseFloat(result[37]) / Float.parseFloat(result[36]);

            // 실시간 현재가 저장
            sharePriceHistoryCommandService.createSharePriceHistory(ticker, sharePrice, bidAskBalanceRatio, tradingTime);

            // 미체결 주문 내역이 존재할 경우 주문하지 않음
            if (orderQueryService.existsNotTradingOrder(TEST_ID, ticker)) {
                return;
            }

            // 당일 마지막 체결 내역 조회
            Optional<TradingHistory> lastTradingHistory = tradingHistoryQueryService.getLastHistoryOfToday(ticker);

            // 마지막 체결 내역이 매수일 경우에만 매도
            if (lastTradingHistory.isPresent() && lastTradingHistory.get().isBuyTrade()) {
                if (koreaStockOrderQueryService.getSellSignal(ticker, sharePrice, lastTradingHistory.get().getTradingPrice(), tradingTime)) {
                    orderCommandService.order(
                            TEST_ID, ticker, OrderDvsnCode.SELL, KisOrderDvsnCode.MARKET_ORDER, sharePrice);
                }
            } else {
                if (koreaStockOrderQueryService.getBuySignal(ticker, tradingTime)) {
                    orderCommandService.order(
                            TEST_ID, ticker, OrderDvsnCode.BUY, KisOrderDvsnCode.MARKET_ORDER, sharePrice);
                }
            }
        }
        catch (DataAccessException dae) {
            log.error("[{} Socket response DB 에러] exception : {}, message: {}", trId, dae, dae.getMessage());
            log.error("[Socket response DB 에러] message : {}", message);
        }
        catch (RuntimeException re) {
            log.error("[{} Socket response 런타임 에러] exception : {}, message: {}", trId, re, re.getMessage());
            log.error("[Socket response 런타임 에러] message : {}", message);
        } catch (Exception e) {
            log.error("[{} Socket response 미처리 에러] exception : {}, message: {}", trId, e, e.getMessage());
            log.error("[Socket response 미처리 에러] message : {}", message);
        }
    }

    /**
     * 실시간 체결 통보 응답 처리
     * @param message
     * @param resultBody
     */
    private void tradeResultNoticeProcess(String trId, String message, String[] resultBody) {

        try {
            KisAccount kisAccount = kisAccountQueryService.getKisAccount(TEST_ID);
            String tradeResult = new AES256().decrypt(resultBody[3], kisAccount.getSocketDecryptKey(), kisAccount.getSocketDecryptIv());

            String[] result = tradeResult.split("\\^");
            if (result.length < 15) {
                log.info("[Socket response] 실시간 체결 통보 응답값의 길이가 짧습니다. tradeResult : " + tradeResult);
                return;
            }

            log.info("[실시간 체결 통보 응답] : " + tradeResult);
            String kisId = result[0];
            String kisOrderId = result[2];
            String kisOrOrderID = result[3];
            String orderDvsnCode = result[4];
            String kisOrderDvsnCode = result[6];
            String ticker = result[8];
            String tradingQty = result[9];
            String tradingPrice = result[10];
            String tradingTime = result[11];
            String refuseCode = result[12];
            String tradeResultCode = result[13];

            Optional<Order> optLastOrder = orderQueryService.getLastOrder(TEST_ID, ticker);

            if (optLastOrder.isEmpty()) {
                log.info("[주문 내역 조회 실패] accountId: {}, ticker: {}", TEST_ID, ticker);
                throw new RuntimeException("[주문 내역 조회 실패] 주문 정보 존재하지 않음 : " + tradeResult);
            }

            Order lastOrder = optLastOrder.get();

            if (lastOrder.isTrading()) {
                throw new RuntimeException("[주문 내역 조회 실패] 미체결 주문 정보 존재하지 않음 - 마지막 주문 정보 : " + lastOrder);
            }

            if (tradeResultCode.equals(TRADE_RES_CODE_ORDER_TRANSMISSION)) {
                CreateTradingHistoryCommand createTradingHistoryCommand = CreateTradingHistoryCommand.builder()
                        .ticker(ticker)
                        .orderDvsnCode(orderDvsnCode)
                        .tradingPrice(Integer.parseInt(tradingPrice))
                        .tradingQty(Integer.parseInt(tradingQty))
//                    .tradingResultType(tradingResultType)
                        .kisOrderDvsnCode(kisOrderDvsnCode)
                        .kisId(kisId)
                        .tradingTime(tradingTime)
                        .kisOrderId(kisOrderId)
                        .kisOrOrderId(kisOrOrderID)
                        .build();
                tradingHistoryCommandService.createTradingHistory(createTradingHistoryCommand);

                lastOrder.updateOrderStatus(OrderStatus.ORDER_SUCCESS);
                orderRepository.save(lastOrder);
            } else if (tradeResultCode.equals(TRADE_RES_CODE_TRADE_COMPLETION)) {
                tradingHistoryQueryService.getTradingHistory(ticker)
                        .orElseThrow(() -> new RuntimeException("[체결 내역 조회 실패] 주문 정보 존재하지 않음 : " + tradeResult));

                lastOrder.updateOrderStatus(OrderStatus.TRADE_SUCCESS);
                orderRepository.save(lastOrder);
            }
//            String tradingResultType = "0".equals(refuseCode) && "2".equals(tradeResultCode) ? "0" : "1";
        } catch (DataAccessException dae) {
            log.error("[{} Socket response DB 에러] exception : {}, message: {}", trId, dae, dae.getMessage());
            log.error("[Socket response DB 에러] message : {}", message);
        } catch (RuntimeException re) {
            log.error("[{} Socket response 런타임 에러] exception : {}, message: {}", trId, re, re.getMessage());
            log.error("[Socket response 런타임 에러] message : {}", message);
        } catch (Exception e) {
            log.error("[{} Socket response 미처리 에러] exception : {}, message: {}", trId, e, e.getMessage());
            log.error("[Socket response 미처리 에러] message : {}", message);
        }
    }
}
