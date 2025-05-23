package com.kr.economy.tradebatch;

import com.kr.economy.tradebatch.trade.domain.constants.KisOrderDvsnCode;
import com.kr.economy.tradebatch.trade.domain.constants.OrderDvsnCode;
import com.kr.economy.tradebatch.trade.domain.model.commands.CreateTradingHistoryCommand;
import com.kr.economy.tradebatch.trade.application.SocketProcessService;
import com.kr.economy.tradebatch.trade.application.commandservices.StockQuotesCommandService;
import com.kr.economy.tradebatch.trade.application.commandservices.TradingHistoryCommandService;
import com.kr.economy.tradebatch.trade.application.queryservices.KoreaStockOrderQueryService;
import com.kr.economy.tradebatch.trade.application.queryservices.StockItemInfoQueryService;
import com.kr.economy.tradebatch.trade.application.queryservices.TradingHistoryQueryService;
import com.kr.economy.tradebatch.trade.domain.model.aggregates.*;
import com.kr.economy.tradebatch.trade.infrastructure.repositories.StockQuotesSimRepository;
import com.kr.economy.tradebatch.trade.infrastructure.repositories.TradingHistoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static com.kr.economy.tradebatch.common.constants.KisStaticValues.TEST_ID;

@SpringBootTest
public class TradeBatchApplicationTests {

	@Autowired
	private StockQuotesCommandService stockQuotesCommandService;

	@Autowired
	private TradingHistoryCommandService tradingHistoryCommandService;

	@Autowired
	private TradingHistoryQueryService tradingHistoryQueryService;

	@Autowired
	private StockQuotesSimRepository stockQuotesSimRepository;

	@Autowired
	private SocketProcessService socketProcessService;

	@Autowired
	private StockItemInfoQueryService stockItemInfoQueryService;

	@Autowired
	private TradingHistoryRepository tradingHistoryRepository;

	@Test
	public void buySignTest() throws Exception {

		// 배치 실행 전 데이터 초기화
		stockQuotesCommandService.deleteHistory();
		tradingHistoryCommandService.deleteHistory();
		System.out.println("데이터 초기화");

		// DB 테이블로 테스트 시
		List<StockQuotesSim> stockQuotes = stockQuotesSimRepository.findAll();

		for (int i = 0; i < stockQuotes.size(); i++) {
			StockQuotesSim sharePriceData = stockQuotes.get(i);

			String ticker = sharePriceData.getTicker();
			String tradingTime = sharePriceData.getTradingTime();
			int sharePrice = sharePriceData.getQuotedPrice();
			Float bidAskBalanceRatio = sharePriceData.getBidAskBalanceRatio();

			// 실시간 현재가 저장
			stockQuotesCommandService.createStockQuote(ticker, sharePrice, bidAskBalanceRatio, tradingTime);

			// 당일 마지막 체결 내역 조회
			Optional<TradingHistory> lastTradingHistory = tradingHistoryQueryService.getLastHistoryOfToday(ticker);

			// 주식 종목 정보 조회
			StockItemInfo stockItemInfo = stockItemInfoQueryService.getStockItemInfo(ticker);

			// 마지막 체결 내역이 매수일 경우에만 매도
			if (lastTradingHistory.isPresent() && lastTradingHistory.get().isBuyTrade()) {
				if (koreaStockOrderQueryService.getSellSignal(ticker, sharePrice, lastTradingHistory.get().getTradingPrice(), tradingTime)) {
					CreateTradingHistoryCommand createTradingHistoryCommand = CreateTradingHistoryCommand.builder()
							.ticker(ticker)
							.orderDvsnCode("01")
							.tradingPrice(sharePrice - stockItemInfo.getParValue() * 1)
							.tradingQty(1)
							.tradingResultType("0")
							.kisOrderDvsnCode("00")
							.tradingTime(tradingTime)
							.build();
					this.createTradingHistory(createTradingHistoryCommand);
				}
			} else {
				if (koreaStockOrderQueryService.getBuySignal(ticker, sharePrice, tradingTime, TEST_ID ,"")) {
					CreateTradingHistoryCommand createTradingHistoryCommand = CreateTradingHistoryCommand.builder()
							.ticker(ticker)
							.orderDvsnCode("02")
							.tradingPrice(sharePrice + stockItemInfo.getParValue() * 1)
							.tradingQty(1)
							.tradingResultType("0")
							.kisOrderDvsnCode("00")
							.tradingTime(tradingTime)
							.build();
					this.createTradingHistory(createTradingHistoryCommand);
				}
			}
		}
	}

	private void createTradingHistory(CreateTradingHistoryCommand command) {
		TradingHistory tradingHistory = TradingHistory
				.builder()
				.ticker(command.getTicker())
				.orderDvsnCode(OrderDvsnCode.find(command.getOrderDvsnCode()))
				.tradingPrice(command.getTradingPrice())
				.tradingQty(command.getTradingQty())
				.tradeResultCode(command.getTradeResultCode())
				.kisOrderDvsnCode(KisOrderDvsnCode.find(command.getKisOrderDvsnCode()))
				.tradingTime(command.getTradingTime())
				.kisId(command.getKisId())
				.kisOrderId(command.getKisOrderId())
				.kisOrOrderId(command.getKisOrOrderId())
				.build();
		tradingHistoryRepository.save(tradingHistory);
	}
}
