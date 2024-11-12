package com.kr.economy.tradebatch.job.domesticStockTrade.tasklet;

import com.kr.economy.tradebatch.config.WebSocketClientEndPoint;
import com.kr.economy.tradebatch.trade.application.SocketProcessService;
import com.kr.economy.tradebatch.trade.application.commandservices.OrderCommandService;
import com.kr.economy.tradebatch.trade.application.commandservices.SharePriceHistoryCommandService;
import com.kr.economy.tradebatch.trade.application.commandservices.TradingHistoryCommandService;
import com.kr.economy.tradebatch.trade.application.queryservices.KisAccountQueryService;
import com.kr.economy.tradebatch.trade.domain.model.aggregates.KisAccount;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import static com.kr.economy.tradebatch.common.constants.KisStaticValues.*;
import static com.kr.economy.tradebatch.common.util.KisUtil.getRealTimeReqJson;


@Profile("local|stg")
@Configuration
@Slf4j
@RequiredArgsConstructor
public class DomesticStockVirtualTradeTaskletImpl implements DomesticStockTradeTasklet {

    @Value("${credential.kis.trade.his-id}")
    private String hisId;

    private final SharePriceHistoryCommandService sharePriceHistoryCommandService;
    private final TradingHistoryCommandService tradingHistoryCommandService;
    private final SocketProcessService socketProcessService;
    private final KisAccountQueryService kisAccountQueryService;
    private final OrderCommandService orderCommandService;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        log.info("[모의투자 트레이딩 봇] - 실행");

        try {
            // 사용자 정보 조회
            KisAccount kisAccount = kisAccountQueryService.getKisAccount(TEST_ID);
            log.info("[트레이딩 봇] - 전일 히스토리 초기화 전 사용자 정보 : {}", kisAccount);

            // 전일 히스토리 초기화
            orderCommandService.deleteHistory();
            sharePriceHistoryCommandService.deleteHistory();
            tradingHistoryCommandService.deleteHistory();

            final WebSocketClientEndPoint clientEndPoint = new WebSocketClientEndPoint(socketProcessService);

            // 삼성 체결가 조회 요청
            clientEndPoint.sendMessage(getRealTimeReqJson(kisAccount, TR_ID_H0STCNT0, TICKER_SAMSUNG_ELECTRONICS));
            Thread.sleep(5000);

            // 하이닉스 체결가 조회 요청
            clientEndPoint.sendMessage(getRealTimeReqJson(kisAccount, TR_ID_H0STCNT0, TICKER_SK_HYNIX));
            Thread.sleep(5000);

            // 체결 통보 조회 요청
            clientEndPoint.sendMessage(getRealTimeReqJson(kisAccount, TR_ID_H0STCNI9, hisId));
        } catch (InterruptedException ex) {
            log.error("InterruptedException exception: {}", ex.getMessage());
        } catch (RuntimeException re) {
            log.error("RuntimeException exception: {}", re.getMessage());
        }
        return RepeatStatus.FINISHED;
    }
}
