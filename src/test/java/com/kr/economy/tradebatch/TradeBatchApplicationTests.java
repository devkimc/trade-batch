package com.kr.economy.tradebatch;

import com.kr.economy.tradebatch.trade.application.CreateTradingHistoryCommand;
import com.kr.economy.tradebatch.trade.application.SocketProcessService;
import com.kr.economy.tradebatch.trade.application.commandservices.BidAskBalanceCommandService;
import com.kr.economy.tradebatch.trade.application.commandservices.SharePriceHistoryCommandService;
import com.kr.economy.tradebatch.trade.application.commandservices.TradingHistoryCommandService;
import com.kr.economy.tradebatch.trade.application.queryservices.KoreaStockOrderQueryService;
import com.kr.economy.tradebatch.trade.application.queryservices.TradingHistoryQueryService;
import com.kr.economy.tradebatch.trade.domain.model.aggregates.*;
import com.kr.economy.tradebatch.trade.domain.repositories.BidAskBalanceRatioHistorySimRepository;
import com.kr.economy.tradebatch.trade.domain.repositories.SharePriceHistorySimRepository;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.util.List;
import java.util.Optional;

import static com.kr.economy.tradebatch.common.constants.KisStaticValues.TICKER_SAMSUNG;

@SpringBootTest
//@Import({TestConfig.class})
public class TradeBatchApplicationTests {

	@Autowired
	private SharePriceHistoryCommandService sharePriceHistoryCommandService;

	@Autowired
	private BidAskBalanceCommandService bidAskBalanceCommandService;

	@Autowired
	private TradingHistoryCommandService tradingHistoryCommandService;

	@Autowired
	private KoreaStockOrderQueryService koreaStockOrderQueryService;

	@Autowired
	private TradingHistoryQueryService tradingHistoryQueryService;

	@Autowired
	private SharePriceHistorySimRepository sharePriceHistorySimRepository;

	@Autowired
	private BidAskBalanceRatioHistorySimRepository bidAskBalanceRatioHistorySimRepository;

	@Autowired
	private SocketProcessService socketProcessService;

	@Test
	public void buySignTest() throws Exception {

		// 배치 실행 전 데이터 초기화
		sharePriceHistoryCommandService.deleteHistory();
		bidAskBalanceCommandService.deleteHistory();
		tradingHistoryCommandService.deleteHistory();
		System.out.println("데이터 초기화");

		// DB 테이블로 테스트 시
		List<SharePriceHistorySim> sharePriceHistoryList = sharePriceHistorySimRepository.findAll();
		List<BidAskBalanceRatioHistorySim> bidAskBalanceRatioHistoryList = bidAskBalanceRatioHistorySimRepository.findAll();

		System.out.println("bidAskBalanceRatioHistoryList = " + bidAskBalanceRatioHistoryList.size());
		System.out.println("bidAskBalanceRatioHistoryList.size() = " + bidAskBalanceRatioHistoryList.size());

		for (int i = 0; i < sharePriceHistoryList.size(); i++) {
			SharePriceHistorySim sharePriceData = sharePriceHistoryList.get(i);
			BidAskBalanceRatioHistorySim bidAskData = bidAskBalanceRatioHistoryList.get(i);

			String tradingTime = sharePriceData.getTradingTime();
			int sharePrice = sharePriceData.getSharePrice();
			float bidAskBalanceRatio = bidAskData.getBidAskBalanceRatio();

			// 실시간 현재가 저장
			sharePriceHistoryCommandService.createSharePriceHistory(TICKER_SAMSUNG, sharePrice, tradingTime);

			// 실시간 매수매도 잔량비 저장
			bidAskBalanceCommandService.createBidAskBalanceRatioHistory(TICKER_SAMSUNG, bidAskBalanceRatio, tradingTime);

			// 당일 마지막 체결 내역 조회
			Optional<TradingHistory> lastTradingHistory = tradingHistoryQueryService.getLastHistoryOfToday(TICKER_SAMSUNG);

			// 마지막 체결 내역이 매수일 경우에만 매도
			if (lastTradingHistory.isPresent() && lastTradingHistory.get().isBuyTrade()) {
				if (lastTradingHistory.get().isSellSignal(sharePrice, tradingTime)) {
					CreateTradingHistoryCommand createTradingHistoryCommand = CreateTradingHistoryCommand.builder()
							.ticker(TICKER_SAMSUNG)
							.orderDvsnCode("01")
							.tradingPrice(sharePrice - 100)
							.tradingQty(1)
							.tradingResultType("0")
							.kisOrderDvsnCode("00")
							.kisId("") // TODO 제거하기
							.tradingTime(tradingTime) // TODO 제거하기
							.build();
					tradingHistoryCommandService.createTradingHistory(createTradingHistoryCommand);
					System.out.println("********************************************* " + tradingTime + " : " + (sharePrice - 100) + " 매도 체결 ********************************************* ");
				}
			} else {
				if (koreaStockOrderQueryService.getBuySignal(TICKER_SAMSUNG, tradingTime)) {
					CreateTradingHistoryCommand createTradingHistoryCommand = CreateTradingHistoryCommand.builder()
							.ticker(TICKER_SAMSUNG)
							.orderDvsnCode("02")
							.tradingPrice(sharePrice + 100)
							.tradingQty(1)
							.tradingResultType("0")
							.kisOrderDvsnCode("00")
							.kisId("") // TODO 제거하기
							.tradingTime(tradingTime) // TODO 제거하기
							.build();
					tradingHistoryCommandService.createTradingHistory(createTradingHistoryCommand);
					System.out.println("********************************************* " + tradingTime + " : " + (sharePrice + 100) + " 매수 체결 ********************************************* ");
				}
			}
		}

		// 로그 파일로 테스트 시
//		FileInputStream fis = new FileInputStream("src/test/resources/share-price-0821.log");
//		String[] responseList = IOUtils.toString(fis, "UTF-8").split("message = ");
//		for (int i = 0; i < responseList.length; i++) {
//			if (responseList[i].length() < 30) continue;
//			String[] resultBody = responseList[i].split("\\|");
//
//			if (resultBody.length < 4) continue;
//			String[] result = resultBody[3].split("\\^");
//
//			String tradingTime = result[1];
//			int sharePrice = Integer.parseInt(result[2]);
//			float bidAskBalanceRatio = Float.parseFloat(result[37]) / Float.parseFloat(result[36]);
//
//			// 실시간 현재가 저장
//			sharePriceHistoryCommandService.createSharePriceHistory(TICKER_SAMSUNG, sharePrice, tradingTime);
//
//			// 실시간 매수매도 잔량비 저장
//			bidAskBalanceCommandService.createBidAskBalanceRatioHistory(TICKER_SAMSUNG, bidAskBalanceRatio);
//
//			// 당일 마지막 체결 내역 조회
//			Optional<TradingHistory> lastTradingHistory = tradingHistoryQueryService.getLastHistoryOfToday(TICKER_SAMSUNG);
//
//			// 마지막 체결 내역이 매수일 경우에만 매도
//			if (lastTradingHistory.isPresent() && lastTradingHistory.get().isBuyTrade()) {
//				if (lastTradingHistory.get().isSellSignal(sharePrice)) {
//					CreateTradingHistoryCommand createTradingHistoryCommand = CreateTradingHistoryCommand.builder()
//							.ticker(TICKER_SAMSUNG)
//							.orderDvsnCode("01")
//							.tradingPrice(sharePrice - 100)
//							.tradingQty(1)
//							.tradingResultType("0")
//							.kisOrderDvsnCode("00")
//							.kisId("") // TODO 제거하기
//							.tradingTime("") // TODO 제거하기
//							.build();
//					tradingHistoryCommandService.createTradingHistory(createTradingHistoryCommand);
//					System.out.println("********************************************* " + tradingTime + " : " + (sharePrice - 100) + " 매도 체결 ********************************************* ");
//				}
//			} else {
//				if (koreaStockOrderQueryService.getBuySignal(TICKER_SAMSUNG)) {
//					CreateTradingHistoryCommand createTradingHistoryCommand = CreateTradingHistoryCommand.builder()
//							.ticker(TICKER_SAMSUNG)
//							.orderDvsnCode("02")
//							.tradingPrice(sharePrice + 100)
//							.tradingQty(1)
//							.tradingResultType("0")
//							.kisOrderDvsnCode("00")
//							.kisId("") // TODO 제거하기
//							.tradingTime("") // TODO 제거하기
//							.build();
//					tradingHistoryCommandService.createTradingHistory(createTradingHistoryCommand);
//					System.out.println("********************************************* " + tradingTime + " : " + (sharePrice + 100) + " 매수 체결 ********************************************* ");
//				}
//			}
//		}
	}
}
