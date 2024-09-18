package com.kr.economy.tradebatch.trade.application.commandservices;

import com.kr.economy.tradebatch.common.util.KisUtil;
import com.kr.economy.tradebatch.trade.application.OrderInCashCommand;
import com.kr.economy.tradebatch.trade.domain.model.aggregates.Order;
import com.kr.economy.tradebatch.trade.domain.repositories.OrderRepository;
import com.kr.economy.tradebatch.trade.infrastructure.rest.DomesticStockOrderClient;
import com.kr.economy.tradebatch.trade.infrastructure.rest.dto.OrderInCashReqDto;
import com.kr.economy.tradebatch.trade.infrastructure.rest.dto.OrderInCashResDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class DomesticStockOrderCommandService {

    @Value("${credential.kis.trade.app-key}")
    private String appKey;

    @Value("${credential.kis.trade.secret-key}")
    private String secretKey;

    private final OrderRepository orderRepository;
    private final DomesticStockOrderClient domesticStockOrderClient;

    // TODO 트랜잭션 전파 확인, feign client 요청 시 유지할지, 주문 상태 업데이트할 떄 유지할 지
    public Order orderInCash(OrderInCashCommand orderInCashCommand) {

        Order updatedOrder = null;

        try {
            // 1. 주문 정보 등록
//            Order registeredOrder = orderRepository.save(new Order(orderInCashCommand));

            // 2. 한투 주문 요청
            OrderInCashResDto orderInCashResDto = domesticStockOrderClient.orderInCash(
                    "application/json; charset=utf-8",
                    orderInCashCommand.getAuthorization(),
                    appKey,
                    secretKey,
                    orderInCashCommand.getTrId(),
                    "P",
                    toOrderInCashReqDto(orderInCashCommand)
            );

//            registeredOrder.updateOrderResult(orderInCashResDto);
//
//            // 3. 주문 정보 수정
//            updatedOrder = orderRepository.save(registeredOrder);
        } catch (DataAccessException dae) {
            log.error("[국내 주식 주문 실패] DB 처리 에러 - {}", dae.toString());
        } catch (RuntimeException re) {
            log.error("[국내 주식 주문 실패] - {}", re.toString());
        }

        return updatedOrder;
    }

    // TODO mapper 로 변환하기
    private OrderInCashReqDto toOrderInCashReqDto(OrderInCashCommand orderInCashCommand) {
        return OrderInCashReqDto.builder()
                .cano(KisUtil.getCano(orderInCashCommand.getAccountNumber()))
                .acntPrdtCd(KisUtil.getAcntPrdtCd(orderInCashCommand.getAccountNumber()))
                .pdno(orderInCashCommand.getTicker())
                .ordDvsn(orderInCashCommand.getKisOrderDvsnCode().getCode())
                .ordQty(String.valueOf(orderInCashCommand.getQty()))
                .ordUnpr(String.valueOf(orderInCashCommand.getOrderPrice()))
                .build();
    }
}
