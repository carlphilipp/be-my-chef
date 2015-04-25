package com.epickur.api.validator;

import org.apache.commons.lang3.StringUtils;

import com.epickur.api.enumeration.DishType;
import com.epickur.api.exception.EpickurIllegalArgument;

/**
 * @author cph
 * @version 1.0
 *
 */
public final class SearchValidator extends Validator {

	/**
	 * Constructor
	 */
	protected SearchValidator() {
		super("search");
	}

	/**
	 * @param type
	 *            The Dish type
	 * @param address
	 *            The address
	 */
	public void checkSearch(final DishType type, final String address) {
		if (type == null) {
			throw new EpickurIllegalArgument("The parameter type is not allowed to be null");
		}
		if (StringUtils.isBlank(address)) {
			throw new EpickurIllegalArgument("The address type is not allowed to be null or empty");
		}
	}

}
