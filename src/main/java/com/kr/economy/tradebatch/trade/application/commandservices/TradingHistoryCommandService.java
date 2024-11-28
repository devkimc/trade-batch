package com.kr.economy.tradebatch.trade.application.commandservices;

import com.kr.economy.tradebatch.trade.application.queryservices.OrderQueryService;
import com.kr.economy.tradebatch.trade.application.queryservices.TradingHistoryQueryService;
import com.kr.economy.tradebatch.trade.domain.model.aggregates.Order;
import com.kr.economy.tradebatch.trade.domain.constants.KisOrderDvsnCode;
import com.kr.economy.tradebatch.trade.domain.constants.OrderDvsnCode;
import com.kr.economy.tradebatch.trade.domain.model.aggregates.TradingHistory;
import com.kr.economy.tradebatch.trade.domain.model.commands.CreateTradingHistoryProcessCommand;
import com.kr.economy.tradebatch.trade.infrastructure.repositories.TradingHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.kr.economy.tradebatch.common.constants.KisStaticValues.*;

@Transactional
@Service
@Slf4j
@RequiredArgsConstructor
public class TradingHistoryCommandService {

    private final TradingHistoryQueryService tradingHistoryQueryService;
    private final OrderQueryService orderQueryService;
    private final OrderCommandService orderCommandService;
    private final TradingHistoryRepository tradingHistoryRepository;

    public void tradeBySocketResponse(CreateTradingHistoryProcessCommand command) {

        String kisOrderId = command.getKisOrderId();
        String ticker = command.getTicker();
        int tradingPrice = Integer.parseInt(command.getTradingPrice());
        String tradeResultCode = command.getTradeResultCode();
        
        try {
            // 주문 내역 조회
            Order order = orderQueryService.getOrderByOrderNo(kisOrderId)
                    .orElseThrow(
                            () -> new RuntimeException("[주문 내역 조회 실패] 주문 정보 존재하지 않음 : " + command.getTradeResult())
                    );

            if (order.isTraded()) {
                throw new RuntimeException("[주문 내역 조회 실패] 미체결 주문 정보 존재하지 않음 - 주문 번호: " + order);
            }
            
            if (TRADE_RES_CODE_ORDER_TRANSMISSION.equals(tradeResultCode)) {
                // 주문접수 응답일 경우

                // TODO toEntity mapper 로 변경
                TradingHistory tradingHistory = TradingHistory
                        .builder()
                        .ticker(command.getTicker())
                        .orderDvsnCode(OrderDvsnCode.find(command.getOrderDvsnCode()))
                        .tradingPrice(tradingPrice)
                        .tradingQty(Integer.parseInt(command.getTradingQty()))
                        .tradeResultCode(tradeResultCode)
                        .kisOrderDvsnCode(KisOrderDvsnCode.find(command.getKisOrderDvsnCode()))
                        .tradingTime(command.getTradingTime())
                        .kisId(command.getKisId())
                        .kisOrderId(kisOrderId)
                        .kisOrOrderId(command.getKisOrOrderId())
                        .build();

                // 주문접수 상태의 체결 내역 생성
                tradingHistoryRepository.save(tradingHistory);

            } else if (TRADE_RES_CODE_COMPLETION.equals(tradeResultCode)) {
                // 체결완료 응답일 경우

                // 주문번호와 일치하는 체결 내역을 모두 조회
                List<TradingHistory> tradingHistoryList = tradingHistoryQueryService.getTradingHistoryList(ticker, kisOrderId);

                // 미체결 내역 추출
                TradingHistory tradingHistory = this.filterNotTradedTradingHistory(tradingHistoryList, command);

                // 미체결 내역 거래
                this.trade(tradingHistory, tradingPrice);

                // 체결 수량의 합 조회
                int tradingQtySum = this.getTradedQtySum(tradingHistoryList, order);

                // 주문 거래 완료
                orderCommandService.trade(command, tradingQtySum);
            }
        } catch (DataAccessException dae) {
            log.error("[체결 내역 저장 DB 에러] exception : {}, ticker: {}, orderId: {}", dae, ticker, kisOrderId);
        } catch (RuntimeException re) {
            log.error("[체결 내역 저장 런타임 에러] exception : {}, ticker: {}, orderId: {}", re, ticker, kisOrderId);
        }
    }

    /**
     * 거래되지 않은 체결 내역 추출
     * @param tradingHistoryList
     * @param command
     * @return
     */
    private TradingHistory filterNotTradedTradingHistory(List<TradingHistory> tradingHistoryList, CreateTradingHistoryProcessCommand command) {
        return tradingHistoryList.stream()
                .filter(history -> history.isAbleToTrade(Integer.parseInt(command.getTradingQty())))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("[체결 내역 조회 에러] - 체결 가능한 내역 미존재 ticker: " + command.getTicker()
                        + ", orderId: " + command.getKisOrderId() + ", tradingQty: " + command.getTradingQty()));
    }

    private void trade(TradingHistory tradingHistory, int tradingPrice) {
        tradingHistory.trade(tradingPrice);
        TradingHistory completedTradeHistory = tradingHistoryRepository.save(tradingHistory);

        log.info("[체결 완료] - 주문번호: {}, 종목: {}, 가격: {}, 수량: {}"
                , completedTradeHistory.getKisOrderId(), completedTradeHistory.getTicker()
                , completedTradeHistory.getTradingPrice(), completedTradeHistory.getTradingQty());
    }

    private int getTradedQtySum(List<TradingHistory> tradingHistoryList, Order order) {
        int tradingQtySum = tradingHistoryList.stream()
                .filter(TradingHistory::isTradeCompleted)
                .mapToInt(TradingHistory::getTradingQty)
                .sum();
        log.info("[주문, 체결 수량 비교] - 주문번호: {}, 주문수량: {}, 체결수량 합: {}", order.getKisOrderNo(), order.getOrderQty(), tradingQtySum);

        return tradingQtySum;
    }
    /**
     * 체결 내역 초기화
     */
    public void deleteHistory() {
        tradingHistoryRepository.deleteAll();
        log.info("[트레이딩 봇] - 체결 내역 초기화 완료");
    }
}
