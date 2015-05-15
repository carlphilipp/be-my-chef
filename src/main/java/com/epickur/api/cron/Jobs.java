package com.epickur.api.cron;

import static org.quartz.SimpleScheduleBuilder.simpleSchedule;

import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.epickur.api.utils.Utils;

/**
 * The goal of this class is to run {@link CleanKeysJob} that clean the keys into MongodDB
 * 
 * @author cph
 * @version 1.0
 */
public class Jobs {

	/** Logger **/
	private static final Logger LOG = LogManager.getLogger(Jobs.class.getSimpleName());
	/** Properties **/
	private final Properties prop;

	/** Constructor **/
	public Jobs() {
		LOG.info("Running Job class");
		this.prop = Utils.getEpickurProperties();
	}

	/**
	 * Function that starts the cron job {@link CleanKeysJob}
	 * 
	 * @throws SchedulerException
	 *             If an error occurred while running jobs
	 */
	public final void run() throws SchedulerException {
		try {
			final Integer interval = Integer.valueOf(prop.getProperty("cron.cleankeys.interval"));
			Scheduler scheduler = new StdSchedulerFactory().getScheduler();
			JobDetail cleanKeys = JobBuilder.newJob(CleanKeysJob.class).withIdentity("cleanKeys").build();
			Trigger triggerCleanKeys = TriggerBuilder.newTrigger().withIdentity("cleanKeys")
					.withSchedule(simpleSchedule().withIntervalInMinutes(interval.intValue()).repeatForever()).build();
			scheduler.scheduleJob(cleanKeys, triggerCleanKeys);
			scheduler.start();
		} catch (SchedulerException se) {
			LOG.error(se.getLocalizedMessage(), se);
		}
	}

}
