package com.kr.economy.tradebatch.trade.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONWrappedObject;
import com.kr.economy.tradebatch.config.MonitoringHandler;
import com.kr.economy.tradebatch.trade.domain.aggregate.KisAccount;
import com.kr.economy.tradebatch.trade.domain.repositories.KisAccountRepository;
import com.kr.economy.tradebatch.trade.infrastructure.rest.dto.GetRealTimeQuoteReqDto;
import com.kr.economy.tradebatch.trade.infrastructure.rest.dto.GetRealTimeQuoteResDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import static com.kr.economy.tradebatch.common.constants.KisStaticValues.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class KisQuoteService {

    private final String TEST_ID = "DEVKIMC";

    @Value("${credential.kis.trade.app-key}")
    private String appKey;

    @Value("${credential.kis.trade.secret-key}")
    private String secretKey;

    private final ObjectMapper objectMapper;
    private final KisAccountRepository kisAccountRepository;
    private final MonitoringHandler monitoringHandler;

//    @Value("${endpoint.kis.trade.socket.host}")
//    private String socketHost;
//
//    @Value("${endpoint.kis.trade.socket.port}")
//    private int socketPort;

    public GetRealTimeQuoteResDto getRealTimeQuoteResDto() {

        KisAccount account = kisAccountRepository.findById(TEST_ID).orElseThrow(() -> new RuntimeException("존재하지 않는 ID: " + TEST_ID));

        HashMap<String, Object> map = new HashMap<>();
        HashMap<String, String> headerMap = new HashMap<>();
        HashMap<String, String> inputMap = new HashMap<>();
        HashMap<String, Object> bodyMap = new HashMap<>();

        // header 세팅
        headerMap.put("approval_key", account.getSocketKey());
        headerMap.put("custtype", CUST_TYPE_PERSONAL);
        headerMap.put("tr_type", TRADE_TYPE_REGISTRATION);
        headerMap.put("content-type", "utf-8");

        // body 세팅
        inputMap.put("tr_id", TR_ID_H0STASP0);
        inputMap.put("tr_key", TICKER_SAMSUNG);

        bodyMap.put("input", inputMap);

        map.put("header", headerMap);
        map.put("body", bodyMap);

        String jsonRequest = null;

        try {
            jsonRequest = objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        monitoringHandler.sendMessage(jsonRequest);

//        GetRealTimeQuoteReqDto getRealTimeQuoteReqDto = GetRealTimeQuoteReqDto.builder()
//                .tr_id(TR_ID_H0STASP0)
//                .tr_key(TICKER_SAMSUNG)
//                .build();
//        log.info("[실시간 호가 조회] {}", getRealTimeQuoteReqDto);
//
//        KisAccount account = kisAccountRepository.findById(TEST_ID).orElseThrow(() -> new RuntimeException("존재하지 않는 ID: " + TEST_ID));
//
//        HashMap<String, Object> map = new HashMap<>();
//
//        HashMap<String, String> headerMap = new HashMap<>();
//
//        HashMap<String, String> inputMap = new HashMap<>();
//        HashMap<String, Object> bodyMap = new HashMap<>();
//
//
//        // header 세팅
//        headerMap.put("approval_key", account.getSocketKey());
//        headerMap.put("custtype", CUST_TYPE_PERSONAL);
//        headerMap.put("tr_type", TRADE_TYPE_REGISTRATION);
//        headerMap.put("content-type", "utf-8");
//
//        // body 세팅
//        inputMap.put("tr_id", TR_ID_H0STASP0);
//        inputMap.put("tr_key", TICKER_SAMSUNG);
//
//        bodyMap.put("input", inputMap);
//
//        map.put("header", headerMap);
//        map.put("body", bodyMap);
//
//        try {
//            String s = mapToStr(map);
//            testSocket(s);
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }

        log.info("[실시간 호가 조회] 종료");

//        GetRealTimeQuoteResDto realTimeQuote = kisQuoteClient.getRealTimeQuote(
//                account.getSocketKey(),
//                CUST_TYPE_PERSONAL,
//                TRADE_TYPE_REGISTRATION,
//                getRealTimeQuoteReqDto
//        );
//        log.info("[실시간 호가 조회] 결과 {}", realTimeQuote);

        return null;
    }

    private void testSocket(String message) {
        Socket socket = null;

        try {
            socket = new Socket();
            log.info("[Socket request]");

//            socket.connect(new InetSocketAddress("ops.koreainvestment.com", socketPort));
            log.info("[Socket connect] {}", socket);

            byte[] bytes = null;

            // Socket에서 가져온 출력스트림
            OutputStream os = socket.getOutputStream();
//            DataOutputStream dos = new DataOutputStream(os);
//            bytes = message.getBytes("UTF-8");
//            dos.writeInt(bytes.length);
//            dos.write(bytes, 0, bytes.length);
//            dos.flush();

            // send bytes
            PrintWriter pw = new PrintWriter(os);

            log.info("[Socket Data Send Success] {}", message);

            // Socket 에서 가져온 입력스트림
            InputStream is = socket.getInputStream();
            int read = is.read();

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String s = reader.readLine();
            System.out.println("s = " + s);


            // read int
//            int receiveLength = dis.readInt();
//
//            // receive bytes
//            if (receiveLength > 0) {
//                byte receiveByte[] = new byte[receiveLength];
//                dis.readFully(receiveByte, 0, receiveLength);
//
//                message = new String(receiveByte);
//                log.info("[Socket Data Receive Success] {}", message);
//            } else {
//                log.info("[Empty Data Receive] message: {}, receiveLength: {}", message, receiveLength);
//            }

            // OutputStream, InputStream 종료
            os.close();
            is.close();

            // Socket 종료
            socket.close();
            log.info("[Socket closed]");
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!socket.isClosed()) {
            try {
                socket.close();
                log.info("[Socket close]");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String mapToStr(HashMap<String, Object> map) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String tempStr = mapper.writeValueAsString(map);
        return tempStr;
    }
}
