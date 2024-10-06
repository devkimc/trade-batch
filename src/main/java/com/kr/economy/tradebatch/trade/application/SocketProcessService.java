package com.kr.economy.tradebatch.trade.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kr.economy.tradebatch.config.SocketResultDto;
import com.kr.economy.tradebatch.trade.application.commandservices.OrderCommandService;
import com.kr.economy.tradebatch.trade.application.commandservices.SharePriceHistoryCommandService;
import com.kr.economy.tradebatch.trade.application.commandservices.TradingHistoryCommandService;
import com.kr.economy.tradebatch.trade.application.queryservices.KisAccountQueryService;
import com.kr.economy.tradebatch.trade.application.queryservices.KoreaStockOrderQueryService;
import com.kr.economy.tradebatch.trade.application.queryservices.TradingHistoryQueryService;
import com.kr.economy.tradebatch.trade.domain.model.aggregates.KisAccount;
import com.kr.economy.tradebatch.trade.domain.model.aggregates.TradingHistory;
import com.kr.economy.tradebatch.trade.domain.repositories.KisAccountRepository;
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

                System.out.println("socketResultDto = " + socketResultDto);
                SocketResultDto.Body body = socketResultDto.getBody();

                if (body == null) {
                    log.info("[Socket response] body 값 미존재 socketResultDto : {}", socketResultDto);
                    return;
                }
                SocketResultDto.OutPut output = body.getOutput();

                if (!StringUtils.hasText(output.getIv()) || !StringUtils.hasText(output.getKey())) {
                    log.info("[Socket response] 복호화 값 미존재 iv : {}, key : {}", output.getIv(), output.getIv());
                    return;
                }

                KisAccount kisAccount = kisAccountQueryService.getKisAccount("DEVKIMC");

                kisAccount.updateSocketDecryptKey(output.getIv(), output.getKey());
                kisAccountRepository.save(kisAccount);
                log.info("[Socket response] 복호화 값 저장 성공 iv : {}, key : {}", kisAccount.getSocketDecryptIv(), kisAccount.getSocketDecryptKey());
                return;
            }

            String trId = resultBody[1];

            if (TR_ID_H0STCNT0.equals(trId)) {
                processRealTimeSharePrice(message, resultBody);
//            } else if (TR_ID_H0STCNI0.equals(trId)) {           // 실전
//                tradeResultNoticeProcess(message, resultBody);
            } else if (TR_ID_H0STCNI9.equals(trId)) {           // 모의
                tradeResultNoticeProcess(message, resultBody);
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

    private void processRealTimeSharePrice(String message, String[] resultBody) {
        
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

            // 당일 마지막 체결 내역 조회
            Optional<TradingHistory> optionalTradingHistory = tradingHistoryQueryService.getLastHistoryOfToday(ticker);

            // 마지막 체결 내역이 매수일 경우에만 매도
            if (optionalTradingHistory.isPresent() && optionalTradingHistory.get().isBuyTrade()) {
                TradingHistory lastTradingHistory = optionalTradingHistory.get();

                if (koreaStockOrderQueryService.getSellSignal(ticker, sharePrice, lastTradingHistory.getTradingPrice(), tradingTime)) {
                    orderCommandService.order(ticker, "S");
                }
            } else {
                if (koreaStockOrderQueryService.getBuySignal(ticker, tradingTime)) {
                    orderCommandService.order(ticker, "B");
                }
            }
        } catch (DataAccessException dae) {
            log.error("[Socket response DB 에러] exception : {}, message: {}", dae, dae.getMessage());
            log.error("[Socket response DB 에러] message : {}", message);
        } catch (RuntimeException re) {
            log.error("[Socket response 런타임 에러] exception : {}, message: {}", re, re.getMessage());
            log.error("[Socket response 런타임 에러] message : {}", message);
        }
    }

    private void tradeResultNoticeProcess(String message, String[] resultBody) {

        try {
            KisAccount kisAccount = kisAccountQueryService.getKisAccount("DEVKIMC");
            String decryptedMessage = new AES256().decrypt(resultBody[3], kisAccount.getSocketDecryptKey(), kisAccount.getSocketDecryptIv());

            String[] result = decryptedMessage.split("\\^");
            if (result.length < 15) {
                log.info("[Socket response] 실시간 체결 통보 응답값의 길이가 짧습니다. decryptedMessage : " + decryptedMessage);
                return;
            }

            log.info("[Socket response temporary check] decryptedMessage : " + decryptedMessage);
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
            String tradingResultType = "0".equals(refuseCode) && "2".equals(tradeResultCode) ? "0" : "1";

            CreateTradingHistoryCommand createTradingHistoryCommand = CreateTradingHistoryCommand.builder()
                    .ticker(ticker)
                    .orderDvsnCode(orderDvsnCode)
                    .tradingPrice(Integer.parseInt(tradingPrice))
                    .tradingQty(Integer.parseInt(tradingQty))
                    .tradingResultType(tradingResultType)
                    .kisOrderDvsnCode(kisOrderDvsnCode)
                    .kisId(kisId)
                    .tradingTime(tradingTime)
                    .kisOrderId(kisOrderId)
                    .kisOrOrderId(kisOrOrderID)
                    .build();
            tradingHistoryCommandService.createTradingHistory(createTradingHistoryCommand);
        } catch (DataAccessException dae) {
            log.error("[Socket response DB 에러] exception : {}, message: {}" , dae, dae.getMessage());
            log.error("[Socket response DB 에러] message : " + message);
        } catch (RuntimeException re) {
            log.error("[Socket response 런타임 에러] exception : {}, message: {}" , re, re.getMessage());
            log.error("[Socket response 런타임 에러] message : {}" , message);
        } catch (Exception e) {
            log.error("[Socket response 런타임 에러] exception : {}, message: {}" , e, e.getMessage());
            log.error("[Socket response 런타임 에러] message : {}" , message);
        }
    }
}
