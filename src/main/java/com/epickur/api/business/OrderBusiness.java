package com.epickur.api.business;

import java.util.List;

import org.bson.types.ObjectId;
import org.joda.time.DateTime;

import com.epickur.api.cron.Jobs;
import com.epickur.api.dao.mongo.OrderDAOImpl;
import com.epickur.api.dao.mongo.SequenceDAOImpl;
import com.epickur.api.dao.mongo.UserDAOImpl;
import com.epickur.api.entity.Key;
import com.epickur.api.entity.Order;
import com.epickur.api.entity.User;
import com.epickur.api.entity.Voucher;
import com.epickur.api.enumeration.Operation;
import com.epickur.api.enumeration.OrderStatus;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.exception.EpickurForbiddenException;
import com.epickur.api.exception.EpickurNotFoundException;
import com.epickur.api.payment.stripe.StripePayment;
import com.epickur.api.utils.ErrorUtils;
import com.epickur.api.utils.Security;
import com.epickur.api.utils.email.EmailUtils;
import com.epickur.api.validator.FactoryValidator;
import com.epickur.api.validator.UserValidator;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;

/**
 * Order business layer. Access Order and User DAO layer and execute logic.
 * 
 * @author cph
 * @version 1.0
 */
public class OrderBusiness {

	/** Order dao */
	private OrderDAOImpl orderDAO;
	/** User dao */
	private UserDAOImpl userDAO;
	/** Sequence Order dao */
	private SequenceDAOImpl seqDAO;
	/** Voucher dao */
	private VoucherBusiness voucherBusiness;
	/** User validator */
	private UserValidator validator;
	/** User Email utils */
	private EmailUtils emailUtils;

	/** The constructor */
	public OrderBusiness() {
		this.orderDAO = new OrderDAOImpl();
		this.userDAO = new UserDAOImpl();
		this.seqDAO = new SequenceDAOImpl();
		this.voucherBusiness = new VoucherBusiness();
		this.validator = (UserValidator) FactoryValidator.getValidator("user");
		this.emailUtils = new EmailUtils();
	}

	public OrderBusiness(final OrderDAOImpl orderDAO, final UserDAOImpl userDAO, final SequenceDAOImpl seqDAO,
			final VoucherBusiness voucherBusiness, final EmailUtils emailUtils) {
		this.orderDAO = orderDAO;
		this.userDAO = userDAO;
		this.seqDAO = seqDAO;
		this.voucherBusiness = voucherBusiness;
		this.validator = (UserValidator) FactoryValidator.getValidator("user");
		this.emailUtils = emailUtils;
	}

	/**
	 * Create an Order
	 * 
	 * @param userId
	 *            The user id
	 * @param order
	 *            The order
	 * @param sendEmail
	 *            True if you want send all emails
	 * @return an Order
	 * @throws EpickurException
	 *             If an EpickurException occurred
	 */
	public Order create(final String userId, final Order order, final boolean sendEmail)
			throws EpickurException {
		User user = this.userDAO.read(userId);
		if (user == null) {
			throw new EpickurNotFoundException(ErrorUtils.USER_NOT_FOUND, userId);
		}
		Voucher current = order.getVoucher();
		if (current != null) {
			Voucher updated = this.voucherBusiness.validateVoucher(current.getCode());
			order.setVoucher(updated);
		}
		order.setCreatedBy(new ObjectId(userId));
		order.setStatus(OrderStatus.PENDING);
		String sequence = this.seqDAO.getNextId();
		order.setReadableId(sequence);
		order.prepareForInsertionIntoDB();
		Order res = this.orderDAO.create(order);
		String orderCode = Security.createOrderCode(res.getId(), order.getCardToken());
		if (sendEmail) {
			this.emailUtils.emailNewOrder(user, res, orderCode);
		}
		Jobs.getInstance().addTemporaryOrderJob(user, res);
		return res;
	}

	/**
	 * @param id
	 *            The Order id
	 * @param key
	 *            The key
	 * @return An Order
	 * @throws EpickurException
	 *             If an ${@link EpickurException} occurred
	 */
	public Order read(final String id, final Key key) throws EpickurException {
		Order order = this.orderDAO.read(id);
		if (order != null) {
			this.validator.checkOrderRightsAfter(key.getRole(), key.getUserId(), order, Operation.READ);
			return order;
		}
		return null;
	}

	/**
	 * @param userId
	 *            The user id
	 * @return a list of Order
	 * @throws EpickurException
	 *             If an ${@link EpickurException} occurred
	 */
	public List<Order> readAllWithUserId(final String userId) throws EpickurException {
		return this.orderDAO.readAllWithUserId(userId);
	}

	/**
	 * @param catererId
	 *            The Caterer Id
	 * @param start
	 *            The start date
	 * @param end
	 *            The end date
	 * @return A list of Order
	 * @throws EpickurException
	 *             If an ${@link EpickurException} occurred
	 */
	public List<Order> readAllWithCatererId(final String catererId, final DateTime start, final DateTime end) throws EpickurException {
		return this.orderDAO.readAllWithCatererId(catererId, start, end);
	}

	/**
	 * @param order
	 *            The Order
	 * @param key
	 *            The key
	 * @return The updated Order
	 * @throws EpickurException
	 *             If an ${@link EpickurException} occurred
	 */
	public Order update(final Order order, final Key key) throws EpickurException {
		Order read = this.orderDAO.read(order.getId().toHexString());
		if (read != null) {
			this.validator.checkOrderRightsAfter(key.getRole(), key.getUserId(), read, Operation.UPDATE);
			if (read.getStatus() != OrderStatus.PENDING) {
				throw new EpickurException("It's not allowed to modify an order that has a " + order.getStatus() + " status");
			}
			order.prepareForUpdateIntoDB();
			return this.orderDAO.update(order);
		}
		return null;
	}

	/**
	 * @param id
	 *            The Order id
	 * @return True if The Order has been deleted
	 * @throws EpickurException
	 *             If an EpickurException occurred
	 */
	public boolean delete(final String id) throws EpickurException {
		return this.orderDAO.delete(id);
	}

	/**
	 * Execute order. Confirm or Cancel the order.
	 * 
	 * @param userId
	 *            The User id
	 * @param orderId
	 *            The Order id
	 * @param confirm
	 *            If the caterer confirmed the order
	 * @param sendEmail
	 *            If we want to send the emails
	 * @param shouldCharge
	 *            If we charge the user
	 * @param orderCode
	 *            The order code
	 * @return The Updated order
	 * @throws EpickurException
	 *             If an EpickurException occurred
	 */
	public Order executeOrder(final String userId, final String orderId, final boolean confirm, final boolean sendEmail,
			final boolean shouldCharge, final String orderCode) throws EpickurException {
		User user = this.userDAO.read(userId);
		if (user == null) {
			throw new EpickurNotFoundException(ErrorUtils.USER_NOT_FOUND, userId);
		}
		Order order = this.orderDAO.read(orderId);
		if (order == null) {
			throw new EpickurNotFoundException(ErrorUtils.ORDER_NOT_FOUND, orderId);
		}
		if (!orderCode.equals(Security.createOrderCode(new ObjectId(orderId), order.getCardToken()))) {
			throw new EpickurForbiddenException();
		}
		if (confirm) {
			if (shouldCharge) {
				StripePayment stripe = new StripePayment();
				try {
					Charge charge = stripe.chargeCard(order.getCardToken(), order.calculateTotalAmount(), order.getCurrency());
					if (charge == null || !charge.getPaid()) {
						orderFailed(order, user, sendEmail);
					} else {
						order.setChargeId(charge.getId());
						order.setPaid(charge.getPaid());
						order.setStatus(OrderStatus.SUCCESSFUL);
						// Send email to User, Caterer and admins - Order success
						if (sendEmail) {
							this.emailUtils.emailSuccessOrder(user, order);
						}
					}
				} catch (StripeException e) {
					orderFailed(order, user, sendEmail);
				} finally {
					order.prepareForUpdateIntoDB();
					order = orderDAO.update(order);
				}
			}
		} else {
			// Send email to USER and ADMINS - Order decline
			if (sendEmail) {
				this.emailUtils.emailDeclineOrder(user, order);
			}
			if (order.getVoucher() != null) {
				Voucher voucher = this.voucherBusiness.revertVoucher(order.getVoucher().getCode());
				order.setVoucher(voucher);
			}
			order.setStatus(OrderStatus.DECLINED);
			order = this.orderDAO.update(order);
		}
		Jobs.getInstance().removeTemporaryOrderJob(orderId);
		return order;
	}

	/**
	 * @param order
	 *            The Order
	 * @param user
	 *            The User
	 * @param sendEmail
	 *            If we send an email
	 * @throws EpickurException
	 *             If an EpickurException occurred
	 */
	protected void orderFailed(final Order order, final User user, final boolean sendEmail) throws EpickurException {
		order.setPaid(false);
		order.setStatus(OrderStatus.FAILED);
		// Send email to User, Caterer and admins - Order failed
		if (sendEmail) {
			this.emailUtils.emailFailOrder(user, order);
		}
		if (order.getVoucher() != null) {
			Voucher voucher = this.voucherBusiness.revertVoucher(order.getVoucher().getCode());
			order.setVoucher(voucher);
		}
	}
}
