package com.epickur.api.cron;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.epickur.api.dao.mongo.OrderDaoImpl;

public final class CancelOrderJob implements Job {

	/** Logger **/
	private static final Logger LOG = LogManager.getLogger(CancelOrderJob.class.getSimpleName());

	private OrderDaoImpl orderDao;
	
	public CancelOrderJob(){
		this.orderDao = new OrderDaoImpl();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 */
	@Override
	public void execute(final JobExecutionContext context) throws JobExecutionException {
		String orderId = context.getJobDetail().getJobDataMap().getString("orderId");
		LOG.info("Cancel order id: " + orderId);
	}
}
