package com.kr.economy.tradebatch.trade.application.commandservices;

import com.kr.economy.tradebatch.trade.application.queryservices.OrderQueryService;
import com.kr.economy.tradebatch.trade.application.queryservices.TradingHistoryQueryService;
import com.kr.economy.tradebatch.trade.domain.constants.OrderStatus;
import com.kr.economy.tradebatch.trade.domain.model.aggregates.Order;
import com.kr.economy.tradebatch.trade.domain.model.commands.CalculateTradeReturnCommand;
import com.kr.economy.tradebatch.trade.domain.model.commands.CreateTradingHistoryCommand;
import com.kr.economy.tradebatch.trade.domain.constants.KisOrderDvsnCode;
import com.kr.economy.tradebatch.trade.domain.constants.OrderDvsnCode;
import com.kr.economy.tradebatch.trade.domain.constants.TradingResultType;
import com.kr.economy.tradebatch.trade.domain.model.aggregates.TradingHistory;
import com.kr.economy.tradebatch.trade.domain.model.commands.CreateTradingHistoryProcessCommand;
import com.kr.economy.tradebatch.trade.infrastructure.repositories.OrderRepository;
import com.kr.economy.tradebatch.trade.infrastructure.repositories.TradingHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.Optional;

import static com.kr.economy.tradebatch.common.constants.KisStaticValues.*;

@Transactional
@Service
@Slf4j
@RequiredArgsConstructor
public class TradingHistoryCommandService {

    private final TradingHistoryRepository tradingHistoryRepository;
    private final TradingHistoryQueryService tradingHistoryQueryService;
    private final OrderRepository orderRepository;
    private final TradeReturnCommandService tradeReturnCommandService;
    private final OrderQueryService orderQueryService;

    // TODO 메서드 제거
    public void createTradingHistory(CreateTradingHistoryCommand createTradingHistoryCommand) {
        TradingHistory tradingHistory = TradingHistory
                .builder()
                .ticker(createTradingHistoryCommand.getTicker())
                .orderDvsnCode(OrderDvsnCode.find(createTradingHistoryCommand.getOrderDvsnCode()))
                .tradingPrice(createTradingHistoryCommand.getTradingPrice())
                .tradingQty(createTradingHistoryCommand.getTradingQty())
                .tradingResultType(TradingResultType.find(createTradingHistoryCommand.getTradingResultType()))
                .kisOrderDvsnCode(KisOrderDvsnCode.find(createTradingHistoryCommand.getKisOrderDvsnCode()))
                .tradingTime(createTradingHistoryCommand.getTradingTime())
                .kisId(createTradingHistoryCommand.getKisId())
                .kisOrderId(createTradingHistoryCommand.getKisOrderId())
                .kisOrOrderId(createTradingHistoryCommand.getKisOrOrderId())
                .build();
        tradingHistoryRepository.save(tradingHistory);
    }

    // TODO 메서드명 변경하기 (createTradingHistoryProcess -> createTradingHistory)
    public void createTradingHistoryProcess(CreateTradingHistoryProcessCommand command) {

        try {
            // TODO [다량 주문] 변경 대상 - 마지막 주문이 아닌 주문 번호로 찾아야 함
            Optional<Order> optLastOrder = orderQueryService.getLastOrder(TEST_ID, command.getTicker());

            if (optLastOrder.isEmpty()) {
                log.info("[주문 내역 조회 실패] accountId: {}, ticker: {}", TEST_ID, command.getTicker());
                throw new RuntimeException("[주문 내역 조회 실패] 주문 정보 존재하지 않음 : " + command.getTradeResult());
            }

            Order lastOrder = optLastOrder.get();

            if (lastOrder.isTrading()) {
                throw new RuntimeException("[주문 내역 조회 실패] 미체결 주문 정보 존재하지 않음 - 마지막 주문 정보 : " + lastOrder);
            }

            // TODO [다량 주문] 변경 대상
            // TODO TradingHistoryCommandService 의 별도 서비스로 관리할지 검토 필요
            if (TRADE_RES_CODE_ORDER_TRANSMISSION.equals(command.getTradeResultCode())) {
                CreateTradingHistoryCommand createTradingHistoryCommand = CreateTradingHistoryCommand.builder()
                        .ticker(command.getTicker())
                        .orderDvsnCode(command.getOrderDvsnCode())
                        .tradingPrice(Integer.parseInt(command.getTradingPrice()))
                        .tradingQty(Integer.parseInt(command.getTradingQty()))
                        .kisOrderDvsnCode(command.getKisOrderDvsnCode())
                        .kisId(command.getKisId())
                        .tradingTime(command.getTradingTime())
                        .kisOrderId(command.getKisOrderId())
                        .kisOrOrderId(command.getKisOrOrderId())
                        .build();
                this.createTradingHistory(createTradingHistoryCommand);

                lastOrder.updateOrderStatus(OrderStatus.ORDER_SUCCESS);
                orderRepository.save(lastOrder);

            } else if (TRADE_RES_CODE_COMPLETION.equals(command.getTradeResultCode())) {

                if (ObjectUtils.isEmpty(tradingHistoryQueryService.getTradingHistoryList(command.getTicker()))) {
                    throw new RuntimeException("[실시간 체결 통보] 주문 접수 내역이 존재하지 않습니다.");
                }

                lastOrder.updateOrderPrice(Integer.parseInt(command.getTradingPrice()));
                lastOrder.updateOrderStatus(OrderStatus.TRADE_SUCCESS);
                orderRepository.save(lastOrder);

                CalculateTradeReturnCommand calculateTradeReturnCommand = CalculateTradeReturnCommand.builder()
                        .accountId(command.getAccountId())
                        .ticker(command.getTicker())
                        .orderDvsnCode(command.getOrderDvsnCode())
                        .tradingPrice(Integer.parseInt(command.getTradingPrice()))
                        .tradingQty(Integer.parseInt(command.getTradingQty()))
                        .build();

                tradeReturnCommandService.calculateTradeReturn(calculateTradeReturnCommand);
//                log.info("[실시간 체결 통보] 체결 완료 된 주문 DB 저장 완료 order: {}", updatedOrder);
            }
        } catch (DataAccessException dae) {
            log.error("[체결 내역 저장 DB 에러] exception : {}, ticker: {}", dae, command.getTicker());
        } catch (RuntimeException re) {
            log.error("[체결 내역 저장 런타임 에러] exception : {}, ticker: {}", re, command.getTicker());
        }
    }

    /**
     * 체결 내역 초기화
     */
    public void deleteHistory() {
        tradingHistoryRepository.deleteAll();
        log.info("[트레이딩 봇] - 체결 내역 초기화 완료");
    }
}
