package com.epickur.api.cron;

import com.epickur.api.dao.mongo.OrderDAO;
import com.epickur.api.dao.mongo.UserDAO;
import com.epickur.api.dao.mongo.VoucherDAO;
import com.epickur.api.entity.Order;
import com.epickur.api.entity.User;
import com.epickur.api.entity.Voucher;
import com.epickur.api.enumeration.OrderStatus;
import com.epickur.api.enumeration.voucher.ExpirationType;
import com.epickur.api.enumeration.voucher.Status;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.utils.email.EmailUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * This class represents a process to cancel orders when it's been too long time it was not accepted.
 *
 * @author cph
 * @version 1.0
 */
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
	private VoucherDAO voucherDAO;
	/**
	 * Email utils
	 */
	private EmailUtils emailUtils;

	/**
	 * Constructs a Cancel Order Job
	 */
	public CancelOrderJob() {
		emailUtils = new EmailUtils();
	}

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
					revertVoucher(order.getVoucher().getCode());
				}
				emailUtils.emailCancelOrder(user, order);
			}
		} catch (EpickurException e) {
			LOG.error(e.getLocalizedMessage(), e);
		}
	}

	// TODO: duplicated method with the one in voucher service. Need to be merged when scheduler will be managed by Spring
	public void revertVoucher(final String code) throws EpickurException {
		Voucher found = voucherDAO.read(code);
		if (found == null) {
			throw new EpickurException("Voucher '" + code + "' not found");
		}
		if (found.getExpirationType() == ExpirationType.ONETIME) {
			found.setStatus(Status.VALID);
		} else if (found.getExpirationType() == ExpirationType.UNTIL) {
			found.setUsedCount(found.getUsedCount() - 1);
		}
		found.prepareForUpdateIntoDB();
		voucherDAO.update(found);
	}
}
