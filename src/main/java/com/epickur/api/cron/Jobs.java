package com.epickur.api.cron;

import static org.quartz.SimpleScheduleBuilder.simpleSchedule;

import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.epickur.api.entity.Order;
import com.epickur.api.entity.User;
import com.epickur.api.utils.Utils;

/**
 * The goal of this class is to run {@link CleanKeysJob} that clean the keys into MongodDB
 * 
 * @author cph
 * @version 1.0
 */
public final class Jobs {

	/** Logger */
	private static final Logger LOG = LogManager.getLogger(Jobs.class.getSimpleName());
	/** Properties */
	private final Properties prop;
	/** Jobs */
	private static Jobs jobs;
	/** Quartz scheduler */
	private Scheduler scheduler;
	/** Order max time */
	private int orderMaxTime;

	/**
	 * Concstruct a Jobs
	 * 
	 * @throws SchedulerException
	 *             If a SchedulerException occured
	 */
	private Jobs() throws SchedulerException {
		this.prop = Utils.getEpickurProperties();
		this.scheduler = new StdSchedulerFactory().getScheduler();
		this.orderMaxTime = Integer.parseInt(prop.getProperty("cron.order.timelimit"));
	}

	/**
	 * Get an instance of Jobs. Singleton
	 * 
	 * @return A Jobs instance
	 */
	public static Jobs getInstance() {
		try {
			if (jobs == null) {
				jobs = new Jobs();
			}
			return jobs;
		} catch (SchedulerException se) {
			LOG.error(se.getLocalizedMessage(), se);
			throw new RuntimeException("Can't instanciate the scheduler");
		}
	}

	/**
	 * Function that starts the cron job {@link CleanKeysJob}
	 * 
	 * @throws SchedulerException
	 *             If an error occurred while running jobs
	 */
	public void run() throws SchedulerException {
		final Integer interval = Integer.valueOf(prop.getProperty("cron.cleankeys.interval"));
		String identityKeys = "cleanKeys";
		// String identityMongoDB = "mongodb";
		JobDetail cleanKeys = JobBuilder.newJob(CleanKeysJob.class).withIdentity(identityKeys).build();
		Trigger triggerCleanKeys = TriggerBuilder.newTrigger().withIdentity(identityKeys)
				.withSchedule(simpleSchedule()
						.withIntervalInMinutes(interval.intValue())
						.repeatForever()).build();
		scheduler.scheduleJob(cleanKeys, triggerCleanKeys);
		LOG.info("Added job '" + identityKeys + "' to scheduler");
		/*
		 * JobDetail mongoDBDump = JobBuilder.newJob(MongoDBDumpJob.class).withIdentity(identityMongoDB).build(); Trigger triggerMongoDBDump =
		 * TriggerBuilder.newTrigger().withIdentity(identityMongoDB) .withSchedule(simpleSchedule() .withIntervalInMinutes(5)
		 * .repeatForever()).build(); scheduler.scheduleJob(mongoDBDump, triggerMongoDBDump); LOG.info("Added job '" + identityMongoDB +
		 * "' to scheduler");
		 */
		scheduler.start();
		LOG.info("Scheduler started ");
	}

	/**
	 * Add a temporary order job
	 * 
	 * @param user
	 *            The User
	 * @param order
	 *            The Order
	 */
	public void addTemporaryOrderJob(final User user, final Order order) {
		String userId = user.getId().toHexString();
		String orderId = order.getId().toHexString();
		DateTime orderDate = order.getCreatedAt();
		DateTime scheduleCancelDate = orderDate.plusMinutes(orderMaxTime);
		String identity = "cancelOrder_" + orderId;
		JobDetail cancelOrder = JobBuilder.newJob(CancelOrderJob.class)
				.withIdentity(identity)
				.usingJobData("orderId", orderId)
				.usingJobData("userId", userId)
				.build();
		Trigger triggerCancelOrder = TriggerBuilder.newTrigger()
				.withIdentity(identity)
				.startAt(scheduleCancelDate.toDate())
				.build();
		try {
			scheduler.scheduleJob(cancelOrder, triggerCancelOrder);
			LOG.info("Added job '" + identity + "' to the scheduler with orderId " + orderId + " and userId " + userId);
		} catch (SchedulerException se) {
			LOG.error(se.getLocalizedMessage(), se);
		}
	}

	/**
	 * Remove a temporary order job
	 * 
	 * @param orderId
	 *            The order Id
	 */
	public void removeTemporaryOrderJob(final String orderId) {
		String identity = "cancelOrder_" + orderId;
		LOG.info("Remove job '" + identity + "' from the scheduler");
		JobKey jobKey = new JobKey(identity);
		try {
			scheduler.deleteJob(jobKey);
		} catch (SchedulerException se) {
			LOG.error(se.getLocalizedMessage(), se);
		}
	}
}
