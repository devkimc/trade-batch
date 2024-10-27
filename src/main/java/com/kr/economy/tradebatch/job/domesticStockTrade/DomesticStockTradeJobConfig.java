package com.kr.economy.tradebatch.job.domesticStockTrade;

import com.kr.economy.tradebatch.job.domesticStockTrade.tasklet.DomesticStockVirtualTradeTaskletImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DomesticStockTradeJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DomesticStockVirtualTradeTaskletImpl domesticStockTradeTasklet;


    @Bean
    public Job tradeDomesticStockJob() {
        return new JobBuilder("DOMESTIC_STOCK_TRADE_JOB", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(tradeDomesticStockStep())
                .build();
    }

    @Bean
    @JobScope
    public Step tradeDomesticStockStep() {
        return new StepBuilder("DOMESTIC_STOCK_TRADE_STEP", jobRepository)
                .tasklet(domesticStockTradeTasklet, transactionManager)
                .build();
    }
}
