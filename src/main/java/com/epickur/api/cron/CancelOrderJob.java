package com.epickur.api.cron;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.epickur.api.business.VoucherBusiness;
import com.epickur.api.dao.mongo.OrderDAOImpl;
import com.epickur.api.dao.mongo.UserDAOImpl;
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
	private OrderDAOImpl orderDAO;
	/** User dao */
	private UserDAOImpl userDAO;
	/** Voucher Business */
	private VoucherBusiness voucherBusiness;

	/**
	 * Constructs a Cancel Order Job
	 */
	public CancelOrderJob() {
		this.orderDAO = new OrderDAOImpl();
		this.userDAO = new UserDAOImpl();
	}

	@Override
	public void execute(final JobExecutionContext context) throws JobExecutionException {
		try {
			String orderId = context.getJobDetail().getJobDataMap().getString("orderId");
			Order order = this.orderDAO.read(orderId);
			String userId = context.getJobDetail().getJobDataMap().getString("userId");
			User user = this.userDAO.read(userId);
			if (user != null && order != null) {
				LOG.info("Cancel order id: " + orderId + " with user id: " + userId);
				order.setStatus(OrderStatus.CANCELED);
				order.prepareForUpdateIntoDB();
				order = this.orderDAO.update(order);
				if (order.getVoucher() != null) {
					this.voucherBusiness.revertVoucher(order.getVoucher().getCode());
				}
				EmailUtils.emailCancelOrder(user, order);
			}
		} catch (EpickurException e) {
			LOG.error(e.getLocalizedMessage(), e);
		}
	}
}
