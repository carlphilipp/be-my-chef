package com.epickur.api.cron;

import com.epickur.api.config.EpickurProperties;
import com.epickur.api.entity.Order;
import com.epickur.api.entity.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
@Component
public class OrderJob {

	/**
	 * Logger
	 */
	private static final Logger LOG = LogManager.getLogger(OrderJob.class.getSimpleName());

	@Autowired
	public EpickurProperties properties;
	@Autowired
	private SchedulerFactoryBean schedulerFactoryBean;

	/**
	 * Order max time
	 */
	//private int orderMaxTime;

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
			LOG.info("Added job '" + identity + "' to the scheduler with orderId " + orderId + " and userId " + userId);
		} catch (SchedulerException se) {
			LOG.error(se.getLocalizedMessage(), se);
		}
	}

	/**
	 * Remove a temporary order job
	 *
	 * @param orderId The order Id
	 */
	public void removeTemporaryOrderJob(final String orderId) {
		final String identity = "cancelOrder_" + orderId;
		LOG.info("Remove job '" + identity + "' from the scheduler");
		final JobKey jobKey = new JobKey(identity);
		try {
			final Scheduler scheduler = schedulerFactoryBean.getObject();
			scheduler.deleteJob(jobKey);
		} catch (SchedulerException se) {
			LOG.error(se.getLocalizedMessage(), se);
		}
	}
}
