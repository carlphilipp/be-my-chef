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

public final class CancelOrderJob implements Job {

	/** Logger */
	private static final Logger LOG = LogManager.getLogger(CancelOrderJob.class.getSimpleName());
	/** Order dao */
	private OrderDaoImpl orderDao;
	/** User dao */
	private UserDaoImpl userDao;

	public CancelOrderJob() {
		this.orderDao = new OrderDaoImpl();
		this.userDao = new UserDaoImpl();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 */
	@Override
	public void execute(final JobExecutionContext context) throws JobExecutionException {
		try {
			String orderId = context.getJobDetail().getJobDataMap().getString("orderId");
			Order order = orderDao.read(orderId);
			String userId = context.getJobDetail().getJobDataMap().getString("userId");
			User user = userDao.read(userId);
			LOG.info("Cancel order id: " + orderId + " with user id: " + userId);
			if (user != null && order != null) {
				order.setStatus(OrderStatus.CANCELED);
				orderDao.update(order);
				EmailUtils.emailCancelOrder(user, order);
			}
		} catch (EpickurException e) {
			LOG.error(e.getLocalizedMessage(), e);
		}
	}
}
