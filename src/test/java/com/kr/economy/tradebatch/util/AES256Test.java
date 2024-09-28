package com.kr.economy.tradebatch.util;

import com.kr.economy.tradebatch.trade.application.CreateTradingHistoryCommand;
import com.kr.economy.tradebatch.trade.application.SocketProcessService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AES256Test {


    @Autowired
    private SocketProcessService socketProcessService;

    @Test
    public void decryptTest() throws Exception {

        String decryptedMessage = new AES256().decrypt("VdsRTWiodusj4v0hl1HA3/wF4PGOhTwQuD3tkxPhtHAIwx5NTSAFrlIkYptcgC4l9NF5/+1NXp1Qfh/aSwoIlr0u6i6ROpCDAbsJsd/RtD89L8aiDmaUVcXL2p6Qy16wKQnWKR/3+hXfDniDDByemTSOao/pMt+rl+KByuCjPOWhmA9kiJwhl2QSsc+Jt5H+KuNf666UiKILaJer6/07nQ==", "mwmfpuhxvqycewdnmfaxocpmhqlgzdol", "1fc4fdf4dd42d57d");

        String[] result = decryptedMessage.split("\\^");

        String kisId = result[0];
        String kisOrderId = result[2];
        String kisOrOrderID = result[3];
        String orderDvsnCode = result[4];
        String kisOrderDvsnCode = result[6];
        String ticker = result[8];
        String tradingQty = result[9];
        String tradingPrice = result[10];
        String tradingTime = result[11];
        String refuseCode = result[12];
        String tradeResultCode = result[13];
        String tradingResultType = "0".equals(refuseCode) && "2".equals(tradeResultCode) ? "0" : "1";

        CreateTradingHistoryCommand createTradingHistoryCommand = CreateTradingHistoryCommand.builder()
                .ticker(ticker)
                .orderDvsnCode(orderDvsnCode)
                .tradingPrice(Integer.parseInt(tradingPrice))
                .tradingQty(Integer.parseInt(tradingQty))
                .tradingResultType(tradingResultType)
                .kisOrderDvsnCode(kisOrderDvsnCode)
                .kisId(kisId)
                .tradingTime(tradingTime)
                .kisOrderId(kisOrderId)
                .kisOrOrderId(kisOrOrderID)
                .build();

        System.out.println("createTradingHistoryCommand = " + createTradingHistoryCommand);
    }

}