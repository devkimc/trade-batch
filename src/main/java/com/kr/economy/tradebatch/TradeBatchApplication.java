package com.kr.economy.tradebatch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

//@EnableScheduling
//@EnableBatchProcessing
@EnableFeignClients
@SpringBootApplication
@EnableJpaAuditing
//@EnableJpaRepositories(basePackages = "com.kr.economy")
public class TradeBatchApplication {

	public static void main(String[] args) {
		SpringApplication.run(TradeBatchApplication.class, args);
	}

}
