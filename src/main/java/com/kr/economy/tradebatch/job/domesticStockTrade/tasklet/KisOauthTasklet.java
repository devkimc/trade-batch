package com.kr.economy.tradebatch.job.domesticStockTrade.tasklet;

import com.kr.economy.tradebatch.trade.application.commandservices.KisAccountCommandService;
import com.kr.economy.tradebatch.trade.application.queryservices.KisAccountQueryService;
import com.kr.economy.tradebatch.trade.domain.model.aggregates.KisAccount;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Configuration;

import static com.kr.economy.tradebatch.common.constants.KisStaticValues.*;


@Configuration
@Slf4j
@RequiredArgsConstructor
public class KisOauthTasklet implements Tasklet {

    private final KisAccountQueryService kisAccountQueryService;
    private final KisAccountCommandService kisAccountCommandService;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        log.info("[모의투자 트레이딩 봇] - 실행");

        try {
            kisAccountCommandService.oauthToken(TEST_ID);

            // 사용자 정보 조회
            KisAccount kisAccount = kisAccountQueryService.getKisAccount(TEST_ID);
            log.info("[트레이딩 봇] - 토큰 발급 후 사용자 정보 : {}", kisAccount);

        } catch (RuntimeException re) {
            log.error("RuntimeException exception: {}", re.getMessage());
        }
        return RepeatStatus.FINISHED;
    }
}
