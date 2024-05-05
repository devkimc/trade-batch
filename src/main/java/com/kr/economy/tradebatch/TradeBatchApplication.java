package com.kr.economy.tradebatch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

//@EnableScheduling
//@EnableBatchProcessing
@EnableFeignClients
@SpringBootApplication
public class TradeBatchApplication {

	public static void main(String[] args) {
		SpringApplication.run(TradeBatchApplication.class, args);
	}

}
