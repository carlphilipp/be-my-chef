package com.epickur.api.cron;

import com.epickur.api.dao.mongo.OrderDAO;
import com.epickur.api.dao.mongo.UserDAO;
import com.epickur.api.entity.Order;
import com.epickur.api.entity.User;
import com.epickur.api.enumeration.OrderStatus;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.service.VoucherService;
import com.epickur.api.utils.email.EmailUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

/**
 * This class represents a process to cancel orders when it's been too long time it was not accepted.
 *
 * @author cph
 * @version 1.0
 */
@Component
public class CancelOrderJob extends QuartzJobBean {

	/**
	 * Logger
	 */
	private static final Logger LOG = LogManager.getLogger(CancelOrderJob.class.getSimpleName());
	/**
	 * Order dao
	 */
	@Autowired
	private OrderDAO orderDAO;
	/**
	 * User dao
	 */
	@Autowired
	private UserDAO userDAO;
	/**
	 * Voucher Business
	 */
	@Autowired
	private VoucherService voucherService;
	/**
	 * Email utils
	 */
	@Autowired
	private EmailUtils emailUtils;

	@Override
	protected void executeInternal(final JobExecutionContext context) throws JobExecutionException {
		try {
			String orderId = context.getJobDetail().getJobDataMap().getString("orderId");
			Order order = orderDAO.read(orderId);
			String userId = context.getJobDetail().getJobDataMap().getString("userId");
			User user = userDAO.read(userId);
			if (user != null && order != null) {
				LOG.info("Cancel order id: " + orderId + " with user id: " + userId);
				order.setStatus(OrderStatus.CANCELED);
				order.prepareForUpdateIntoDB();
				order = orderDAO.update(order);
				if (order.getVoucher() != null) {
					voucherService.revertVoucher(order.getVoucher().getCode());
				}
				emailUtils.emailCancelOrder(user, order);
			}
		} catch (EpickurException e) {
			LOG.error(e.getLocalizedMessage(), e);
		}
	}
}
