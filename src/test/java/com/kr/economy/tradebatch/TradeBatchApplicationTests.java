package com.kr.economy.tradebatch;

import com.kr.economy.tradebatch.trade.application.commandservices.BidAskBalanceCommandService;
import com.kr.economy.tradebatch.trade.application.queryservices.BidAskBalanceQueryService;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.io.FileInputStream;

@SpringBootTest
@Import({TestConfig.class})
public class TradeBatchApplicationTests {

	@Autowired
	private BidAskBalanceCommandService bidAskBalanceCommandService;

	@Autowired
	private BidAskBalanceQueryService bidAskBalanceQueryService;


	@Test
	public void tttt() {
		System.out.println("true = " + true);
	}

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

			String currentPrice = quoteDataList[13];
			String totalAskAmount = quoteDataList[43];
			String totalBidAmount = quoteDataList[44];

			float bidAskBalanceRatio = Float.parseFloat(totalBidAmount) / Float.parseFloat(totalAskAmount);

			bidAskBalanceCommandService.createBidAskBalanceRatioHistory(bidAskBalanceRatio, Float.parseFloat(currentPrice));
			bidAskBalanceQueryService.getBuySignal();
		}
	}

}
