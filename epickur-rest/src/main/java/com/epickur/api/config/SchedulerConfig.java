package com.epickur.api.config;

import com.epickur.api.commons.CommonsUtil;
import com.epickur.api.cron.CleanKeysJob;
import com.epickur.api.cron.CleanVouchersJob;
import com.epickur.api.cron.MongoDBDumpJob;
import com.epickur.api.cron.OrderJob;
import com.epickur.api.dump.MongoDBDump;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

@Configuration
public class SchedulerConfig {

	@Autowired
	private ApplicationContext applicationContext;

	@Bean
	public SchedulerFactoryBean schedulerFactoryBean() {
		SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
		schedulerFactoryBean.setAutoStartup(true);
		AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
		jobFactory.setApplicationContext(applicationContext);
		schedulerFactoryBean.setJobFactory(jobFactory);
		return schedulerFactoryBean;
	}

	@Profile("prod")
	@Bean
	public MongoDBDumpJob databaseDump() {
		return new MongoDBDumpJob();
	}

	@Bean
	@Scope("prototype")
	public MongoDBDump dbDump(){
		return new MongoDBDump(CommonsUtil.getCurrentDateInFormat("ddMMyyyy-hhmmss"));
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

	/**
	 * Add autowire capability to Quartz.
	 */
	private class AutowiringSpringBeanJobFactory extends SpringBeanJobFactory implements ApplicationContextAware {

		private transient AutowireCapableBeanFactory beanFactory;

		@Override
		public void setApplicationContext(final ApplicationContext context) {
			beanFactory = context.getAutowireCapableBeanFactory();
		}

		@Override
		protected Object createJobInstance(final TriggerFiredBundle bundle) throws Exception {
			final Object job = super.createJobInstance(bundle);
			beanFactory.autowireBean(job);
			return job;
		}
	}
}
