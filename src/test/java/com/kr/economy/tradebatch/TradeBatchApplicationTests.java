package com.kr.economy.tradebatch;

import com.kr.economy.tradebatch.trade.application.commandservices.BidAskBalanceCommandService;
import com.kr.economy.tradebatch.trade.application.commandservices.SharePriceHistoryCommandService;
import com.kr.economy.tradebatch.trade.application.queryservices.KoreaStockOrderQueryService;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.io.FileInputStream;

import static com.kr.economy.tradebatch.common.constants.KisStaticValues.TICKER_SAMSUNG;

@SpringBootTest
@Import({TestConfig.class})
public class TradeBatchApplicationTests {

	@Autowired
	private SharePriceHistoryCommandService sharePriceHistoryCommandService;

	@Autowired
	private BidAskBalanceCommandService bidAskBalanceCommandService;

	@Autowired
	private KoreaStockOrderQueryService koreaStockOrderQueryService;

	@Test
	public void buySignTest() throws Exception {

		FileInputStream fis = new FileInputStream("src/test/resources/quote_test.txt");
		String quoteTestData = IOUtils.toString(fis, "UTF-8");

		String[] responseList = quoteTestData.split("response = ");

		for (int i = 0; i < responseList.length; i++) {
			if (responseList[i].length() < 30) {
				continue;
			}

			String[] resultList = responseList[i].split("\\|");
			String[] quoteDataList = resultList[3].split("\\^");

			Float valuableAskAmount = 0F;
			Float valuableBidAmount = 0F;

			// 응답값 중 3번째 인덱스 부터가 매도 호가임
			// 매수 호가는 매수의 10번 째 뒤부터임
			for (int j = 3; j < 8; j++) {
				valuableAskAmount += Float.parseFloat(quoteDataList[j]);
				valuableBidAmount += Float.parseFloat(quoteDataList[j + 10]);
			}

			float bidAskBalanceRatio = valuableBidAmount / valuableAskAmount;

			bidAskBalanceCommandService.createBidAskBalanceRatioHistory(TICKER_SAMSUNG, bidAskBalanceRatio);

//			executionHistoryCommandService.createExecutionHistory(TICKER_SAMSUNG, );
//			koreaStockOrderQueryService.getBuySignal(TICKER_SAMSUNG);
		}
	}

}
