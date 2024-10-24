package com.kr.economy.tradebatch;

import com.kr.economy.tradebatch.trade.application.CreateTradingHistoryCommand;
import com.kr.economy.tradebatch.trade.application.SocketProcessService;
import com.kr.economy.tradebatch.trade.application.commandservices.SharePriceHistoryCommandService;
import com.kr.economy.tradebatch.trade.application.commandservices.TradingHistoryCommandService;
import com.kr.economy.tradebatch.trade.application.queryservices.KoreaStockOrderQueryService;
import com.kr.economy.tradebatch.trade.application.queryservices.StockItemInfoQueryService;
import com.kr.economy.tradebatch.trade.application.queryservices.TradingHistoryQueryService;
import com.kr.economy.tradebatch.trade.domain.model.aggregates.*;
import com.kr.economy.tradebatch.trade.domain.repositories.SharePriceHistorySimRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

@SpringBootTest
//@Import({TestConfig.class})
public class TradeBatchApplicationTests {

	@Autowired
	private SharePriceHistoryCommandService sharePriceHistoryCommandService;

	@Autowired
	private TradingHistoryCommandService tradingHistoryCommandService;

	@Autowired
	private KoreaStockOrderQueryService koreaStockOrderQueryService;

	@Autowired
	private TradingHistoryQueryService tradingHistoryQueryService;

	@Autowired
	private SharePriceHistorySimRepository sharePriceHistorySimRepository;

	@Autowired
	private SocketProcessService socketProcessService;

	@Autowired
	private StockItemInfoQueryService stockItemInfoQueryService;

	@Test
	public void buySignTest() throws Exception {

		// 배치 실행 전 데이터 초기화
		sharePriceHistoryCommandService.deleteHistory();
		tradingHistoryCommandService.deleteHistory();
		System.out.println("데이터 초기화");

		// DB 테이블로 테스트 시
		List<SharePriceHistorySim> sharePriceHistoryList = sharePriceHistorySimRepository.findAll();

		for (int i = 0; i < sharePriceHistoryList.size(); i++) {
			SharePriceHistorySim sharePriceData = sharePriceHistoryList.get(i);

			String ticker = sharePriceData.getTicker();
			String tradingTime = sharePriceData.getTradingTime();
			int sharePrice = sharePriceData.getSharePrice();
			Float bidAskBalanceRatio = sharePriceData.getBidAskBalanceRatio();

			// 실시간 현재가 저장
			sharePriceHistoryCommandService.createSharePriceHistory(ticker, sharePrice, bidAskBalanceRatio, tradingTime);

			// 당일 마지막 체결 내역 조회
			Optional<TradingHistory> lastTradingHistory = tradingHistoryQueryService.getLastHistoryOfToday(ticker);

			// 주식 종목 정보 조회
			StockItemInfo stockItemInfo = stockItemInfoQueryService.getStockItemInfo(ticker);

			// 마지막 체결 내역이 매수일 경우에만 매도
			if (lastTradingHistory.isPresent() && lastTradingHistory.get().isBuyTrade()) {
				if (lastTradingHistory.get().isSellSignal(sharePrice, tradingTime)) {
					CreateTradingHistoryCommand createTradingHistoryCommand = CreateTradingHistoryCommand.builder()
							.ticker(ticker)
							.orderDvsnCode("01")
							.tradingPrice(sharePrice - stockItemInfo.getParValue() * 1)
							.tradingQty(1)
							.tradingResultType("0")
							.kisOrderDvsnCode("00")
							.tradingTime(tradingTime)
							.build();
					tradingHistoryCommandService.createTradingHistory(createTradingHistoryCommand);
					System.out.println("****************** " + tradingTime + " : " + (sharePrice - stockItemInfo.getParValue() * 1) + " 매도 체결 ******************");
				}
			} else {
				if (koreaStockOrderQueryService.getBuySignal(ticker, tradingTime)) {
					CreateTradingHistoryCommand createTradingHistoryCommand = CreateTradingHistoryCommand.builder()
							.ticker(ticker)
							.orderDvsnCode("02")
							.tradingPrice(sharePrice + stockItemInfo.getParValue() * 1)
							.tradingQty(1)
							.tradingResultType("0")
							.kisOrderDvsnCode("00")
							.tradingTime(tradingTime)
							.build();
					tradingHistoryCommandService.createTradingHistory(createTradingHistoryCommand);
					System.out.println("****************** " + tradingTime + " : " + (sharePrice + stockItemInfo.getParValue() * 1) + " 매수 체결 ******************");
				}
			}
		}
	}
}
