package com.epickur.api.service;

import com.epickur.api.aop.ValidateComplexAccessRights;
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
import com.epickur.api.payment.stripe.StripePayment;
import com.epickur.api.utils.ErrorUtils;
import com.epickur.api.utils.Security;
import com.epickur.api.utils.email.EmailUtils;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
	 * Sequence Order dao
	 */
	@Autowired
	private SequenceDAO seqDAO;
	/**
	 * Voucher dao
	 */
	@Autowired
	private VoucherService voucherService;
	@Autowired
	private OrderJob jobs;
	/**
	 * User Email utils
	 */
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
	public Order create(final String userId, final Order order)
			throws EpickurException {
		User user = readUser(userId);
		handleVoucher(order);
		prepareOrder(order, userId);

		Order orderCreated = orderDAO.create(order);

		postCreation(orderCreated, user);
		return orderCreated;
	}

	protected void handleVoucher(final Order order) throws EpickurException {
		Voucher voucher = order.getVoucher();
		if (voucher != null) {
			Voucher updated = voucherService.validateVoucher(voucher.getCode());
			order.setVoucher(updated);
		}
	}

	protected void prepareOrder(final Order order, final String userId) throws EpickurDBException {
		addSequenceIdToOrder(order);
		order.setCreatedBy(new ObjectId(userId));
		order.prepareForInsertionIntoDB();
	}

	protected void postCreation(final Order order, final User user) throws EpickurException {
		String orderCode = Security.createOrderCode(order.getId(), order.getCardToken());
		emailUtils.emailNewOrder(user, order, orderCode);
		jobs.addTemporaryOrderJob(user, order);
	}

	protected void addSequenceIdToOrder(final Order order) throws EpickurDBException {
		String sequence = seqDAO.getNextId();
		order.setReadableId(sequence);
	}

	protected User readUser(final String userId) throws EpickurException {
		User user = userDAO.read(userId);
		if (user == null) {
			throw new EpickurNotFoundException(ErrorUtils.USER_NOT_FOUND, userId);
		}
		return user;
	}

	/**
	 * @param id The Order id
	 * @return An Order
	 * @throws EpickurException If an ${@link EpickurException} occurred
	 */
	@ValidateComplexAccessRights(operation = READ, type = ORDER)
	public Order readOrder(final String id) throws EpickurException {
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
			throw new EpickurNotFoundException(ErrorUtils.ORDER_NOT_FOUND, id);
		}
		return isDeleted;
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
	public Order executeOrder(final String userId, final String orderId, final boolean confirm,
			final boolean shouldCharge, final String orderCode) throws EpickurException {
		User user = readUser(userId);
		Order order = read(orderId);
		checkAutorization(orderCode, order);
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
			Charge charge = stripePayment.chargeCard(order.getCardToken(), order.calculateTotalAmount(), order.getCurrency());
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
		Order order = orderDAO.read(orderId);
		if (order == null) {
			throw new EpickurNotFoundException(ErrorUtils.ORDER_NOT_FOUND, orderId);
		}
		return order;
	}

	protected void checkAutorization(final String orderCode, final Order order) throws EpickurException {
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
			Voucher voucher = voucherService.revertVoucher(order.getVoucher().getCode());
			order.setVoucher(voucher);
		}
	}
}
