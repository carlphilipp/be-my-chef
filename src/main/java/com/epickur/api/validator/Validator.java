package com.epickur.api.validator;

import javax.ws.rs.ForbiddenException;

import com.epickur.api.enumeration.Crud;
import com.epickur.api.enumeration.Role;
import com.epickur.api.exception.EpickurException;

/**
 * @author cph
 * @version 1.0
 */
public abstract class Validator {

	private String entity;

	public static final String PARAM_ID_NULL = "The parameter id is not allowed to be null or empty";

	public static final String PARAM_ORDERID_NULL = "The parameter orderId is not allowed to be null or empty";

	public static final String PARAM_TOKEN_NULL = "The parameter token is not allowed to be null or empty";

	public static final String NO_USER_PROVIDED = "No user has been provided";

	public static final String NO_ORDER_PROVIDED = "No order has been provided";

	public static final String NO_CATERER_PROVIDED = "No caterer has been provided";

	public static final String NO_DISH_PROVIDED = "No dish has been provided";

	public static final String FIELD_NULL = "The field @object@.@field@ is not allowed to be null or empty";

	/**
	 * Constructor
	 */
	public Validator(final String entity) {
		this.entity = entity;
	}

	/**
	 * @return The entity name
	 */
	public final String getEntity() {
		return entity;
	}

	/**
	 * @param entity
	 * @param field
	 * @return
	 */
	public static final String fieldNull(final String entity, final String field) {
		return FIELD_NULL.replaceAll("@object@", entity).replaceAll("@field@", field);
	}

	/**
	 * @param role
	 * @param action
	 * @throws EpickurException
	 */
	public final void checkRightsBefore(final Role role, final Crud action) throws EpickurException {
		checkRightsBefore(role, action, null);
	}

	/**
	 * @param role
	 * @param action
	 * @param validatorType
	 * @throws EpickurException
	 */
	public final void checkRightsBefore(final Role role, final Crud action, final String validatorType) throws EpickurException {
		String type = null;
		if (validatorType == null) {
			type = getEntity();
		} else {
			type = new String(validatorType);
		}
		if (role != Role.ADMIN) {
			if (type.equals("user")) {
				if (action == Crud.CREATE || action == Crud.DELETE) {
					throw new ForbiddenException();
				}
			} else if (type.equals("caterer")) {
				if (action == Crud.CREATE || action == Crud.DELETE || (action == Crud.UPDATE && role == Role.USER)) {
					throw new ForbiddenException();
				}
			} else if (type.equals("dish")) {
				if (role == Role.USER && (action == Crud.CREATE || action == Crud.UPDATE || action == Crud.DELETE)) {
					throw new ForbiddenException();
				}
			} else if (type.equals("order")) {
				if (action == Crud.DELETE) {
					throw new ForbiddenException();
				}
			} else {
				throw new EpickurException("Type error while checking rights: " + type);
			}
		}
	}
}
