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
	 * @param types
	 *            The list of dish type
	 * @param at
	 *            The geo coordinate
	 * @param searchtext
	 *            The address to search
	 */
	public void checkSearch(final String types, final String at, final String searchtext) {
		if (StringUtils.isBlank(types)) {
			throw new EpickurIllegalArgument("The parameter types is not allowed to be null");
		} else {
			String[] typesArray = types.split(",");
			for (String temp : typesArray) {
				try {
					DishType.fromString(temp);
				} catch (IllegalArgumentException e) {
					throw new EpickurIllegalArgument(temp + " is not recongnize as a dish type");
				}
			}
		}
		if (StringUtils.isBlank(searchtext) && StringUtils.isBlank(at)) {
			throw new EpickurIllegalArgument("The parameter at or searchtext are not allowed to be null or empty at the same time");
		} else {
			if (!StringUtils.isBlank(at)) {
				String[] coordinates = at.split(",");
				if (coordinates.length != 2) {
					throw new EpickurIllegalArgument("The parameter at should contain 2 coordinates");
				} else {
					for (String temp : coordinates) {
						try {
							Double.valueOf(temp);
						} catch (NumberFormatException e) {
							throw new EpickurIllegalArgument(at + " is not a valid coordinate");
						}
					}
				}
			}
		}
	}
}
