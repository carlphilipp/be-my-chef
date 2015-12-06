package com.epickur.api.validator;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;

import com.epickur.api.exception.EpickurIllegalArgument;

/**
 * @author cph
 * @version 1.0
 */
public abstract class Validator {

	/** The entity */
	private String entity;
	/** Id Null */
	public static final String PARAM_ID_NULL = "The parameter id is not allowed to be null or empty";
	/** Order Id Null */
	public static final String PARAM_ORDER_ID_NULL = "The parameter orderId is not allowed to be null or empty";
	/** Token Null */
	public static final String PARAM_TOKEN_NULL = "The parameter token is not allowed to be null or empty";
	/** No User provided */
	public static final String NO_USER_PROVIDED = "No user has been provided";
	/** No Order provided */
	public static final String NO_ORDER_PROVIDED = "No order has been provided";
	/** No Caterer provided */
	public static final String NO_CATERER_PROVIDED = "No caterer has been provided";
	/** No Dish provided */
	public static final String NO_DISH_PROVIDED = "No dish has been provided";
	/** No Voucher provided */
	public static final String NO_VOUCHER_PROVIDED = "No voucher has been provided";
	/** Field Null */
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
	public static String fieldNull(final String entity, final String field) {
		return FIELD_NULL.replaceAll("@object@", entity).replaceAll("@field@", field);
	}

	/**
	 * Check if an id is correctly formed
	 * 
	 * @param id
	 *            The id to check
	 */
	public final void checkId(final String id) {
		if (StringUtils.isBlank(id)) {
			throw new EpickurIllegalArgument(PARAM_ID_NULL);
		}
		if (!ObjectId.isValid(id)) {
			throw new EpickurIllegalArgument(id + " is not a valid id");
		}
	}
}
