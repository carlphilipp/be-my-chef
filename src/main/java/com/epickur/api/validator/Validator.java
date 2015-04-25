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

	/** The entity **/
	private String entity;
	/** Id Null **/
	public static final String PARAM_ID_NULL = "The parameter id is not allowed to be null or empty";
	/** Order Id Null **/
	public static final String PARAM_ORDER_ID_NULL = "The parameter orderId is not allowed to be null or empty";
	/** Token Null **/
	public static final String PARAM_TOKEN_NULL = "The parameter token is not allowed to be null or empty";
	/** No User provided **/
	public static final String NO_USER_PROVIDED = "No user has been provided";
	/** No Order provided **/
	public static final String NO_ORDER_PROVIDED = "No order has been provided";
	/** No Caterer provided **/
	public static final String NO_CATERER_PROVIDED = "No caterer has been provided";
	/** No Dish provided **/
	public static final String NO_DISH_PROVIDED = "No dish has been provided";
	/** Field Null **/
	public static final String FIELD_NULL = "The field @object@.@field@ is not allowed to be null or empty";

	/**
	 * Constructor
	 * 
	 * @param entity
	 *            The entity
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
	 *            The entity
	 * @param field
	 *            The field
	 * @return A String
	 */
	public static final String fieldNull(final String entity, final String field) {
		return FIELD_NULL.replaceAll("@object@", entity).replaceAll("@field@", field);
	}

	/**
	 * @param role
	 *            The Role
	 * @param action
	 *            The Crud action
	 * @throws EpickurException
	 *             If an EpickurException occured
	 */
	public final void checkRightsBefore(final Role role, final Crud action) throws EpickurException {
		checkRightsBefore(role, action, null);
	}

	/**
	 * @param role
	 *            The Role
	 * @param action
	 *            The Crud action
	 * @param validatorType
	 *            The validator type
	 * @throws EpickurException
	 *             If an EpickurException occured
	 */
	public final void checkRightsBefore(final Role role, final Crud action, final String validatorType) throws EpickurException {
		String type = null;
		if (validatorType == null) {
			type = getEntity();
		} else {
			type = validatorType;
		}
		if (role != Role.ADMIN) {
			if (type.equals("user")) {
				if (action == Crud.CREATE || action == Crud.DELETE) {
					throw new ForbiddenException();
				}
			} else if (type.equals("caterer")) {
				if (action == Crud.CREATE || action == Crud.DELETE || action == Crud.UPDATE && role == Role.USER) {
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
