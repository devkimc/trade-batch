package com.kr.economy.tradebatch.job;

import com.kr.economy.tradebatch.trade.application.KisSocketTestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class KisSocketJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final KisSocketTestService kisSocketTestService;


    @Bean
    public Job kisSocketJob() {
        return new JobBuilder("KIS_SOCKET", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(kisSocketStep())
                .build();
    }

    @Bean
    @JobScope
    public Step kisSocketStep() {
        return new StepBuilder("kisSocketStep", jobRepository)
                .tasklet(kisSocketTasklet(), transactionManager).build();
    }

    @Bean
    @JobScope
    public Tasklet kisSocketTasklet() {
        kisSocketTestService.test("DEVKIMC");

        return (contribution, chunkContext) -> {
            return RepeatStatus.FINISHED;
        };
    }
}
