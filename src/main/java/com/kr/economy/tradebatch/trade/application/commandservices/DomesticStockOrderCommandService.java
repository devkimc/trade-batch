package com.kr.economy.tradebatch.trade.application.commandservices;

import com.kr.economy.tradebatch.trade.domain.constants.KisOrderDvsnCode;
import com.kr.economy.tradebatch.trade.domain.constants.OrderDvsnCode;
import com.kr.economy.tradebatch.trade.domain.model.aggregates.Order;
import com.kr.economy.tradebatch.trade.domain.repositories.OrderRepository;
import com.kr.economy.tradebatch.trade.infrastructure.rest.DomesticStockOrderClient;
import com.kr.economy.tradebatch.trade.infrastructure.rest.dto.OrderInCashReqDto;
import com.kr.economy.tradebatch.trade.infrastructure.rest.dto.OrderInCashResDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class DomesticStockOrderCommandService {

    private final OrderRepository orderRepository;
    private final DomesticStockOrderClient domesticStockOrderClient;

    // TODO 트랜잭션 전파 확인, feign client 요청 시 유지할지, 주문 상태 업데이트할 떄 유지할 지
    // TODO 파라미터 dto 로 변경
    public Order orderInCash(String accountId,
                            String authorization,
                            String ticker,
                            String trId,
                            OrderDvsnCode orderDvsnCode,
                            Float sharePrice,
                            Float orderPrice,
                            Float qty,
                            KisOrderDvsnCode kisOrderDvsnCode
                            ) {
        Order order = Order.builder()
                .accountId(accountId)
                .ticker(ticker)
                .orderDvsnCode(orderDvsnCode)
                .sharePrice(sharePrice)
                .orderPrice(orderPrice)
                .qty(qty)
                .kisOrderDvsnCode(kisOrderDvsnCode)
                .build();

        // 1. 주문 정보 등록
        // TODO 예외처리
        Order registeredOrder = orderRepository.save(order);

        OrderInCashReqDto orderInCashReqDto = OrderInCashReqDto.builder()
                .CANO("계좌번호")
                .ACNT_PRDT_CD("계좌상품코드")
                .PDNO(ticker)
                .ORD_DVSN(kisOrderDvsnCode.getCode())
                .ORD_QTY(String.valueOf(qty))
                .ORD_UNPR(String.valueOf(orderPrice))
                .build();

        // 2. 한투 주문 요청
        // TODO 실패 시 로그 기록, 예외처리
        OrderInCashResDto orderInCashResDto = domesticStockOrderClient.orderInCash(
                "application/json; charset=utf-8",
                authorization,
                "appkey",
                "appSecret",
                trId,
                orderInCashReqDto
        );

        registeredOrder.updateOrderResult(orderInCashResDto.getOutput().get(0).getODNO(),
                orderInCashResDto.getRt_cd(),
                orderInCashResDto.getMsg());

        // 3. 주문 정보 수정
        return orderRepository.save(registeredOrder);
    }
}
