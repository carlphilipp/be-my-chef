package com.epickur.api.utils.email;

import java.util.Map;

import com.epickur.api.entity.Order;
import com.epickur.api.entity.User;
import com.epickur.api.utils.Info;

/**
 * Class that handle static method that send emails
 * 
 * @author cph
 * @version 1.0
 *
 */
public class EmailUtils {

	private Email email;

	/**
	 * Constructor
	 */
	public EmailUtils() {
		this.email = new Email();
	}

	public EmailUtils(final Email email) {
		this.email = email;
	}

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
		Map<String, String> emailData = EmailTemplate.convertToDataNewRegistrationUser(user, code);
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
		Map<String, String> emailDataAdmin = EmailTemplate.convertToDataNewRegistrationAdmins(user);
		// Send an email to admins
		this.email.sendMail(EmailType.REGISTRATION_ADMIN, emailDataAdmin, Info.admins.toArray(new String[Info.admins.size()]));
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
		Map<String, String> emailData = EmailTemplate.convertToDataNewOrderUser(user, order);
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
		Map<String, String> emailData = EmailTemplate.convertToDataNewOrderCaterer(user, order, orderCode);
		this.email.sendMail(EmailType.ORDER_CATERER_NEW, emailData, new String[] { order.getDish().getCaterer().getEmail() });
	}

	/**
	 * @param user
	 *            The user
	 * @param order
	 *            The order
	 */
	private void emailNewOrderAdmin(final User user, final Order order) {
		Map<String, String> emailData = EmailTemplate.convertToDataNewOrderAdmins(user, order);
		this.email.sendMail(EmailType.ORDER_ADMINS_NEW, emailData, Info.admins.toArray(new String[Info.admins.size()]));
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
		Map<String, String> emailData = EmailTemplate.convertToDataDeclineOrderUser(user, order);
		this.email.sendMail(EmailType.ORDER_USER_DECLINED, emailData, new String[] { user.getEmail() });
	}

	/**
	 * @param user
	 *            The user
	 * @param order
	 *            The order
	 */
	private void emailDeclineOrderAdmins(final User user, final Order order) {
		Map<String, String> emailData = EmailTemplate.convertToDataDeclineOrderAdmins(user, order);
		this.email.sendMail(EmailType.ORDER_ADMINS_DECLINED, emailData, Info.admins.toArray(new String[Info.admins.size()]));
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
		Map<String, String> emailData = EmailTemplate.convertToDataSuccessOrderUser(user, order);
		this.email.sendMail(EmailType.ORDER_USER_SUCCESS, emailData, new String[] { user.getEmail() });
	}

	/**
	 * @param user
	 *            The user
	 * @param order
	 *            The order
	 */
	private void emailSuccessOrderCaterer(final User user, final Order order) {
		Map<String, String> emailData = EmailTemplate.convertToDataSuccessOrderCaterer(user, order);
		this.email.sendMail(EmailType.ORDER_CATERER_SUCCESS, emailData, new String[] { order.getDish().getCaterer().getEmail() });
	}

	/**
	 * @param user
	 *            The user
	 * @param order
	 *            The order
	 */
	private void emailSuccessOrderAdmins(final User user, final Order order) {
		Map<String, String> emailData = EmailTemplate.convertToDataSuccessOrderAdmins(user, order);
		this.email.sendMail(EmailType.ORDER_ADMINS_SUCCESS, emailData, Info.admins.toArray(new String[Info.admins.size()]));
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
		Map<String, String> emailData = EmailTemplate.convertToDataDeclineOrderUser(user, order);
		this.email.sendMail(EmailType.ORDER_USER_FAIL, emailData, new String[] { user.getEmail() });
	}

	/**
	 * @param user
	 *            The user
	 * @param order
	 *            The order
	 */
	private void emailFailOrderCaterer(final User user, final Order order) {
		Map<String, String> emailData = EmailTemplate.convertToDataFailOrderCaterer(user, order);
		this.email.sendMail(EmailType.ORDER_CATERER_FAIL, emailData, new String[] { order.getDish().getCaterer().getEmail() });
	}

	/**
	 * @param user
	 *            The user
	 * @param order
	 *            The order
	 */
	private void emailFailOrderAdmins(final User user, final Order order) {
		Map<String, String> emailData = EmailTemplate.convertToDataFailOrderAdmins(user, order);
		this.email.sendMail(EmailType.ORDER_ADMINS_FAIL, emailData, Info.admins.toArray(new String[Info.admins.size()]));
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
		Map<String, String> emailData = EmailTemplate.convertToDataCancelOrderUser(user, order);
		this.email.sendMail(EmailType.ORDER_USER_CANCEL, emailData, new String[] { user.getEmail() });
	}

	/**
	 * @param user
	 *            The user
	 * @param order
	 *            The order
	 */
	private void emailCancelOrderCaterer(final User user, final Order order) {
		Map<String, String> emailData = EmailTemplate.convertToDataCancelOrderCaterer(user, order);
		this.email.sendMail(EmailType.ORDER_CATERER_CANCEL, emailData, new String[] { order.getDish().getCaterer().getEmail() });
	}

	/**
	 * @param user
	 *            The user
	 * @param order
	 *            The order
	 */
	private void emailCancelOrderAdmins(final User user, final Order order) {
		Map<String, String> emailData = EmailTemplate.convertToDataCancelOrderAdmins(user, order);
		this.email.sendMail(EmailType.ORDER_ADMINS_CANCEL, emailData, Info.admins.toArray(new String[Info.admins.size()]));
	}

	/**
	 * @param user
	 *            The user.
	 * @param resetCode
	 *            The reset code.
	 */
	public void resetPassword(final User user, final String resetCode) {
		Map<String, String> emailData = EmailTemplate.convertToDataResetUserPassword(user, resetCode);
		this.email.sendMail(EmailType.RESET_USER_PASSWORD, emailData, new String[] { user.getEmail() });
	}
}
