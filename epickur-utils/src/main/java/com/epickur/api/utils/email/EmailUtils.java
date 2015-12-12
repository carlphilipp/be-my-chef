package com.epickur.api.utils.email;

import com.epickur.api.entity.Order;
import com.epickur.api.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Class that handle static method that send emails
 * 
 * @author cph
 * @version 1.0
 *
 */
@Component
public class EmailUtils {

	@Autowired
	private EmailTemplate emailTemplate;

	@Autowired
	private Email email;

	@Value("${admins}")
	private String[] admins;

	// Registration
	/**
	 * @param user
	 *            The user
	 * @param code
	 *            The code
	 */
	public void emailNewRegistration(final User user, final String code) {
		emailNewRegistrationUser(user, code);
		emailNewRegistrationAdmin(user);
	}

	/**
	 * @param name
	 *            The user name
	 * @param first
	 *            The user first name
	 * @param code
	 *            The code
	 * @param email
	 *            The email
	 */
	private void emailNewRegistrationUser(final User user, String code) {
		// Convert data to use email template
		Map<String, String> emailData = emailTemplate.convertToDataNewRegistrationUser(user, code);
		// Send an email to the user
		this.email.sendMail(EmailType.REGISTRATION_USER, emailData, new String[] { user.getEmail() });
	}

	/**
	 * @param name
	 *            The user name
	 * @param email
	 *            The user email
	 */
	private void emailNewRegistrationAdmin(final User user) {
		// Convert data to use email template
		Map<String, String> emailDataAdmin = emailTemplate.convertToDataNewRegistrationAdmins(user);
		// Send an email to admins
		this.email.sendMail(EmailType.REGISTRATION_ADMIN, emailDataAdmin, admins);
	}

	// ORDER: case 1 - New order
	/**
	 * @param user
	 *            The user name
	 * @param order
	 *            The order
	 * @param orderCode
	 *            The order code
	 */
	public void emailNewOrder(final User user, final Order order, final String orderCode) {
		emailNewOrderUser(user, order);
		emailNewOrderCaterer(user, order, orderCode);
		emailNewOrderAdmin(user, order);
	}

	/**
	 * @param user
	 *            The user
	 * @param order
	 *            The order
	 */
	private void emailNewOrderUser(final User user, final Order order) {
		Map<String, String> emailData = emailTemplate.convertToDataNewOrderUser(user, order);
		this.email.sendMail(EmailType.ORDER_USER_NEW, emailData, new String[] { user.getEmail() });
	}

	/**
	 * @param user
	 *            The user
	 * @param order
	 *            The order
	 * @param orderCode
	 *            The order code
	 */
	private void emailNewOrderCaterer(final User user, final Order order, final String orderCode) {
		Map<String, String> emailData = emailTemplate.convertToDataNewOrderCaterer(user, order, orderCode);
		this.email.sendMail(EmailType.ORDER_CATERER_NEW, emailData, new String[] { order.getDish().getCaterer().getEmail() });
	}

	/**
	 * @param user
	 *            The user
	 * @param order
	 *            The order
	 */
	private void emailNewOrderAdmin(final User user, final Order order) {
		Map<String, String> emailData = emailTemplate.convertToDataNewOrderAdmins(user, order);
		this.email.sendMail(EmailType.ORDER_ADMINS_NEW, emailData, admins);
	}

	// ORDER: case 2 - Caterer declined the order
	/**
	 * @param user
	 *            The user
	 * @param order
	 *            The order
	 */
	public void emailDeclineOrder(final User user, final Order order) {
		emailDeclineOrderUser(user, order);
		emailDeclineOrderAdmins(user, order);
	}

	/**
	 * @param user
	 *            The user
	 * @param order
	 *            The order
	 */
	private void emailDeclineOrderUser(final User user, final Order order) {
		Map<String, String> emailData = emailTemplate.convertToDataDeclineOrderUser(user, order);
		this.email.sendMail(EmailType.ORDER_USER_DECLINED, emailData, new String[] { user.getEmail() });
	}

	/**
	 * @param user
	 *            The user
	 * @param order
	 *            The order
	 */
	private void emailDeclineOrderAdmins(final User user, final Order order) {
		Map<String, String> emailData = emailTemplate.convertToDataDeclineOrderAdmins(user, order);
		this.email.sendMail(EmailType.ORDER_ADMINS_DECLINED, emailData, admins);
	}

	// ORDER: case 3 - The order is a success
	/**
	 * @param user
	 *            The user
	 * @param order
	 *            The order
	 */
	public void emailSuccessOrder(final User user, final Order order) {
		emailSuccessOrderUser(user, order);
		emailSuccessOrderCaterer(user, order);
		emailSuccessOrderAdmins(user, order);
	}

	/**
	 * @param user
	 *            The user
	 * @param order
	 *            The order
	 */
	private void emailSuccessOrderUser(final User user, final Order order) {
		Map<String, String> emailData = emailTemplate.convertToDataSuccessOrderUser(user, order);
		this.email.sendMail(EmailType.ORDER_USER_SUCCESS, emailData, new String[] { user.getEmail() });
	}

	/**
	 * @param user
	 *            The user
	 * @param order
	 *            The order
	 */
	private void emailSuccessOrderCaterer(final User user, final Order order) {
		Map<String, String> emailData = emailTemplate.convertToDataSuccessOrderCaterer(user, order);
		this.email.sendMail(EmailType.ORDER_CATERER_SUCCESS, emailData, new String[] { order.getDish().getCaterer().getEmail() });
	}

	/**
	 * @param user
	 *            The user
	 * @param order
	 *            The order
	 */
	private void emailSuccessOrderAdmins(final User user, final Order order) {
		Map<String, String> emailData = emailTemplate.convertToDataSuccessOrderAdmins(user, order);
		this.email.sendMail(EmailType.ORDER_ADMINS_SUCCESS, emailData, admins);
	}

	// ORDER: case 4 - The order has been accepted but the payment failed
	/**
	 * @param user
	 *            The user
	 * @param order
	 *            The order
	 */
	public void emailFailOrder(final User user, final Order order) {
		emailFailOrderUser(user, order);
		emailFailOrderCaterer(user, order);
		emailFailOrderAdmins(user, order);
	}

	/**
	 * @param user
	 *            The user
	 * @param order
	 *            The order
	 */
	private void emailFailOrderUser(final User user, final Order order) {
		Map<String, String> emailData = emailTemplate.convertToDataDeclineOrderUser(user, order);
		this.email.sendMail(EmailType.ORDER_USER_FAIL, emailData, new String[] { user.getEmail() });
	}

	/**
	 * @param user
	 *            The user
	 * @param order
	 *            The order
	 */
	private void emailFailOrderCaterer(final User user, final Order order) {
		Map<String, String> emailData = emailTemplate.convertToDataFailOrderCaterer(user, order);
		this.email.sendMail(EmailType.ORDER_CATERER_FAIL, emailData, new String[] { order.getDish().getCaterer().getEmail() });
	}

	/**
	 * @param user
	 *            The user
	 * @param order
	 *            The order
	 */
	private void emailFailOrderAdmins(final User user, final Order order) {
		Map<String, String> emailData = emailTemplate.convertToDataFailOrderAdmins(user, order);
		this.email.sendMail(EmailType.ORDER_ADMINS_FAIL, emailData, admins);
	}

	// ORDER: case 5 - The order has been received by the Caterer, but he did not answer it on time.
	/**
	 * @param user
	 *            The user
	 * @param order
	 *            The order
	 */
	public void emailCancelOrder(final User user, final Order order) {
		emailCancelOrderUser(user, order);
		emailCancelOrderCaterer(user, order);
		emailCancelOrderAdmins(user, order);
	}

	/**
	 * @param user
	 *            The user
	 * @param order
	 *            The order
	 */
	private void emailCancelOrderUser(final User user, final Order order) {
		Map<String, String> emailData = emailTemplate.convertToDataCancelOrderUser(user, order);
		this.email.sendMail(EmailType.ORDER_USER_CANCEL, emailData, new String[] { user.getEmail() });
	}

	/**
	 * @param user
	 *            The user
	 * @param order
	 *            The order
	 */
	private void emailCancelOrderCaterer(final User user, final Order order) {
		Map<String, String> emailData = emailTemplate.convertToDataCancelOrderCaterer(user, order);
		this.email.sendMail(EmailType.ORDER_CATERER_CANCEL, emailData, new String[] { order.getDish().getCaterer().getEmail() });
	}

	/**
	 * @param user
	 *            The user
	 * @param order
	 *            The order
	 */
	private void emailCancelOrderAdmins(final User user, final Order order) {
		Map<String, String> emailData = emailTemplate.convertToDataCancelOrderAdmins(user, order);
		this.email.sendMail(EmailType.ORDER_ADMINS_CANCEL, emailData, admins);
	}

	/**
	 * @param user
	 *            The user.
	 * @param resetCode
	 *            The reset code.
	 */
	public void resetPassword(final User user, final String resetCode) {
		Map<String, String> emailData = emailTemplate.convertToDataResetUserPassword(user, resetCode);
		this.email.sendMail(EmailType.RESET_USER_PASSWORD, emailData, new String[] { user.getEmail() });
	}
}
