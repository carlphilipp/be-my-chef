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
	public static void emailNewRegistration(final String name, final String code, final String email) {
		emailNewRegistrationUser(name, code, email);
		emailNewRegistrationAdmin(name, email);
	}

	private static void emailNewRegistrationUser(final String name, final String code, final String email) {
		// Convert data to use email template
		Map<String, String> emailData = EmailTemplate.convertToDataNewRegistrationUser(name, code);
		// Send an email to the user
		Email.sendMail(EmailType.REGISTRATION_USER, emailData, new String[] { email });
	}

	private static void emailNewRegistrationAdmin(final String name, final String email) {
		// Convert data to use email template
		Map<String, String> emailDataAdmin = EmailTemplate.convertToDataNewRegistrationAdmins(name, email);
		// Send an email to admins
		Email.sendMail(EmailType.REGISTRATION_ADMIN, emailDataAdmin, Info.admins.toArray(new String[Info.admins.size()]));
	}

	// ORDER: case 1 - New order
	public static void emailNewOrder(final User user, final Order order) {
		emailNewOrderUser(user, order);
		emailNewOrderCaterer(user, order);
		emailNewOrderAdmin(user, order);
	}

	private static void emailNewOrderUser(final User user, final Order order) {
		Map<String, String> emailData = EmailTemplate.convertToDataNewOrderUser(user.getName(), order.getId().toHexString(), order.getDish()
				.getName());
		Email.sendMail(EmailType.ORDER_USER_NEW, emailData, new String[] { user.getEmail() });
	}

	private static void emailNewOrderCaterer(final User user, final Order order) {
		Map<String, String> emailData = EmailTemplate.convertToDataNewOrderCaterer(user.getName(), order.getId().toHexString(), order.getDish()
				.getName(), order.getDish().getCaterer().getName());
		Email.sendMail(EmailType.ORDER_CATERER_NEW, emailData, new String[] { order.getDish().getCaterer().getEmail() });
	}

	private static void emailNewOrderAdmin(final User user, final Order order) {
		Map<String, String> emailData = EmailTemplate.convertToDataNewOrderAdmins(user.getName(), order.getId().toHexString(), order.getDish()
				.getName(),
				order.getDish().getCaterer().getName());
		Email.sendMail(EmailType.ORDER_ADMINS_NEW, emailData, Info.admins.toArray(new String[Info.admins.size()]));
	}

	// ORDER: case 2 - Caterer declined the order
	public static void emailDeclineOrder(final User user, final Order order) {
		emailDeclineOrderUser(user, order);
		emailDeclineOrderAdmins(order);
	}

	private static void emailDeclineOrderUser(final User user, final Order order) {
		Map<String, String> emailData = EmailTemplate.convertToDataDeclineOrderUser(order.getId().toHexString());
		Email.sendMail(EmailType.ORDER_USER_DECLINED, emailData, new String[] { user.getEmail() });
	}

	private static void emailDeclineOrderAdmins(final Order order) {
		Map<String, String> emailData = EmailTemplate.convertToDataDeclineOrderUser(order.getId().toHexString());
		Email.sendMail(EmailType.ORDER_ADMINS_DECLINED, emailData, Info.admins.toArray(new String[Info.admins.size()]));
	}

	// ORDER: case 3 - The order is a success
	public static void emailSuccessOrder(final User user, final Order order) {
		emailSuccessOrderUser(user, order);
		emailSuccessOrderCaterer(order);
		emailSuccessOrderAdmins(order);
	}

	private static void emailSuccessOrderUser(final User user, final Order order) {
		Map<String, String> emailData = EmailTemplate.convertToDataDeclineOrderUser(order.getId().toHexString());
		Email.sendMail(EmailType.ORDER_USER_SUCCESS, emailData, new String[] { user.getEmail() });
	}

	private static void emailSuccessOrderCaterer(final Order order) {
		Map<String, String> emailData = EmailTemplate.convertToDataSuccessOrderCaterer(order.getId().toHexString());
		Email.sendMail(EmailType.ORDER_CATERER_SUCCESS, emailData, new String[] { order.getDish().getCaterer().getEmail() });
	}

	private static void emailSuccessOrderAdmins(final Order order) {
		Map<String, String> emailData = EmailTemplate.convertToDataSuccessOrderCaterer(order.getId().toHexString());
		Email.sendMail(EmailType.ORDER_ADMINS_SUCCESS, emailData, Info.admins.toArray(new String[Info.admins.size()]));
	}

	// ORDER: case 4 - The order has been accepted but the payment failed
	public static void emailFailOrder(final User user, final Order order) {
		emailFailOrderUser(user, order);
		emailFailOrderCaterer(order);
		emailFailOrderAdmins(order);
	}

	private static void emailFailOrderUser(final User user, final Order order) {
		Map<String, String> emailData = EmailTemplate.convertToDataDeclineOrderUser(order.getId().toHexString());
		Email.sendMail(EmailType.ORDER_USER_FAIL, emailData, new String[] { user.getEmail() });
	}

	private static void emailFailOrderCaterer(final Order order) {
		Map<String, String> emailData = EmailTemplate.convertToDataSuccessOrderCaterer(order.getId().toHexString());
		Email.sendMail(EmailType.ORDER_CATERER_FAIL, emailData, new String[] { order.getDish().getCaterer().getEmail() });
	}

	private static void emailFailOrderAdmins(final Order order) {
		Map<String, String> emailData = EmailTemplate.convertToDataSuccessOrderCaterer(order.getId().toHexString());
		Email.sendMail(EmailType.ORDER_ADMINS_FAIL, emailData, Info.admins.toArray(new String[Info.admins.size()]));
	}
	
	// ORDER: case 5 - The order has been received by the Caterer, but he did not answer it on time.
	public static void emailCancelOrder(final User user, final Order order){
		emailCancelOrderUser(user, order);
		emailCancelOrderCaterer(order);
		emailCancelOrderAdmins(order);
	}

	private static void emailCancelOrderUser(final User user, final Order order) {
		Map<String, String> emailData = EmailTemplate.convertToDataCancelOrderUser(order.getId().toHexString());
		Email.sendMail(EmailType.ORDER_USER_CANCEL, emailData, new String[] { user.getEmail() });
	}
	private static void emailCancelOrderCaterer(final Order order) {
		Map<String, String> emailData = EmailTemplate.convertToDataCancelOrderUser(order.getId().toHexString());
		Email.sendMail(EmailType.ORDER_CATERER_CANCEL, emailData, new String[] { order.getDish().getCaterer().getEmail() });
	}
	private static void emailCancelOrderAdmins(final Order order) {
		Map<String, String> emailData = EmailTemplate.convertToDataCancelOrderUser(order.getId().toHexString());
		Email.sendMail(EmailType.ORDER_ADMINS_CANCEL, emailData, Info.admins.toArray(new String[Info.admins.size()]));
	}
}
