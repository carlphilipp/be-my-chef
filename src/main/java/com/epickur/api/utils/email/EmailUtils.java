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
public final class EmailUtils {

	/**
	 * We can't construct this class
	 */
	private EmailUtils() {
	}

	// Registration
	/**
	 * @param name
	 *            The user name
	 * @param code
	 *            The code
	 * @param email
	 *            The email
	 */
	public static void emailNewRegistration(final String name, final String first, final String code, final String email) {
		emailNewRegistrationUser(name, first, code, email);
		emailNewRegistrationAdmin(name, email);
	}

	/**
	 * @param name
	 *            The user name
	 * @param code
	 *            The code
	 * @param email
	 *            The email
	 */
	private static void emailNewRegistrationUser(final String name, final String first, final String code, final String email) {
		// Convert data to use email template
		Map<String, String> emailData = EmailTemplate.convertToDataNewRegistrationUser(name, first, email, code);
		// Send an email to the user
		Email.sendMail(EmailType.REGISTRATION_USER, emailData, new String[] { email });
	}

	/**
	 * @param name
	 *            The user name
	 * @param email
	 *            The user email
	 */
	private static void emailNewRegistrationAdmin(final String name, final String email) {
		// Convert data to use email template
		Map<String, String> emailDataAdmin = EmailTemplate.convertToDataNewRegistrationAdmins(name, email);
		// Send an email to admins
		Email.sendMail(EmailType.REGISTRATION_ADMIN, emailDataAdmin, Info.admins.toArray(new String[Info.admins.size()]));
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
	public static void emailNewOrder(final User user, final Order order, final String orderCode) {
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
	private static void emailNewOrderUser(final User user, final Order order) {
		Map<String, String> emailData = EmailTemplate.convertToDataNewOrderUser(user, order);
		Email.sendMail(EmailType.ORDER_USER_NEW, emailData, new String[] { user.getEmail() });
	}

	/**
	 * @param user
	 *            The user
	 * @param order
	 *            The order
	 * @param orderCode
	 *            The order code
	 */
	private static void emailNewOrderCaterer(final User user, final Order order, final String orderCode) {
		Map<String, String> emailData = EmailTemplate.convertToDataNewOrderCaterer(user, order, orderCode);
		Email.sendMail(EmailType.ORDER_CATERER_NEW, emailData, new String[] { order.getDish().getCaterer().getEmail() });
	}

	/**
	 * @param user
	 *            The user
	 * @param order
	 *            The order
	 */
	private static void emailNewOrderAdmin(final User user, final Order order) {
		Map<String, String> emailData = EmailTemplate.convertToDataNewOrderAdmins(user, order);
		Email.sendMail(EmailType.ORDER_ADMINS_NEW, emailData, Info.admins.toArray(new String[Info.admins.size()]));
	}

	// ORDER: case 2 - Caterer declined the order
	/**
	 * @param user
	 *            The user
	 * @param order
	 *            The order
	 */
	public static void emailDeclineOrder(final User user, final Order order) {
		emailDeclineOrderUser(user, order);
		emailDeclineOrderAdmins(user, order);
	}

	/**
	 * @param user
	 *            The user
	 * @param order
	 *            The order
	 */
	private static void emailDeclineOrderUser(final User user, final Order order) {
		Map<String, String> emailData = EmailTemplate.convertToDataDeclineOrderUser(user, order);
		Email.sendMail(EmailType.ORDER_USER_DECLINED, emailData, new String[] { user.getEmail() });
	}

	/**
	 * @param order
	 *            The order
	 */
	private static void emailDeclineOrderAdmins(final User user, final Order order) {
		Map<String, String> emailData = EmailTemplate.convertToDataDeclineOrderAdmins(user, order);
		Email.sendMail(EmailType.ORDER_ADMINS_DECLINED, emailData, Info.admins.toArray(new String[Info.admins.size()]));
	}

	// ORDER: case 3 - The order is a success
	/**
	 * @param user
	 *            The user
	 * @param order
	 *            The order
	 */
	public static void emailSuccessOrder(final User user, final Order order) {
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
	private static void emailSuccessOrderUser(final User user, final Order order) {
		Map<String, String> emailData = EmailTemplate.convertToDataSuccessOrderUser(user, order);
		Email.sendMail(EmailType.ORDER_USER_SUCCESS, emailData, new String[] { user.getEmail() });
	}

	/**
	 * @param order
	 *            The order
	 */
	private static void emailSuccessOrderCaterer(final User user, final Order order) {
		Map<String, String> emailData = EmailTemplate.convertToDataSuccessOrderCaterer(user, order);
		Email.sendMail(EmailType.ORDER_CATERER_SUCCESS, emailData, new String[] { order.getDish().getCaterer().getEmail() });
	}

	/**
	 * @param order
	 *            The order
	 */
	private static void emailSuccessOrderAdmins(final User user, final Order order) {
		Map<String, String> emailData = EmailTemplate.convertToDataSuccessOrderAdmins(user, order);
		Email.sendMail(EmailType.ORDER_ADMINS_SUCCESS, emailData, Info.admins.toArray(new String[Info.admins.size()]));
	}

	// ORDER: case 4 - The order has been accepted but the payment failed
	/**
	 * @param user
	 *            The user
	 * @param order
	 *            The order
	 */
	public static void emailFailOrder(final User user, final Order order) {
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
	private static void emailFailOrderUser(final User user, final Order order) {
		Map<String, String> emailData = EmailTemplate.convertToDataDeclineOrderUser(user, order);
		Email.sendMail(EmailType.ORDER_USER_FAIL, emailData, new String[] { user.getEmail() });
	}

	/**
	 * @param order
	 *            The order
	 */
	private static void emailFailOrderCaterer(final User user, final Order order) {
		Map<String, String> emailData = EmailTemplate.convertToDataFailOrderCaterer(user, order);
		Email.sendMail(EmailType.ORDER_CATERER_FAIL, emailData, new String[] { order.getDish().getCaterer().getEmail() });
	}

	/**
	 * @param order
	 *            The order
	 */
	private static void emailFailOrderAdmins(final User user, final Order order) {
		Map<String, String> emailData = EmailTemplate.convertToDataFailOrderAdmins(user, order);
		Email.sendMail(EmailType.ORDER_ADMINS_FAIL, emailData, Info.admins.toArray(new String[Info.admins.size()]));
	}

	// ORDER: case 5 - The order has been received by the Caterer, but he did not answer it on time.
	/**
	 * @param user
	 *            The user
	 * @param order
	 *            The order
	 */
	public static void emailCancelOrder(final User user, final Order order) {
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
	private static void emailCancelOrderUser(final User user, final Order order) {
		Map<String, String> emailData = EmailTemplate.convertToDataCancelOrderCaterer(user, order);
		Email.sendMail(EmailType.ORDER_USER_CANCEL, emailData, new String[] { user.getEmail() });
	}

	/**
	 * @param order
	 *            The order
	 */
	private static void emailCancelOrderCaterer(final User user, final Order order) {
		Map<String, String> emailData = EmailTemplate.convertToDataCancelOrderCaterer(user, order);
		Email.sendMail(EmailType.ORDER_CATERER_CANCEL, emailData, new String[] { order.getDish().getCaterer().getEmail() });
	}

	/**
	 * @param order
	 *            The order
	 */
	private static void emailCancelOrderAdmins(final User user, final Order order) {
		Map<String, String> emailData = EmailTemplate.convertToDataCancelOrderAdmins(user, order);
		Email.sendMail(EmailType.ORDER_ADMINS_CANCEL, emailData, Info.admins.toArray(new String[Info.admins.size()]));
	}

	/**
	 * @param email
	 *            The email
	 * @param userId
	 *            The user id
	 * @param resetCode
	 *            The reset code
	 */
	public static void resetPassword(final String email, final String userId, final String resetCode) {
		Map<String, String> emailData = EmailTemplate.convertToDataResetUserPassword(email, userId, resetCode);
		Email.sendMail(EmailType.RESET_USER_PASSWORD, emailData, new String[] { email });
	}
}
