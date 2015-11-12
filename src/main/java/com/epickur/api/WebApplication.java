package com.epickur.api;

import javax.annotation.PostConstruct;
import javax.ws.rs.ApplicationPath;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.quartz.SchedulerException;

import com.epickur.api.cron.Jobs;

/**
 * Resource configuration that starts {@link Jobs}
 * 
 * @author cph
 * @version 1.0
 */
@ApplicationPath("/api")
public class WebApplication extends ResourceConfig {

	/** Logger */
	private static final Logger LOG = LogManager.getLogger(WebApplication.class.getSimpleName());

	/** Constructor */
	public WebApplication() {
		property(ServerProperties.APPLICATION_NAME, "epickur");
		property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true);
		property(ServerProperties.TRACING, "ALL");
		property(ServerProperties.TRACING_THRESHOLD, "SUMMARY");	
		
		// Quartz
		property("quartz:shutdown-on-unload", true);
		property("quartz:wait-on-shutdown", true);
		property("quartz:start-scheduler-on-load", true);
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
