package com.kr.economy.tradebatch.trade.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kr.economy.tradebatch.config.WebsocketClientEndpoint;
import com.kr.economy.tradebatch.trade.domain.model.aggregates.KisAccount;
import com.kr.economy.tradebatch.trade.domain.repositories.KisAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.kr.economy.tradebatch.common.constants.KisStaticValues.*;

@Slf4j
@Service
@RequiredArgsConstructor
// TODO: 서비스 명 변경, '실시간 조회 서비스'
public class SocketTestService {

    private final String TEST_ID = "DEVKIMC";

    @Value("${credential.kis.trade.app-key}")
    private String appKey;

    @Value("${credential.kis.trade.secret-key}")
    private String secretKey;

    @Value("${endpoint.kis.trade.socket.host}")
    private String socketUrl;

    private final ObjectMapper objectMapper;
    private final KisOauthService kisOauthService;
    private final KisAccountRepository kisAccountRepository;
    private final KisQuoteService kisQuoteService;

//    @Value("${endpoint.kis.trade.socket.host}")
//    private String socketHost;
//
//    @Value("${endpoint.kis.trade.socket.port}")
//    private int socketPort;

    /**
     * 소켓키 발급
     *      1. 소켓키 발급 내역이 존재하지 않은 경우
     *      2. 소켓키가 만료되지 않은 경우
     * @param accountId
     * @return socketKey
     */
    public String issueSocketKey(String accountId) {
        Optional<KisAccount> optionalAccount = kisAccountRepository.findById(accountId);

        if (optionalAccount.isEmpty()) {
            log.info("[Socket key 발급 - 첫 발급]");
        } else {
            KisAccount account = optionalAccount.get();
            log.info("[Socket key 발급 - 재발급] 발급 시간: {}, 만료 시간: {}", account.getModDate(), account.getExpirationTime());
        }

        // 소켓키 발급
        return kisOauthService.oauthSocket().getApproval_key();
    }

    /**
     * 실시간 정보 조회
     * @return
     */
    public String getRealTimeInfo(String tradeId) {
        String jsonRequest;

        try {
            String socketKey = issueSocketKey(TEST_ID);

            Map<String, Object> reqMap = getQuoteReqMap(tradeId, socketKey);
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
    private Map<String, Object> getQuoteReqMap(String tradeId, String socketKey) {
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
        inputMap.put("tr_key", TICKER_SAMSUNG);

        bodyMap.put("input", inputMap);

        map.put("header", headerMap);
        map.put("body", bodyMap);
        return map;
    }

    public String test() {
        // https://stackoverflow.com/questions/26452903/javax-websocket-client-simple-example 1번
        try {
            // open websocket
            final WebsocketClientEndpoint clientEndPoint = new WebsocketClientEndpoint();

            // send message to websocket
            clientEndPoint.sendMessage(getRealTimeInfo(TR_ID_H0STCNT0));

            // wait 5 seconds for messages from websocket
            Thread.sleep(5000);

        } catch (InterruptedException ex) {
            System.err.println("InterruptedException exception: " + ex.getMessage());
        }

        return "";
    }
}
