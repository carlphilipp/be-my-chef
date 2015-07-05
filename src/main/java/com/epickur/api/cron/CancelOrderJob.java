package com.epickur.api.cron;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.epickur.api.dao.mongo.OrderDaoImpl;
import com.epickur.api.dao.mongo.UserDaoImpl;
import com.epickur.api.entity.Order;
import com.epickur.api.entity.User;
import com.epickur.api.enumeration.OrderStatus;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.utils.email.EmailUtils;

/**
 * This class represents a process to cancel orders when it's been too long time it was not accepted.
 * 
 * @author cph
 * @version 1.0
 *
 */
public final class CancelOrderJob implements Job {

	/** Logger */
	private static final Logger LOG = LogManager.getLogger(CancelOrderJob.class.getSimpleName());
	/** Order dao */
	private OrderDaoImpl orderDao;
	/** User dao */
	private UserDaoImpl userDao;

	/**
	 * Constructs a Cancel Order Job
	 */
	public CancelOrderJob() {
		this.orderDao = new OrderDaoImpl();
		this.userDao = new UserDaoImpl();
	}

	@Override
	public void execute(final JobExecutionContext context) throws JobExecutionException {
		try {
			String orderId = context.getJobDetail().getJobDataMap().getString("orderId");
			Order order = orderDao.read(orderId);
			String userId = context.getJobDetail().getJobDataMap().getString("userId");
			User user = userDao.read(userId);
			if (user != null && order != null) {
				LOG.info("Cancel order id: " + orderId + " with user id: " + userId);
				order.setStatus(OrderStatus.CANCELED);
				order = orderDao.update(order);
				EmailUtils.emailCancelOrder(user, order);
			}
		} catch (EpickurException e) {
			LOG.error(e.getLocalizedMessage(), e);
		}
	}
}
