package com.kr.economy.tradebatch.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kr.economy.tradebatch.trade.domain.model.aggregates.KisAccount;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

import static com.kr.economy.tradebatch.common.constants.KisStaticValues.CUST_TYPE_PERSONAL;
import static com.kr.economy.tradebatch.common.constants.KisStaticValues.TRADE_TYPE_REGISTRATION;

@Slf4j
@RequiredArgsConstructor
public class KisUtil {

    /**
     * 종합계좌번호 조회
     * @param accountNumber
     * @return
     */
    public static String getCano(String accountNumber) {
        if (StringUtils.isEmpty(accountNumber)) {
            throw new RuntimeException("[종합계좌번호 조회 실패] accountNumber null");
        }

        // TODO 예외처리 추가
        String[] splittedAccountNumber = accountNumber.split("-");

        return splittedAccountNumber[0];
    }

    /**
     * 계좌상품코드 조회
     * @param accountNumber
     * @return
     */
    public static String getAcntPrdtCd(String accountNumber) {
        if (StringUtils.isEmpty(accountNumber)) {
            throw new RuntimeException("[계좌상품코드 조회 실패] accountNumber null");
        }

        // TODO 예외처리 추가
        String[] splittedAccountNumber = accountNumber.split("-");

        return splittedAccountNumber[1];
    }

    /**
     * 실시간 조회 요청 데이터 생성
     * @param socketKey
     * @return
     */
    public static Map<String, Object> getRealTimeReqMap(String tradeId, String socketKey, String trKey) {
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

    /**
     * 실시간 정보 조회
     * @return
     */
    public static String getRealTimeReqJson(KisAccount kisAccount, String tradeId, String trKey) {
        String jsonRequest;
        ObjectMapper mapper = new ObjectMapper();

        try {
            Map<String, Object> reqMap = getRealTimeReqMap(tradeId, kisAccount.getSocketKey(), trKey);
            jsonRequest = mapper.writeValueAsString(reqMap);
        } catch (JsonProcessingException e) {
            log.error("[트레이딩 봇] - 실시간 호가 조회 요청 데이터 생성 에러 : {}", e);
            throw new RuntimeException(e);
        }

        log.info("[트레이딩 봇] - 실시간 호가 조회 요청 데이터 생성 : {}", jsonRequest);
        return jsonRequest;
    }
}
