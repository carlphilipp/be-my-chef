package com.epickur.api.cron;

import com.epickur.api.dao.mongo.OrderDAO;
import com.epickur.api.dao.mongo.UserDAO;
import com.epickur.api.entity.Order;
import com.epickur.api.entity.User;
import com.epickur.api.enumeration.OrderStatus;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.service.VoucherService;
import com.epickur.api.utils.email.EmailUtils;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * This class represents a process to cancel orders when it's been too long time it was not accepted.
 *
 * @author cph
 * @version 1.0
 */
@Slf4j
@Component
public class CancelOrderJob extends QuartzJobBean {

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
			final String orderId = context.getJobDetail().getJobDataMap().getString("orderId");
			final String userId = context.getJobDetail().getJobDataMap().getString("userId");

			final Optional<Order> orderOptional = orderDAO.read(orderId);
			final Optional<User> userOptional = userDAO.read(userId);

			if (userOptional.isPresent() && orderOptional.isPresent()) {
				log.info("Cancel order id: {} with user id: {}", orderId, userId);
				Order order = orderOptional.get();
				order.setStatus(OrderStatus.CANCELED);
				order.prepareForUpdateIntoDB();
				order = orderDAO.update(order);
				if (order.getVoucher() != null) {
					voucherService.revertVoucher(order.getVoucher().getCode());
				}

				final User user = userOptional.get();
				emailUtils.emailCancelOrder(user, order);
			} else {
				log.warn("Could not cancel order '" + orderId + "' for user '" + userId + "' - '" + orderOptional + "' '" + userOptional + "'");
			}
		} catch (final EpickurException e) {
			log.error(e.getLocalizedMessage(), e);
		}
	}
}
