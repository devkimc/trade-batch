package com.kr.economy.tradebatch.trade.application.commandservices;

import com.kr.economy.tradebatch.trade.application.queryservices.OrderQueryService;
import com.kr.economy.tradebatch.trade.application.queryservices.TradingHistoryQueryService;
import com.kr.economy.tradebatch.trade.domain.constants.OrderStatus;
import com.kr.economy.tradebatch.trade.domain.model.aggregates.Order;
import com.kr.economy.tradebatch.trade.domain.model.commands.CalculateTradeReturnCommand;
import com.kr.economy.tradebatch.trade.domain.model.commands.CreateTradingHistoryCommand;
import com.kr.economy.tradebatch.trade.domain.constants.KisOrderDvsnCode;
import com.kr.economy.tradebatch.trade.domain.constants.OrderDvsnCode;
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

import java.util.List;
import java.util.Optional;

import static com.kr.economy.tradebatch.common.constants.KisStaticValues.*;

@Transactional
@Service
@Slf4j
@RequiredArgsConstructor
public class TradingHistoryCommandService {

    private final TradingHistoryQueryService tradingHistoryQueryService;
    private final TradeReturnCommandService tradeReturnCommandService;
    private final OrderQueryService orderQueryService;
    private final TradingHistoryRepository tradingHistoryRepository;
    private final OrderRepository orderRepository;

    // TODO 메서드 제거
    public void createTradingHistory(CreateTradingHistoryCommand createTradingHistoryCommand) {
        TradingHistory tradingHistory = TradingHistory
                .builder()
                .ticker(createTradingHistoryCommand.getTicker())
                .orderDvsnCode(OrderDvsnCode.find(createTradingHistoryCommand.getOrderDvsnCode()))
                .tradingPrice(createTradingHistoryCommand.getTradingPrice())
                .tradingQty(createTradingHistoryCommand.getTradingQty())
                .tradeResultCode(createTradingHistoryCommand.getTradeResultCode())
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
            // 주문 내역 조회
            Optional<Order> optLastOrder = orderQueryService.getOrderByOrderNo(command.getKisOrderId());

            if (optLastOrder.isEmpty()) {
                log.info("[주문 내역 조회 실패] accountId: {}, ticker: {}", TEST_ID, command.getTicker());
                throw new RuntimeException("[주문 내역 조회 실패] 주문 정보 존재하지 않음 : " + command.getTradeResult());
            }

            Order lastOrder = optLastOrder.get();

            if (lastOrder.isTrading()) {
                throw new RuntimeException("[주문 내역 조회 실패] 미체결 주문 정보 존재하지 않음 - 마지막 주문 정보 : " + lastOrder);
            }

            if (TRADE_RES_CODE_ORDER_TRANSMISSION.equals(command.getTradeResultCode())) {   // 주문접수 응답일 경우
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
                        .tradeResultCode(command.getTradeResultCode())
                        .build();

                // 주문접수 상태의 체결 내역 생성
                this.createTradingHistory(createTradingHistoryCommand);

            } else if (TRADE_RES_CODE_COMPLETION.equals(command.getTradeResultCode())) {    // 체결 응답일 경우

                if (ObjectUtils.isEmpty(tradingHistoryQueryService.getTradingHistoryList(command.getTicker()))) {
                    throw new RuntimeException("[실시간 체결 통보] 주문 접수 내역이 존재하지 않습니다.");
                }

                // 주문번호와 일치하는 체결 내역을 모두 조회
                List<TradingHistory> tradingHistoryList = tradingHistoryQueryService.getTradingHistoryList(command.getTicker(), command.getKisOrderId());
                log.info("[체결 내역 조회] - 주문번호: {}, 체결 건수: {}건", command.getKisOrderId(), tradingHistoryList.size());

                // 주문 접수 상태이고, 체결 수량이 일치하는 체결 내역의 상태를 변경한다.
                TradingHistory tradingHistory = tradingHistoryList.stream()
                        .filter(history ->
                                TRADE_RES_CODE_ORDER_TRANSMISSION.equals(history.getTradeResultCode()) &&
                                        Integer.parseInt(command.getTradingQty()) == history.getTradingQty()
                        )
                        .findFirst()
                        .orElseThrow(
                                () -> new RuntimeException("[체결 내역 조회 에러] - 주문 접수 상태의 체결 내역이 존재하지 않습니다. ticker: " + command.getTicker()
                                        + ", orderId: " + command.getKisOrderId() + ", tradingQty: " + command.getTradingQty())
                        );

                // 체결 상태 변경 - 체결 완료 
                tradingHistory.changeTradePrice(Integer.parseInt(command.getTradingPrice()));
                tradingHistory.changeTradeResultCode(TRADE_RES_CODE_COMPLETION);
                TradingHistory completedTradeHistory = tradingHistoryRepository.save(tradingHistory);

                log.info("[체결 완료] - 주문번호: {}, 종목: {}, 가격: {}, 수량: {}"
                        , completedTradeHistory.getKisOrderId(), completedTradeHistory.getTicker()
                        , completedTradeHistory.getTradingPrice(), completedTradeHistory.getTradingQty());

                // 체결수량의 합
                int tradingQtySum = tradingHistoryList.stream()
                        .filter(TradingHistory::isTradeCompleted)
                        .mapToInt(TradingHistory::getTradingQty)
                        .sum();
                log.info("[주문, 체결 수량 비교] - 주문번호: {}, 주문수량: {}, 체결수량 합: {}", lastOrder.getKisOrderNo(), lastOrder.getOrderQty(), tradingQtySum);

                // 주문수량과 체결수량의 합을 비교
                if (lastOrder.getOrderQty() != tradingQtySum) {
                    log.info("[주문, 체결 수량 비교] - 체결되지 않은 주문이 존재합니다. 주문번호: {}, 주문수량: {}, 체결수량 합: {}", lastOrder.getKisOrderNo(), lastOrder.getOrderQty(), tradingQtySum);
                    return;
                }

                // 주문 상태 변경 - 거래 성공
                lastOrder.updateOrderStatus(OrderStatus.TRADE_SUCCESS);
                Order tradedOrder = orderRepository.save(lastOrder);

                log.info("[거래 완료] - 주문번호: {}, 종목: {}, 가격: {}, 수량: {}"
                        , tradedOrder.getKisOrderNo(), tradedOrder.getTicker()
                        , tradedOrder.getOrderPrice(), tradedOrder.getOrderQty());

                CalculateTradeReturnCommand calculateTradeReturnCommand = CalculateTradeReturnCommand.builder()
                        .accountId(command.getAccountId())
                        .ticker(command.getTicker())
                        .orderDvsnCode(command.getOrderDvsnCode())
                        .tradingPrice(Integer.parseInt(command.getTradingPrice()))
                        .tradingQty(Integer.parseInt(command.getTradingQty()))
                        .build();

                // 수익 계산
                tradeReturnCommandService.calculateTradeReturn(calculateTradeReturnCommand);
            }
        } catch (DataAccessException dae) {
            log.error("[체결 내역 저장 DB 에러] exception : {}, ticker: {}, orderId: {}", dae, command.getTicker(), command.getKisOrderId());
        } catch (RuntimeException re) {
            log.error("[체결 내역 저장 런타임 에러] exception : {}, ticker: {}, orderId: {}", re, command.getTicker(), command.getKisOrderId());
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
