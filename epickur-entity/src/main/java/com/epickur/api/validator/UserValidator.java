package com.epickur.api.validator;

import com.epickur.api.commons.CommonsUtil;
import com.epickur.api.entity.Caterer;
import com.epickur.api.entity.Order;
import com.epickur.api.entity.User;
import com.epickur.api.entity.Voucher;
import com.epickur.api.entity.times.WorkingTimes;
import com.epickur.api.enumeration.Operation;
import com.epickur.api.enumeration.OrderStatus;
import com.epickur.api.enumeration.Role;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.exception.EpickurForbiddenException;
import com.epickur.api.exception.EpickurIllegalArgument;
import com.epickur.api.exception.EpickurParsingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;

/**
 * @author cph
 * @version 1.0
 */
public class UserValidator extends Validator {

	/**
	 * Logger
	 */
	private static final Logger LOG = LogManager.getLogger(UserValidator.class.getSimpleName());

	/**
	 * Constructor
	 */
	protected UserValidator() {
		super("user");
	}

	/**
	 * @param id      The User id
	 * @param orderId The Order id
	 */
	public void checkReadOneOrder(final String id, final String orderId) {
		checkId(id);
		checkId(orderId);
	}

//	/**
//	 * @param order The Order
//	 */
//	public void checkCreateOneOrder(final Order order) {
//		if (order == null) {
//			throw new EpickurIllegalArgument(NO_ORDER_PROVIDED);
//		} else {
//			DishValidator validator = new DishValidator();
//			validator.checkCreateData(order.getDish());
//			if (StringUtils.isBlank(order.getCardToken())) {
//				throw new EpickurIllegalArgument(fieldNull("order", "cardToken"));
//			}
//			if (StringUtils.isBlank(order.getDescription())) {
//				throw new EpickurIllegalArgument(fieldNull("order", "description"));
//			}
//			if (order.getQuantity() == null) {
//				throw new EpickurIllegalArgument(fieldNull("order", "quantity"));
//			}
//			if (order.getAmount() == null) {
//				throw new EpickurIllegalArgument(fieldNull("order", "amount"));
//			}
//			if (order.getCurrency() == null) {
//				throw new EpickurIllegalArgument(fieldNull("order", "currency"));
//			}
//			if (StringUtils.isBlank(order.getPickupdate())) {
//				throw new EpickurIllegalArgument(fieldNull("order", "pickupdate"));
//			} else {
//				Object[] result = CommonsUtil.parsePickupdate(order.getPickupdate());
//				if (result == null) {
//					throw new EpickurIllegalArgument(
//							"The field order.pickupdate has a wrong format. Should be: ddd-hh:mm, with ddd: mon|tue|wed|thu|fri|sat|sun. Found: "
//									+ order.getPickupdate());
//				} else {
//					Caterer caterer = order.getDish().getCaterer();
//					WorkingTimes workingTimes = caterer.getWorkingTimes();
//					if (!workingTimes.canBePickup((String) result[0], (Integer) result[1])) {
//						throw new EpickurIllegalArgument("The order has a wrong pickupdate");
//					}
//				}
//			}
//			if (order.getPaid() != null && order.getPaid()) {
//				throw new EpickurIllegalArgument("The field order.paid can not be true");
//			}
//			if (order.getVoucher() != null) {
//				checkVoucherData(order.getVoucher());
//			}
//		}
//	}

	/**
	 * @param voucher The voucher
	 */
	private void checkVoucherData(final Voucher voucher) {
		VoucherValidator validator = (VoucherValidator) FactoryValidator.getValidator("voucher");
		validator.checkVoucher(voucher, "order");
	}

	/**
	 * @param orderId The Order id
	 * @param order   The Order
	 */
//	public void checkUpdateOneOrder(final String orderId, final Order order) {
//		if (order == null) {
//			throw new EpickurIllegalArgument(NO_ORDER_PROVIDED);
//		}
//		if (!order.getId().toHexString().equals(orderId)) {
//			throw new EpickurIllegalArgument("The parameter orderId and the field order.id should match");
//		}
//	}

	/**
	 * @param email The user email
	 * @param check The check code
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
	 * @param role   The Role
	 * @param userId The User Id
	 * @param user   The User
	 * @param action The Crud action
	 * @throws EpickurException If a EpickurException occured
	 */
	public void checkUserRightsAfter(final Role role, final ObjectId userId, final User user, final Operation action) throws EpickurException {
		if (role != Role.ADMIN) {
			if ((action == Operation.READ && (role == Role.USER || role == Role.SUPER_USER)) // NOPMD
					|| (action == Operation.UPDATE && (role == Role.USER || role == Role.SUPER_USER))) { // NOPMD
				if (!userId.equals(user.getId())) {
					throw new EpickurForbiddenException();
				}
			} else {
				throw new EpickurException("Rights issue. This case should not happen");
			}
		}
	}

	/**
	 * @param role   The Role
	 * @param userId The User Id
	 * @param order  The Order
	 * @param action The Crud action
	 */
	public void checkOrderRightsAfter(final Role role, final ObjectId userId, final Order order, final Operation action) {
		if (role != Role.ADMIN) {
			if (action == Operation.DELETE) {
				throw new EpickurForbiddenException("User not allowed to access to the order");
			}
			if (action == Operation.READ || action == Operation.UPDATE) {
				if (!userId.equals(order.getCreatedBy())) { // NOPMD
					throw new EpickurForbiddenException("User not allowed to access to the order");
				}
			}
		}
	}

	public void checkOrderStatus(final Order order) throws EpickurException {
		if (order.getStatus() != OrderStatus.PENDING) {
			throw new EpickurException("It's not allowed to modify an order that has a " + order.getStatus() + " status");
		}
	}

	/**
	 * @param node The node containing the email
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
	 * @param node The node containing the password
	 */
	public void checkResetPasswordDataSecondStep(final ObjectNode node) {
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
