package com.kr.economy.tradebatch;

import com.kr.economy.tradebatch.trade.application.commandservices.SharePriceHistoryCommandService;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.io.FileInputStream;

import static com.kr.economy.tradebatch.common.constants.KisStaticValues.TICKER_SAMSUNG;

@SpringBootTest
@Import({TestConfig.class})
public class GetRealTimeSharePriceTest {

	@Autowired
	private SharePriceHistoryCommandService sharePriceHistoryCommandService;

	@Test
	public void buySignTest() throws Exception {

		FileInputStream fis = new FileInputStream("src/test/resources/share_price_test.txt");
		String quoteTestData = IOUtils.toString(fis, "UTF-8");

		String[] responseList = quoteTestData.split(": response");

		for (int i = 0; i < responseList.length; i++) {
			if (responseList[i].length() < 40) {
				continue;
			}

			String[] resultList = responseList[i].split("\\|");

			if (resultList.length < 4) {
				continue;
			}

			String[] sharePriceDataList = resultList[3].split("\\^");

			int sharePrice = Integer.parseInt(sharePriceDataList[2]);
			System.out.println("sharePrice = " + sharePrice);

			sharePriceHistoryCommandService.createSharePriceHistory(TICKER_SAMSUNG, sharePrice);
		}
	}
}
