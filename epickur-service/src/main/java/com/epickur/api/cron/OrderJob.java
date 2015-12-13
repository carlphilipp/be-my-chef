package com.epickur.api.cron;

import com.epickur.api.config.EpickurProperties;
import com.epickur.api.entity.Order;
import com.epickur.api.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

/**
 * The goal of this class is to start all the cron jobs.
 *
 * @author cph
 * @version 1.0
 */
@Slf4j
@Component
public class OrderJob {

	@Autowired
	public EpickurProperties properties;
	@Autowired
	private SchedulerFactoryBean schedulerFactoryBean;

	/**
	 * Add a temporary order job
	 *
	 * @param user  The User
	 * @param order The Order
	 */
	public void addTemporaryOrderJob(final User user, final Order order) {
		final String userId = user.getId().toHexString();
		final String orderId = order.getId().toHexString();
		final DateTime orderDate = order.getCreatedAt();
		final DateTime scheduleCancelDate = orderDate.plusMinutes(properties.getOrderTimeLimit());
		final String identity = "cancelOrder_" + orderId;
		final JobDetail cancelOrder = JobBuilder.newJob(CancelOrderJob.class)
				.withIdentity(identity)
				.usingJobData("orderId", orderId)
				.usingJobData("userId", userId)
				.build();
		final Trigger triggerCancelOrder = TriggerBuilder.newTrigger()
				.withIdentity(identity)
				.startAt(scheduleCancelDate.toDate())
				.build();
		try {
			final Scheduler scheduler = schedulerFactoryBean.getObject();
			scheduler.scheduleJob(cancelOrder, triggerCancelOrder);
			log.info("Added job '{}' to the scheduler with orderId {} and userId ", identity, orderId, userId);
		} catch (SchedulerException se) {
			log.error(se.getLocalizedMessage(), se);
		}
	}

	/**
	 * Remove a temporary order job
	 *
	 * @param orderId The order Id
	 */
	public void removeTemporaryOrderJob(final String orderId) {
		final String identity = "cancelOrder_" + orderId;
		log.info("Remove job '{}' from the scheduler", identity);
		final JobKey jobKey = new JobKey(identity);
		try {
			final Scheduler scheduler = schedulerFactoryBean.getObject();
			scheduler.deleteJob(jobKey);
		} catch (SchedulerException se) {
			log.error(se.getLocalizedMessage(), se);
		}
	}
}
