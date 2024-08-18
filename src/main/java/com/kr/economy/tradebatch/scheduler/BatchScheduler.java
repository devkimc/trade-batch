package com.kr.economy.tradebatch.scheduler;

import com.kr.economy.tradebatch.job.SamsungStockTradeJobConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class BatchScheduler {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private SamsungStockTradeJobConfig jobConfig;

    // cron
    // 초 / 분 / 시 / 일 / 월 / 요일 (0-7, 0과 7은 일요일)
    @Scheduled(cron = "0 34 14 * * *")
    public void runJob() {
        Map<String, JobParameter<?>> confMap = new HashMap<>();
        confMap.put("time", new JobParameter(System.currentTimeMillis(), Long.class, true));
        JobParameters jobParameters = new JobParameters(confMap);

        try {
            jobLauncher.run(jobConfig.testJob(), jobParameters);
        } catch (RuntimeException e) {
            log.error(e.getMessage());
        } catch (JobInstanceAlreadyCompleteException e) {
            throw new RuntimeException(e);
        } catch (JobExecutionAlreadyRunningException e) {
            throw new RuntimeException(e);
        } catch (JobParametersInvalidException e) {
            throw new RuntimeException(e);
        } catch (JobRestartException e) {
            throw new RuntimeException(e);
        }
    }
}
