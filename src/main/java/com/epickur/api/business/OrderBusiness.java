package com.epickur.api.business;

import java.util.List;

import org.bson.types.ObjectId;
import org.joda.time.DateTime;

import com.epickur.api.cron.Jobs;
import com.epickur.api.dao.mongo.OrderDaoImpl;
import com.epickur.api.dao.mongo.SequenceDaoImpl;
import com.epickur.api.dao.mongo.UserDaoImpl;
import com.epickur.api.entity.Key;
import com.epickur.api.entity.Order;
import com.epickur.api.entity.User;
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
public final class OrderBusiness {

	/** Order dao */
	private OrderDaoImpl orderDao;
	/** User dao */
	private UserDaoImpl userDao;
	/** Sequence Order dao */
	private SequenceDaoImpl seqDao;
	/** User validator */
	private UserValidator validator;

	/** The constructor */
	public OrderBusiness() {
		this.orderDao = new OrderDaoImpl();
		this.userDao = new UserDaoImpl();
		this.seqDao = new SequenceDaoImpl();
		this.validator = (UserValidator) FactoryValidator.getValidator("user");
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
		User user = this.userDao.read(userId);
		if (user == null) {
			throw new EpickurNotFoundException(ErrorUtils.USER_NOT_FOUND, userId);
		} else {
			order.setCreatedBy(new ObjectId(userId));
			order.setStatus(OrderStatus.PENDING);
			String sequence = seqDao.getNextId();
			order.setReadableId(sequence);
			Order res = this.orderDao.create(order);
			String orderCode = Security.createOrderCode(res.getId(), order.getCardToken());
			if (sendEmail) {
				EmailUtils.emailNewOrder(user, res, orderCode);
			}
			Jobs.getInstance().addTemporaryOrderJob(user, res);
			return res;
		}
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
		Order order = orderDao.read(id);
		if (order != null) {
			validator.checkOrderRightsAfter(key.getRole(), key.getUserId(), order, Operation.READ);
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
		return orderDao.readAllWithUserId(userId);
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
		return orderDao.readAllWithCatererId(catererId, start, end);
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
		Order read = orderDao.read(order.getId().toHexString());
		if (read != null) {
			validator.checkOrderRightsAfter(key.getRole(), key.getUserId(), read, Operation.UPDATE);
			if (read.getStatus() != OrderStatus.PENDING) {
				throw new EpickurException("It's not allowed to modify an order that has a " + order.getStatus() + " status");
			}
			return orderDao.update(order);
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
		return orderDao.delete(id);
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
		User user = userDao.read(userId);
		if (user == null) {
			throw new EpickurNotFoundException(ErrorUtils.USER_NOT_FOUND, userId);
		} else {
			Order order = orderDao.read(orderId);
			if (order != null) {
				if (!orderCode.equals(Security.createOrderCode(new ObjectId(orderId), order.getCardToken()))) {
					throw new EpickurForbiddenException();
				}
				if (confirm) {
					if (shouldCharge) {
						StripePayment stripe = new StripePayment();
						try {
							Charge charge = stripe.chargeCard(order.getCardToken(), order.getAmount(), order.getCurrency());
							if (charge == null || !charge.getPaid()) {
								order.setPaid(false);
								order.setStatus(OrderStatus.FAILED);
								// Send email to User, Caterer and admins - Order fail
								if (sendEmail) {
									EmailUtils.emailFailOrder(user, order);
								}
							} else {
								order.setChargeId(charge.getId());
								order.setPaid(charge.getPaid());
								order.setStatus(OrderStatus.SUCCESSFUL);
								// Send email to User, Caterer and admins - Order success
								if (sendEmail) {
									EmailUtils.emailSuccessOrder(user, order);
								}
							}
						} catch (StripeException e) {
							order.setPaid(false);
							order.setStatus(OrderStatus.FAILED);
							// Send email to User, Caterer and admins - Order fail
							if (sendEmail) {
								EmailUtils.emailFailOrder(user, order);
							}
						} finally {
							order = orderDao.update(order);
						}
					}
				} else {
					// Send email to USER and ADMINS - Order decline
					if (sendEmail) {
						EmailUtils.emailDeclineOrder(user, order);
					}
					order.setStatus(OrderStatus.DECLINED);
					order = orderDao.update(order);
				}
				Jobs.getInstance().removeTemporaryOrderJob(orderId);
				return order;
			} else {
				throw new EpickurNotFoundException(ErrorUtils.ORDER_NOT_FOUND, orderId);
			}
		}
	}
}
