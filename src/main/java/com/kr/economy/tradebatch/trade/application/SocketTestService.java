package com.kr.economy.tradebatch.trade.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kr.economy.tradebatch.config.WebsocketClientEndpoint;
import com.kr.economy.tradebatch.trade.application.commandservices.BidAskBalanceCommandService;
import com.kr.economy.tradebatch.trade.application.commandservices.SharePriceHistoryCommandService;
import com.kr.economy.tradebatch.trade.application.commandservices.TradingHistoryCommandService;
import com.kr.economy.tradebatch.trade.application.queryservices.KisAccountQueryService;
import com.kr.economy.tradebatch.trade.domain.model.aggregates.KisAccount;
import com.kr.economy.tradebatch.trade.domain.repositories.KisAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.kr.economy.tradebatch.common.constants.KisStaticValues.*;

@Slf4j
@Service
@RequiredArgsConstructor
// TODO: 서비스 명 변경, '실시간 조회 서비스'
public class SocketTestService {

    private final String TEST_ID = "DEVKIMC";

    private final ObjectMapper objectMapper;
    private final KisOauthService kisOauthService;
    private final KisAccountRepository kisAccountRepository;
    private final SharePriceHistoryCommandService sharePriceHistoryCommandService;
    private final BidAskBalanceCommandService bidAskBalanceCommandService;
    private final TradingHistoryCommandService tradingHistoryCommandService;
    private final SocketProcessService socketProcessService;
    private final KisAccountQueryService kisAccountQueryService;

    /**
     * 실시간 정보 조회
     * @return
     */
    public String getRealTimeInfo(String tradeId, String trKey) {
        String jsonRequest;

        try {
            KisAccount kisAccount = kisAccountQueryService.getKisAccount(TEST_ID);

            Map<String, Object> reqMap = getQuoteReqMap(tradeId, kisAccount.getSocketKey(), trKey);
            jsonRequest = objectMapper.writeValueAsString(reqMap);
        } catch (JsonProcessingException e) {
            log.error("[실시간 호가 조회 요청 데이터 생성 에러] {}", e);
            throw new RuntimeException(e);
        }

        log.info("[실시간 호가 조회 요청 데이터 생성] {}", jsonRequest);
        return jsonRequest;
    }

    /**
     * 실시간 조회 요청 데이터 생성
     * @param socketKey
     * @return
     */
    private Map<String, Object> getQuoteReqMap(String tradeId, String socketKey, String trKey) {
        HashMap<String, Object> map = new HashMap<>();
        HashMap<String, String> headerMap = new HashMap<>();
        HashMap<String, String> inputMap = new HashMap<>();
        HashMap<String, Object> bodyMap = new HashMap<>();

        // header 세팅
        headerMap.put("approval_key", socketKey);
        headerMap.put("custtype", CUST_TYPE_PERSONAL);
        headerMap.put("tr_type", TRADE_TYPE_REGISTRATION);
        headerMap.put("content-type", "utf-8");

        // body 세팅
        inputMap.put("tr_id", tradeId);
        inputMap.put("tr_key", trKey);

        bodyMap.put("input", inputMap);

        map.put("header", headerMap);
        map.put("body", bodyMap);
        return map;
    }

    public String test() {
        // https://stackoverflow.com/questions/26452903/javax-websocket-client-simple-example 1번
        try {
            // init history
            sharePriceHistoryCommandService.deleteHistory();
            bidAskBalanceCommandService.deleteHistory();
            tradingHistoryCommandService.deleteHistory();
            log.info("내역 데이터 초기화 완료");

            // open websocket
            final WebsocketClientEndpoint clientEndPoint = new WebsocketClientEndpoint(socketProcessService);

            // send message to websocket 체결가
            clientEndPoint.sendMessage(getRealTimeInfo(TR_ID_H0STCNT0, TICKER_SAMSUNG));

            // wait 5 seconds for messages from websocket
            Thread.sleep(5000);

            // send message to websocket 체결 통보
            clientEndPoint.sendMessage(getRealTimeInfo(TR_ID_H0STCNI0, "@0573181"));
        } catch (InterruptedException ex) {
            System.err.println("InterruptedException exception: " + ex.getMessage());
        } catch (RuntimeException re) {
            System.err.println("RuntimeException exception: " + re.getMessage());
        }

        return "";
    }
}
