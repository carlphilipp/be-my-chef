package com.epickur.api;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.BSON;
import org.glassfish.jersey.server.ResourceConfig;
import org.quartz.SchedulerException;

import com.epickur.api.cron.Jobs;
import com.epickur.api.enumeration.DishType;
import com.epickur.api.utils.EnumTransformer;

/**
 * Resource configuration that starts {@link Jobs}
 * 
 * @author cph
 * @version 1.0
 */
public class WebApplication extends ResourceConfig {

	/** Logger **/
	private static final Logger LOG = LogManager.getLogger(WebApplication.class.getSimpleName());

	/** Constructor **/
	public WebApplication() {
		// Register new encoding to be able to serialize enum in MongoDB
		BSON.addEncodingHook(DishType.class, new EnumTransformer());
	}

	/**
	 * Method called whenever the application starts in the container.
	 */
	@PostConstruct
	public static void initialize() {
		try {
			Jobs jobs = Jobs.getInstance();
			jobs.run();
		} catch (SchedulerException e) {
			LOG.error(e.getLocalizedMessage(), e);
		}
	}
}
