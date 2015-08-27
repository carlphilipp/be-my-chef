package com.epickur.api.validator;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;

import com.epickur.api.entity.Caterer;
import com.epickur.api.entity.Key;
import com.epickur.api.entity.Order;
import com.epickur.api.entity.User;
import com.epickur.api.entity.Voucher;
import com.epickur.api.entity.times.WorkingTimes;
import com.epickur.api.enumeration.Operation;
import com.epickur.api.enumeration.Role;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.exception.EpickurForbiddenException;
import com.epickur.api.exception.EpickurIllegalArgument;
import com.epickur.api.exception.EpickurParsingException;
import com.epickur.api.utils.Utils;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.i18n.phonenumbers.PhoneNumberUtil;

/**
 * @author cph
 * @version 1.0
 */
public final class UserValidator extends Validator {

	/** Logger */
	private static final Logger LOG = LogManager.getLogger(UserValidator.class.getSimpleName());

	/**
	 * Constructor
	 */
	protected UserValidator() {
		super("user");
	}

	/**
	 * @param user
	 *            The User to check
	 */
	public void checkCreateUser(final User user) {
		if (user == null) {
			throw new EpickurIllegalArgument(NO_USER_PROVIDED);
		}
		if (StringUtils.isBlank(user.getName())) {
			throw new EpickurIllegalArgument(fieldNull(getEntity(), "name"));
		}
		if (StringUtils.isBlank(user.getPassword())) {
			throw new EpickurIllegalArgument(fieldNull(getEntity(), "password"));
		}
		if (StringUtils.isBlank(user.getEmail())) {
			throw new EpickurIllegalArgument(fieldNull(getEntity(), "email"));
		}
		if (StringUtils.isBlank(user.getCountry())) {
			throw new EpickurIllegalArgument(fieldNull(getEntity(), "country"));
		}
		if (StringUtils.isBlank(user.getZipcode())) {
			throw new EpickurIllegalArgument(fieldNull(getEntity(), "zipcode"));
		}
		if (StringUtils.isBlank(user.getState())) {
			throw new EpickurIllegalArgument(fieldNull(getEntity(), "state"));
		}
		if (user.getPhoneNumber() != null) {
			PhoneNumberUtil util = PhoneNumberUtil.getInstance();
			if (!util.isValidNumber(user.getPhoneNumber())) {
				System.out.println(user.getPhoneNumber());
				throw new EpickurIllegalArgument("The field " + getEntity() + ".phoneNumber is not valid");
			}
		}
	}

	/**
	 * @param id
	 *            The User id
	 * @param user
	 *            The User
	 */
	public void checkUpdateUser(final String id, final User user) {
		checkId(id);
		if (user == null) {
			throw new EpickurIllegalArgument(NO_USER_PROVIDED);
		}
		if (StringUtils.isNotBlank(user.getNewPassword()) && StringUtils.isBlank(user.getPassword())) {
			throw new EpickurIllegalArgument("The field " + getEntity() + ".password is mandatory when a new password is provided");
		}
		if (user.getId() == null) {
			throw new EpickurIllegalArgument(fieldNull(getEntity(), "id"));
		}
		if (!user.getId().toHexString().equals(id)) {
			throw new EpickurIllegalArgument("The parameter id and the field user.id should match");
		}
		if (user.getAllow() != null) {
			user.setAllow(null);
		}
	}

	/**
	 * @param id
	 *            The User id
	 * @param orderId
	 *            The Order id
	 */
	public void checkReadOneOrder(final String id, final String orderId) {
		checkId(id);
		checkId(orderId);
	}

	/**
	 * @param id
	 *            The User id
	 * @param key
	 *            The Key
	 */
	public void checkReadAllOrder(final String id, final Key key) {
		checkId(id);
	}

	/**
	 * @param userId
	 *            The User id
	 * @param order
	 *            The Order
	 */
	public void checkCreateOneOrder(final String userId, final Order order) {
		checkId(userId);
		if (order == null) {
			throw new EpickurIllegalArgument(NO_ORDER_PROVIDED);
		} else {
			DishValidator validator = new DishValidator();
			validator.checkCreateData(order.getDish());
			if (StringUtils.isBlank(order.getCardToken())) {
				throw new EpickurIllegalArgument(fieldNull("order", "cardToken"));
			}
			if (StringUtils.isBlank(order.getDescription())) {
				throw new EpickurIllegalArgument(fieldNull("order", "description"));
			}
			if (order.getAmount() == null) {
				throw new EpickurIllegalArgument(fieldNull("order", "amount"));
			}
			if (order.getCurrency() == null) {
				throw new EpickurIllegalArgument(fieldNull("order", "currency"));
			}
			if (StringUtils.isBlank(order.getPickupdate())) {
				throw new EpickurIllegalArgument(fieldNull("order", "pickupdate"));
			} else {
				Object[] result = Utils.parsePickupdate(order.getPickupdate());
				if (result == null) {
					throw new EpickurIllegalArgument(
							"The field order.pickupdate has a wrong format. Should be: ddd-hh:mm, with ddd: mon|tue|wed|thu|fri|sat|sun. Found: "
									+ order.getPickupdate());
				} else {
					Caterer caterer = order.getDish().getCaterer();
					WorkingTimes workingTimes = caterer.getWorkingTimes();
					if (!workingTimes.canBePickup((String) result[0], (Integer) result[1])) {
						LOG.info("1: " + (String) result[0]);
						LOG.info("2: " + result[1]);
						LOG.info("Prep time: " + workingTimes.getMinimumPreparationTime());
						try {
							LOG.info("Hours: " + workingTimes.getHours().toStringAPIView());
						} catch (EpickurParsingException e) {
							LOG.error(e.getLocalizedMessage(), e);
						}
						throw new EpickurIllegalArgument("The order has a wrong pickupdate");
					}
				}
			}
			if (order.getPaid() != null) {
				if (order.getPaid()) {
					throw new EpickurIllegalArgument("The field order.paid can not be true");
				}
			}
			if(order.getVoucher() != null){
				checkVoucherData(order.getVoucher());
			}
		}
	}

	/**
	 * @param voucher
	 */
	private void checkVoucherData(final Voucher voucher) {
		VoucherValidator validator = (VoucherValidator) FactoryValidator.getValidator("voucher");
		validator.checkVoucher(voucher, "order");
	}

	/**
	 * @param id
	 *            The User id
	 * @param orderId
	 *            The Order id
	 * @param order
	 *            The Order
	 */
	public void checkUpdateOneOrder(final String id, final String orderId, final Order order) {
		checkId(id);
		checkId(orderId);
		if (order == null) {
			throw new EpickurIllegalArgument(NO_ORDER_PROVIDED);
		}
		if (order.getId() == null) {
			throw new EpickurIllegalArgument(fieldNull("order", "id"));
		}
		if (!order.getId().toHexString().equals(orderId)) {
			throw new EpickurIllegalArgument("The parameter orderId and the field order.id should match");
		}
	}

	/**
	 * @param id
	 *            The User id
	 * @param orderId
	 *            The Order id
	 */
	public void checkDeleteOneOrder(final String id, final String orderId) {
		checkId(id);
		checkId(orderId);
	}

	/**
	 * @param email
	 *            The user email
	 * @param check
	 *            The check code
	 */
	public void checkCheckUser(final String email, final String check) {
		if (StringUtils.isBlank(email)) {
			throw new EpickurIllegalArgument("The parameter email is not allowed to be null or empty");
		}
		if (StringUtils.isBlank(check)) {
			throw new EpickurIllegalArgument("The parameter check is not allowed to be null or empty");
		}
	}

	/**
	 * @param email
	 *            The user email
	 * @param password
	 *            The user email
	 */
	public void checkLogin(final String email, final String password) {
		if (StringUtils.isBlank(email)) {
			throw new EpickurIllegalArgument("The parameter email is not allowed to be null or empty");
		}
		if (StringUtils.isBlank(password)) {
			throw new EpickurIllegalArgument("The parameter password is not allowed to be null or empty");
		}
	}

	/**
	 * @param role
	 *            The Role
	 * @param userId
	 *            The User Id
	 * @param user
	 *            The User
	 * @param action
	 *            The Crud action
	 * @throws EpickurException
	 *             If a EpickurException occured
	 */
	public void checkUserRightsAfter(final Role role, final ObjectId userId, final User user, final Operation action) throws EpickurException {
		if (role != Role.ADMIN) {
			if ((action == Operation.READ && (role == Role.USER || role == Role.SUPER_USER))
					|| (action == Operation.UPDATE && (role == Role.USER || role == Role.SUPER_USER))) {
				if (!userId.equals(user.getId())) {
					throw new EpickurForbiddenException();
				}
			} else {
				throw new EpickurException("Rights issue. This case should not happen");
			}
		}
	}

	/**
	 * @param role
	 *            The Role
	 * @param userId
	 *            The User Id
	 * @param order
	 *            The Order
	 * @param action
	 *            The Crud action
	 */
	public void checkOrderRightsAfter(final Role role, final ObjectId userId, final Order order, final Operation action) {
		if (role != Role.ADMIN) {
			if (action == Operation.DELETE) {
				throw new EpickurForbiddenException();
			}
			if (action == Operation.READ || action == Operation.UPDATE) {
				if (!userId.equals(order.getCreatedBy())) {
					throw new EpickurForbiddenException();
				}
			}
		}
	}

	/**
	 * @param node
	 *            The node containing the email
	 */
	public void checkResetPasswordData(final ObjectNode node) {
		if (!node.has("email")) {
			throw new EpickurIllegalArgument("The field email is mandatory");
		} else {
			String email = node.get("email").asText();
			if (StringUtils.isBlank(email)) {
				throw new EpickurIllegalArgument("The field password is not allowed to be null or empty");
			}
		}
	}

	/**
	 * @param id
	 *            The id
	 * @param node
	 *            The node containing the password
	 * @param token
	 *            The token
	 */
	public void checkResetPasswordData(final String id, final ObjectNode node, final String token) {
		checkId(id);
		if (StringUtils.isBlank(token)) {
			throw new EpickurIllegalArgument("The parameter token is not allowed to be null or empty");
		}
		if (!node.has("password")) {
			throw new EpickurIllegalArgument("The field password is mandatory");

		} else {
			String password = node.get("password").asText();
			if (StringUtils.isBlank(password)) {
				throw new EpickurIllegalArgument("The field password is not allowed to be null or empty");
			}
		}
	}
}
