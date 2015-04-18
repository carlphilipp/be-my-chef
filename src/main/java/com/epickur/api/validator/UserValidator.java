package com.epickur.api.validator;

import javax.ws.rs.ForbiddenException;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;

import com.epickur.api.entity.Key;
import com.epickur.api.entity.Order;
import com.epickur.api.entity.User;
import com.epickur.api.enumeration.Crud;
import com.epickur.api.enumeration.Role;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.exception.EpickurIllegalArgument;

public final class UserValidator extends Validator {

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
	}

	/**
	 * @param id
	 *            The User id
	 */
	public void checkId(final String id) {
		if (StringUtils.isBlank(id)) {
			throw new EpickurIllegalArgument(PARAM_ID_NULL);
		}
	}

	/**
	 * @param user
	 *            The User
	 */
	public void checkUpdateUser(final User user) {
		if (user == null) {
			throw new EpickurIllegalArgument(NO_USER_PROVIDED);
		}
		if (StringUtils.isNotBlank(user.getNewPassword())) {
			if (StringUtils.isBlank(user.getPassword())) {
				throw new EpickurIllegalArgument("The field " + getEntity() + ".password is mandatory when a new password is provided");
			}
		}
		if (user.getId() == null) {
			throw new EpickurIllegalArgument(fieldNull(getEntity(), "id"));
		}
		if (user.getAllow() != null) {
			user.setAllow(null);
		}
		if (user.getRole() != null) {
			user.setRole(null);
		}
	}

	/**
	 * @param id
	 *            The User id
	 * @param user
	 *            The User
	 */
	public void checkUpdateUser2(final String id, final User user) {
		if (StringUtils.isBlank(id)) {
			throw new EpickurIllegalArgument(PARAM_ID_NULL);
		}
		if (user == null) {
			throw new EpickurIllegalArgument(NO_USER_PROVIDED);
		}
		if (StringUtils.isNotBlank(user.getNewPassword())) {
			if (StringUtils.isBlank(user.getPassword())) {
				throw new EpickurIllegalArgument("The field " + getEntity() + ".password is mandatory when a new password is provided");
			}
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
		if (StringUtils.isBlank(id)) {
			throw new EpickurIllegalArgument(PARAM_ID_NULL);
		}
		if (StringUtils.isBlank(orderId)) {
			throw new EpickurIllegalArgument(PARAM_ORDERID_NULL);
		}
	}

	/**
	 * @param id
	 *            The User id
	 */
	public void checkReadAllOrder(final String id, final Key key) {
		if (StringUtils.isBlank(id)) {
			throw new EpickurIllegalArgument(PARAM_ID_NULL);
		}
	}

	/**
	 * @param id
	 *            The User id
	 * @param token
	 *            The Stripe token
	 * @param cardToken
	 *            True if the card must be charged
	 */
	public void checkCreateOneOrder(final String id, final String token, final boolean cardToken, final Order order) {
		if (StringUtils.isBlank(id)) {
			throw new EpickurIllegalArgument(PARAM_ID_NULL);
		}
		if (cardToken) {
			if (StringUtils.isBlank(token)) {
				throw new EpickurIllegalArgument(PARAM_TOKEN_NULL);
			}
		}
		if (order == null) {
			throw new EpickurIllegalArgument(NO_ORDER_PROVIDED);
		} else {
			DishValidator validator = new DishValidator();
			validator.checkCreateData(order.getDish());
		}
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
		if (StringUtils.isBlank(id)) {
			throw new EpickurIllegalArgument(PARAM_ID_NULL);
		}
		if (StringUtils.isBlank(orderId)) {
			throw new EpickurIllegalArgument(PARAM_ORDERID_NULL);
		}
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
		if (StringUtils.isBlank(id)) {
			throw new EpickurIllegalArgument(PARAM_ID_NULL);
		}
		if (StringUtils.isBlank(orderId)) {
			throw new EpickurIllegalArgument(PARAM_ORDERID_NULL);
		}
	}

	/**
	 * @param name
	 *            The user name
	 * @param check
	 *            The check code
	 */
	public void checkCheckUser(final String name, final String check) {
		if (StringUtils.isBlank(name)) {
			throw new EpickurIllegalArgument("The parameter name is not allowed to be null or empty");
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

	public void checkUserRightsAfter(final Role role, final ObjectId userId, final User user, final Crud action) throws EpickurException {
		if (role != Role.ADMIN) {
			if ((action == Crud.READ && (role == Role.USER || role == Role.SUPER_USER))
					|| (action == Crud.UPDATE && (role == Role.USER || role == Role.SUPER_USER))) {
				if (!userId.equals(user.getId())) {
					throw new ForbiddenException();
				}
			} else {
				throw new EpickurException("Rights issue. This case should not happen");
			}
		}
	}

	public void checkOrderRightsAfter(final Role role, final ObjectId userId, final Order order, final Crud action) {
		if (role != Role.ADMIN) {
			if (action == Crud.DELETE) {
				throw new ForbiddenException();
			}
			if (action == Crud.READ || action == Crud.UPDATE) {
				if (!userId.equals(order.getCreatedBy())) {
					throw new ForbiddenException();
				}
			}
		}
	}
}
