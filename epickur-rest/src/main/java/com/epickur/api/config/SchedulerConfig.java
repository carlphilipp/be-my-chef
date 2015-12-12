package com.epickur.api.config;

import com.epickur.api.cron.CleanKeysJob;
import com.epickur.api.cron.CleanVouchersJob;
import com.epickur.api.cron.MongoDBDumpJob;
import com.epickur.api.cron.OrderJob;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 * Created by carl on 12/9/2015.
 */
@Configuration
public class SchedulerConfig {

	@Bean
	public SchedulerFactoryBean schedulerFactoryBean() {
		SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
		schedulerFactoryBean.setAutoStartup(true);
		return schedulerFactoryBean;
	}

	@Profile("prod")
	@Bean
	public MongoDBDumpJob databaseDump() {
		return new MongoDBDumpJob();
	}

	@Bean
	public CleanVouchersJob cleanVouchers() {
		return new CleanVouchersJob();
	}

	@Bean
	public CleanKeysJob cleanKeys() {
		return new CleanKeysJob();
	}

	@Bean
	public OrderJob orderJob() {
		return new OrderJob();
	}
}
