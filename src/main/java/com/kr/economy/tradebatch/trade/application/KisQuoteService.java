package com.kr.economy.tradebatch.trade.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class KisQuoteService {

    private final String TEST_ID = "DEVKIMC";

    @Value("${credential.kis.trade.app-key}")
    private String appKey;

    @Value("${credential.kis.trade.secret-key}")
    private String secretKey;

    private final ObjectMapper objectMapper;
    private final KisOauthService kisOauthService;
    private final KisAccountRepository kisAccountRepository;

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

        String socketKey;

        Optional<KisAccount> optionalAccount = kisAccountRepository.findById(accountId);

        if (optionalAccount.isPresent() && !optionalAccount.get().isRetired()) {
            KisAccount account = optionalAccount.get();
            log.info("[Socket key 발급 - 발급 불필요] 발급 시간: {}, 만료 시간: {}", account.getModDate(), account.getExpirationTime());

            socketKey = account.getSocketKey();
        } else {
            if (optionalAccount.isEmpty()) {
                log.info("[Socket key 발급 - 첫 발급]");
            } else {
                KisAccount account = optionalAccount.get();

                log.info("[Socket key 발급 - 재 발급] 발급 시간: {}, 만료 시간: {}", account.getModDate(), account.getExpirationTime());
            }

            // 소켓키 발급
            socketKey = kisOauthService.oauthSocket().getApproval_key();
        }

        return socketKey;
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
     * 구매 여부 조회
     * @param data
     * @return
     */
    public boolean chkChance(String[] data) {
        // 매도량
        int askpFirstAmount = Integer.parseInt(data[23]);
        int askpSecondAmount = Integer.parseInt(data[24]);
        int askpThirdAmount = Integer.parseInt(data[25]);
        int askpFourthAmount = Integer.parseInt(data[26]);
        int askpFifthAmount = Integer.parseInt(data[27]);
        int askpSumAmount = askpFirstAmount + askpSecondAmount + askpThirdAmount + askpFourthAmount + askpFifthAmount;

        // 매수량
        int bidpFirst = Integer.parseInt(data[13]);
        int bidpFirstAmount = Integer.parseInt(data[33]);
        int bidpSecondAmount = Integer.parseInt(data[34]);
        int bidpThirdAmount = Integer.parseInt(data[35]);
        int bidpFourthAmount = Integer.parseInt(data[36]);
        int bidpFifthAmount = Integer.parseInt(data[37]);
        int bidpSumAmount = bidpFirstAmount + bidpSecondAmount + bidpThirdAmount + bidpFourthAmount + bidpFifthAmount;

        boolean isChance = bidpSumAmount > askpSumAmount;

        if (isChance) {
            log.info("[구매 여부 판단 - 상승 예정] 현재가: {}, {} > {}", bidpFirst, bidpSumAmount, askpSumAmount);
        } else {
            log.info("[구매 여부 판단 - 하락 예정] 현재가: {}, {} < {}", bidpFirst, bidpSumAmount, askpSumAmount);
        }

        return isChance;
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


    private String mapToStr(HashMap<String, Object> map) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String tempStr = mapper.writeValueAsString(map);
        return tempStr;
    }
}
