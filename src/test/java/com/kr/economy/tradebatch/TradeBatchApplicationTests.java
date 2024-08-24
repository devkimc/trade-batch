package com.kr.economy.tradebatch;

import com.kr.economy.tradebatch.trade.application.CreateTradingHistoryCommand;
import com.kr.economy.tradebatch.trade.application.commandservices.BidAskBalanceCommandService;
import com.kr.economy.tradebatch.trade.application.commandservices.SharePriceHistoryCommandService;
import com.kr.economy.tradebatch.trade.application.commandservices.TradingHistoryCommandService;
import com.kr.economy.tradebatch.trade.application.queryservices.KoreaStockOrderQueryService;
import com.kr.economy.tradebatch.trade.domain.constants.OrderDvsnCode;
import com.kr.economy.tradebatch.trade.domain.model.aggregates.TradingHistory;
import com.kr.economy.tradebatch.trade.infrastructure.repositories.TradingHistoryRepositoryCustom;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.io.FileInputStream;
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
	private KoreaStockOrderQueryService koreaStockOrderQueryService;

	@Autowired
	private TradingHistoryCommandService tradingHistoryCommandService;

	// TODO Service 호출하는 방식으로 변경
	@Autowired
	private TradingHistoryRepositoryCustom tradingHistoryRepositoryCustom;

	@Test
	public void buySignTest() throws Exception {

		FileInputStream fis = new FileInputStream("src/test/resources/share-price-0821.log");
		String quoteTestData = IOUtils.toString(fis, "UTF-8");

		String[] responseList = quoteTestData.split("message = ");

		for (int i = 0; i < responseList.length; i++) {
			if (responseList[i].length() < 30) {
				continue;
			}

			String[] resultBody = responseList[i].split("\\|");
			String[] result = resultBody[3].split("\\^");

			String tradingTime = result[1];
			int sharePrice = Integer.parseInt(result[2]);
			Float valuableAskAmount = Float.parseFloat(result[36]);
			Float valuableBidAmount = Float.parseFloat(result[37]);

			float bidAskBalanceRatio = valuableBidAmount / valuableAskAmount;

			sharePriceHistoryCommandService.createSharePriceHistory(TICKER_SAMSUNG, sharePrice, tradingTime);
			bidAskBalanceCommandService.createBidAskBalanceRatioHistory(TICKER_SAMSUNG, bidAskBalanceRatio);

			Optional<TradingHistory> lastTradingHistory = tradingHistoryRepositoryCustom.getLastTradingHistory(TICKER_SAMSUNG);

			System.out.println(tradingTime + " : " + sharePrice);

			// TODO 당일 데이터만 조회하도록 쿼리 수정 필요
			// 당일의 마지막 데이터가 매수일 경우에만 매도
			if (lastTradingHistory.isPresent() && OrderDvsnCode.BUY.equals(lastTradingHistory.get().getOrderDvsnCode())) {

				// TODO 변수명 수정
				boolean highPoint = sharePrice >= lastTradingHistory.get().getTradingPrice() + 300;
				boolean lowPoint = sharePrice <= lastTradingHistory.get().getTradingPrice() - 600;

				// 구매 가격보다 현재가가 300원이 높거나 낮으면 매도 주문
				if (highPoint || lowPoint) {

					System.out.println("********************************************************** 매도 체결 **********************************************************");
					CreateTradingHistoryCommand createTradingHistoryCommand = CreateTradingHistoryCommand.builder()
							.ticker(TICKER_SAMSUNG)
							.orderDvsnCode("01")
							.tradingPrice(sharePrice - 100)
							.tradingQty(1)
							.tradingResultType("0")
							.kisOrderDvsnCode("00")
							.kisId("") // TODO 제거하기
							.tradingTime("") // TODO 제거하기
							.build();
					tradingHistoryCommandService.createTradingHistory(createTradingHistoryCommand);
				}
			} else {
				boolean buySignal = koreaStockOrderQueryService.getBuySignal(TICKER_SAMSUNG);

				if (buySignal) {
					CreateTradingHistoryCommand createTradingHistoryCommand = CreateTradingHistoryCommand.builder()
							.ticker(TICKER_SAMSUNG)
							.orderDvsnCode("02")
							.tradingPrice(sharePrice + 100)
							.tradingQty(1)
							.tradingResultType("0")
							.kisOrderDvsnCode("00")
							.kisId("") // TODO 제거하기
							.tradingTime("") // TODO 제거하기
							.build();
					tradingHistoryCommandService.createTradingHistory(createTradingHistoryCommand);

					System.out.println("********************************************************** 매수 체결 **********************************************************");
				}
			}
		}
	}

}
