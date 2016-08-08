package com.epickur.api.service;

import com.epickur.api.annotation.ValidateComplexAccessRights;
import com.epickur.api.cron.OrderJob;
import com.epickur.api.dao.mongo.OrderDAO;
import com.epickur.api.dao.mongo.SequenceDAO;
import com.epickur.api.dao.mongo.UserDAO;
import com.epickur.api.entity.Order;
import com.epickur.api.entity.User;
import com.epickur.api.entity.Voucher;
import com.epickur.api.enumeration.OrderStatus;
import com.epickur.api.exception.EpickurDBException;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.exception.EpickurForbiddenException;
import com.epickur.api.exception.EpickurNotFoundException;
import com.epickur.api.stripe.StripePayment;
import com.epickur.api.utils.ErrorConstants;
import com.epickur.api.utils.email.EmailUtils;
import com.epickur.api.utils.security.Security;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.epickur.api.enumeration.EndpointType.ORDER;
import static com.epickur.api.enumeration.Operation.READ;
import static com.epickur.api.enumeration.Operation.UPDATE;

/**
 * Order business layer. Access Order and User DAO layer and execute logic.
 *
 * @author cph
 * @version 1.0
 */
@Service
public class OrderService {

	@Autowired
	private OrderDAO orderDAO;
	@Autowired
	private UserDAO userDAO;
	@Autowired
	private SequenceDAO seqDAO;
	@Autowired
	private VoucherService voucherService;
	@Autowired
	private OrderJob jobs;
	@Autowired
	private EmailUtils emailUtils;
	@Autowired
	private StripePayment stripePayment;

	/**
	 * Create an Order
	 *
	 * @param userId The user id
	 * @param order  The order
	 * @return an Order
	 * @throws EpickurException If an EpickurException occurred
	 */
	public Order create(final String userId, final Order order) throws EpickurException {
		final User user = readUser(userId);
		handleVoucher(order);
		prepareOrder(order, userId);

		final Order orderCreated = orderDAO.create(order);

		postCreation(orderCreated, user);
		return orderCreated;
	}

	protected void handleVoucher(final Order order) throws EpickurException {
		final Voucher voucher = order.getVoucher();
		if (voucher != null) {
			final Voucher updated = voucherService.validateVoucher(voucher.getCode());
			order.setVoucher(updated);
		}
	}

	protected void prepareOrder(final Order order, final String userId) throws EpickurDBException {
		addSequenceIdToOrder(order);
		order.setCreatedBy(new ObjectId(userId));
		order.prepareForInsertionIntoDB();
	}

	protected void postCreation(final Order order, final User user) {
		final String orderCode = Security.createOrderCode(order.getId(), order.getCardToken());
		emailUtils.emailNewOrder(user, order, orderCode);
		jobs.addTemporaryOrderJob(user, order);
	}

	protected void addSequenceIdToOrder(final Order order) throws EpickurDBException {
		final String sequence = seqDAO.getNextId();
		order.setReadableId(sequence);
	}

	protected User readUser(final String userId) throws EpickurException {
		return userDAO.read(userId).orElseThrow(() -> new EpickurNotFoundException(ErrorConstants.USER_NOT_FOUND, userId));
	}

	/**
	 * @param id The Order id
	 * @return An Order
	 * @throws EpickurException If an ${@link EpickurException} occurred
	 */
	@ValidateComplexAccessRights(operation = READ, type = ORDER)
	public Optional<Order> readOrder(final String id) throws EpickurException {
		return orderDAO.read(id);
	}

	/**
	 * @param userId The user id
	 * @return a list of Order
	 * @throws EpickurException If an ${@link EpickurException} occurred
	 */
	public List<Order> readAllWithUserId(final String userId) throws EpickurException {
		return orderDAO.readAllWithUserId(userId);
	}

	/**
	 * @param catererId The Caterer Id
	 * @param start     The start date
	 * @param end       The end date
	 * @return A list of Order
	 * @throws EpickurException If an ${@link EpickurException} occurred
	 */
	public List<Order> readAllWithCatererId(final String catererId, final DateTime start, final DateTime end) throws EpickurException {
		return orderDAO.readAllWithCatererId(catererId, start, end);
	}

	/**
	 * @param order The Order
	 * @return The updated Order
	 * @throws EpickurException If an ${@link EpickurException} occurred
	 */
	@ValidateComplexAccessRights(operation = UPDATE, type = ORDER)
	public Order update(final Order order) throws EpickurException {
		order.prepareForUpdateIntoDB();
		return orderDAO.update(order);
	}

	/**
	 * @param id The Order id
	 * @return True if The Order has been deleted
	 * @throws EpickurException If an EpickurException occurred
	 */
	public boolean delete(final String id) throws EpickurException {
		boolean isDeleted = orderDAO.delete(id);
		if (!isDeleted) {
			throw new EpickurNotFoundException(ErrorConstants.ORDER_NOT_FOUND, id);
		}
		return true;
	}

	/**
	 * Execute order. Confirm or Cancel the order.
	 *
	 * @param userId       The User id
	 * @param orderId      The Order id
	 * @param confirm      If the caterer confirmed the order
	 * @param shouldCharge If we charge the user
	 * @param orderCode    The order code
	 * @return The Updated order
	 * @throws EpickurException If an EpickurException occurred
	 */
	public Order executeOrder(final String userId, final String orderId, final boolean confirm, final boolean shouldCharge, final String orderCode) throws EpickurException {
		final User user = readUser(userId);
		Order order = read(orderId);
		checkAuthorization(orderCode, order);
		if (confirm) {
			if (shouldCharge) {
				order = chargeUser(order, user);
			}
		} else {
			// Send email to USER and ADMINS - Order decline
			emailUtils.emailDeclineOrder(user, order);
			if (order.getVoucher() != null) {
				Voucher voucher = voucherService.revertVoucher(order.getVoucher().getCode());
				order.setVoucher(voucher);
			}
			order.setStatus(OrderStatus.DECLINED);
			order = orderDAO.update(order);
		}
		jobs.removeTemporaryOrderJob(orderId);
		return order;
	}

	protected Order chargeUser(Order order, final User user) throws EpickurException {
		try {
			final Charge charge = stripePayment.chargeCard(order.getCardToken(), order.calculateTotalAmount(), order.getCurrency());
			if (charge == null || !charge.getPaid()) {
				handleOrderFail(order, user);
			} else {
				order.setChargeId(charge.getId());
				order.setPaid(charge.getPaid());
				order.setStatus(OrderStatus.SUCCESSFUL);
				// Send email to User, Caterer and admins - Order success
				emailUtils.emailSuccessOrder(user, order);
			}
		} catch (StripeException e) {
			handleOrderFail(order, user);
		} finally {
			order = updateOrderInDB(order);
		}
		return order;
	}

	protected Order updateOrderInDB(Order order) throws EpickurException {
		order.prepareForUpdateIntoDB();
		order = orderDAO.update(order);
		return order;
	}

	protected Order read(final String orderId) throws EpickurException {
		return orderDAO.read(orderId).orElseThrow(() -> new EpickurNotFoundException(ErrorConstants.ORDER_NOT_FOUND, orderId));
	}

	protected void checkAuthorization(final String orderCode, final Order order) {
		if (!orderCode.equals(Security.createOrderCode(order.getId(), order.getCardToken()))) {
			throw new EpickurForbiddenException();
		}
	}

	/**
	 * @param order The Order
	 * @param user  The User
	 * @throws EpickurException If an EpickurException occurred
	 */
	protected void handleOrderFail(final Order order, final User user) throws EpickurException {
		order.setPaid(false);
		order.setStatus(OrderStatus.FAILED);
		// Send email to User, Caterer and admins - Order failed
		emailUtils.emailFailOrder(user, order);
		if (order.getVoucher() != null) {
			final Voucher voucher = voucherService.revertVoucher(order.getVoucher().getCode());
			order.setVoucher(voucher);
		}
	}
}
