package com.epickur.api.business;

import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.joda.time.DateTime;

import com.epickur.api.dao.mongo.OrderDaoImpl;
import com.epickur.api.dao.mongo.UserDaoImpl;
import com.epickur.api.entity.Key;
import com.epickur.api.entity.Order;
import com.epickur.api.entity.User;
import com.epickur.api.enumeration.Crud;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.exception.EpickurNotFoundException;
import com.epickur.api.payment.stripe.StripePayment;
import com.epickur.api.utils.ErrorUtils;
import com.epickur.api.utils.Info;
import com.epickur.api.utils.email.Email;
import com.epickur.api.utils.email.EmailTemplate;
import com.epickur.api.utils.email.EmailType;
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

	/** Order dao **/
	private OrderDaoImpl orderDao;
	/** User dao **/
	private UserDaoImpl userDao;
	/** User validator **/
	private UserValidator validator;

	/** The constructor **/
	public OrderBusiness() {
		this.orderDao = new OrderDaoImpl();
		this.userDao = new UserDaoImpl();
		this.validator = (UserValidator) FactoryValidator.getValidator("user");
	}

	/**
	 * Create an Order
	 * 
	 * @param userId
	 *            The user id
	 * @param order
	 *            The order
	 * @param cardToken
	 *            The Stripe card token
	 * @param shouldCharge
	 *            True if you actually want to charge through Stripe
	 * @param sendEmail
	 *            True if you want send all emails
	 * @return an Order
	 * @throws EpickurException
	 *             If an ${@link EpickurException} occurred
	 */
	public final Order create(final String userId, final Order order, final String cardToken, final boolean shouldCharge, final boolean sendEmail)
			throws EpickurException {
		User user = this.userDao.read(userId);
		if (user == null) {
			throw new EpickurNotFoundException(ErrorUtils.USER_NOT_FOUND, userId);
		} else {
			order.setCreatedBy(new ObjectId(userId));
			if (shouldCharge) {
				StripePayment stripe = new StripePayment();
				try {
					Charge charge = stripe.chargeCard(cardToken, order.getAmount(), order.getCurrency());
					if (charge == null) {
						order.setPaid(false);
					} else {
						order.setChargeId(charge.getId());
						order.setPaid(charge.getPaid());
					}
				} catch (StripeException e) {
					order.setPaid(false);
				}

			}
			Order res = this.orderDao.create(order);
			if (shouldCharge && sendEmail) {
				if (order.getPaid()) {
					// Everything went fine. Let's email folks

					// Convert data to use email template
					Map<String, String> emailData = EmailTemplate.convertToDataOrderUser(user.getName(), res.getId().toHexString(), order.getDish()
							.getName());
					// Send an email to The user
					Email.sendMail(EmailType.ORDER_USER, emailData, new String[] { user.getEmail() });

					// Convert data to use email template
					emailData = EmailTemplate.convertToDataOrderCaterer(user.getName(), res.getId().toHexString(), order.getDish().getName(), order
							.getDish().getCaterer().getName());
					// Send an email to The caterer
					Email.sendMail(EmailType.ORDER_CATERER, emailData, new String[] { order.getDish().getCaterer().getEmail() });

					// Convert data to use email template
					emailData = EmailTemplate.convertToDataOrderAdmin(user.getName(), res.getId().toHexString(), order.getDish().getName(), order
							.getDish().getCaterer().getName());
					// Send an email to The admins
					Email.sendMail(EmailType.ORDER_ADMIN, emailData, Info.admins.toArray(new String[Info.admins.size()]));
				} else {
					// Something went wrong. Let's email anyway

					// Convert data to use email template
					Map<String, String> emailData = EmailTemplate.convertToDataOrderUser(user.getName(), res.getId().toHexString(), order.getDish()
							.getName());
					// Send an email to The user
					Email.sendMail(EmailType.ORDER_FAIL_USER, emailData, new String[] { user.getEmail() });

					// Convert data to use email template
					emailData = EmailTemplate.convertToDataOrderAdmin(user.getName(), res.getId().toHexString(), order.getDish().getName(), order
							.getDish().getCaterer().getName());
					// Send an email to The admins
					Email.sendMail(EmailType.ORDER_FAIL_ADMIN, emailData, Info.admins.toArray(new String[Info.admins.size()]));
				}
			}
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
	public final Order read(final String id, final Key key) throws EpickurException {
		Order order = orderDao.read(id);
		if (order != null) {
			validator.checkOrderRightsAfter(key.getRole(), key.getUserId(), order, Crud.READ);
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
	public final List<Order> readAllWithUserId(final String userId) throws EpickurException {
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
	public final List<Order> readAllWithCatererId(final String catererId, final DateTime start, final DateTime end) throws EpickurException {
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
	public final Order update(final Order order, final Key key) throws EpickurException {
		Order read = orderDao.read(order.getId().toHexString());
		if (read != null) {
			validator.checkOrderRightsAfter(key.getRole(), key.getUserId(), read, Crud.UPDATE);
			return orderDao.update(order);
		}
		return null;
	}

	/**
	 * @param id
	 *            The Order id
	 * @return True if The Order has been deleted
	 * @throws EpickurException
	 *             If an ${@link EpickurException} occurred
	 */
	public final boolean delete(final String id) throws EpickurException {
		return orderDao.delete(id);
	}
}
