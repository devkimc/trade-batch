package com.kr.economy.tradebatch.trade.application.commandservices;

import com.kr.economy.tradebatch.common.util.KisUtil;
import com.kr.economy.tradebatch.trade.application.queryservices.KisAccountQueryService;
import com.kr.economy.tradebatch.trade.domain.constants.KisOrderDvsnCode;
import com.kr.economy.tradebatch.trade.domain.constants.OrderDvsnCode;
import com.kr.economy.tradebatch.trade.domain.constants.OrderStatus;
import com.kr.economy.tradebatch.trade.domain.model.aggregates.Order;
import com.kr.economy.tradebatch.trade.infrastructure.repositories.OrderRepository;
import com.kr.economy.tradebatch.trade.infrastructure.rest.DomesticStockOrderClient;
import com.kr.economy.tradebatch.trade.infrastructure.rest.dto.OrderInCashReqDto;
import com.kr.economy.tradebatch.trade.infrastructure.rest.dto.OrderInCashResDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.kr.economy.tradebatch.common.constants.KisStaticValues.*;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class OrderCommandService {

    @Value("${credential.kis.trade.app-key}")
    private String appKey;

    @Value("${credential.kis.trade.secret-key}")
    private String secretKey;

    @Value("${credential.kis.trade.account-no}")
    private String accountNo;

    @Value("${spring.profiles.active}")
    private String activeProfile;

    private final DomesticStockOrderClient domesticStockOrderClient;
    private final KisAccountQueryService kisAccountQueryService;
    private final OrderRepository orderRepository;

    /**
     * 매수 매도 주문
     * @param accountId
     * @param ticker
     * @param orderDvsnCode
     * @param kisOrderDvsnCode
     * @param quotedPrice
     */
    public void order(String accountId,
                      String ticker,
                      OrderDvsnCode orderDvsnCode,
                      KisOrderDvsnCode kisOrderDvsnCode,
                      int quotedPrice) {
        String orderDvsnName = OrderDvsnCode.BUY.equals(orderDvsnCode) ? "매수" : "매도";
        String trId = "";

        if (OrderDvsnCode.BUY.equals(orderDvsnCode)) {
            if ("dev".equals(activeProfile) || "prod".equals(activeProfile)) {
                trId = TR_ID_TTTC0802U;
            } else {
                trId = TR_ID_VTTC0802U;
            }
        } else {
            if ("dev".equals(activeProfile) || "prod".equals(activeProfile)) {
                trId = TR_ID_TTTC0801U;
            } else {
                trId = TR_ID_VTTC0801U;
            }
        }

        String accessToken = kisAccountQueryService.getKisAccount(accountId).getAccessToken();

        Order order = Order.builder()
                .accountId(accountId)
                .ticker(ticker)
                .orderStatus(OrderStatus.REQUEST)
                .orderDvsnCode(orderDvsnCode)
                .quotedPrice(quotedPrice)
                .orderPrice(0)
                .orderQty(10)
                .kisOrderDvsnCode(kisOrderDvsnCode)
                .build();
        orderRepository.save(order);

        OrderInCashReqDto orderInCashReqDto = OrderInCashReqDto.builder()
                .cano(KisUtil.getCano(accountNo))
                .acntPrdtCd(KisUtil.getAcntPrdtCd(accountNo))
                .pdno(ticker)
                .ordDvsn(kisOrderDvsnCode.getCode())
                .ordQty("10")
                .ordUnpr("0")  // 시장가일 경우 0
                .build();

        // 한투 주문 요청
        OrderInCashResDto orderInCashResDto = domesticStockOrderClient.orderInCash(
                "application/json",
                "Bearer " + accessToken,
                appKey,
                secretKey,
                trId,
                "P",
                orderInCashReqDto
        );

        if (orderInCashResDto == null || orderInCashResDto.getOutput() == null || !"0".equals(orderInCashResDto.getResultCode())) {
            throw new RuntimeException("[한투 주문 실패] 응답: " + orderInCashResDto);
        }

        log.info("[{} 주문] 결과: {}", orderDvsnName, orderInCashResDto);

        order.changeKisOrderNo(orderInCashResDto.getOutput().getOdno());
        orderRepository.save(order);
    }

    /**
     * 주문 내역 초기화
     */
    public void deleteHistory() {
        orderRepository.deleteAll();
        log.info("[트레이딩 봇] - 주문 내역 초기화 완료");
    }
}
