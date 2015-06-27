package com.epickur.api.enumeration;

/**
 * @author cph
 * @version 1.0
 */
public enum DishType {
	/** Meat */
	MEAT("meat"),
	/** Fish */
	FISH("fish"),
	/** Vegan */
	VEGAN("vegan");

	/**
	 * The constructor
	 * 
	 * @param type
	 *            The type
	 */
	DishType(final String type) {
		this.type = type;
	}

	/**
	 * @param value
	 *            The value to convert
	 * @return A DishType
	 */
	public static DishType fromString(final String value) {
		if (value == null) {
			throw new IllegalArgumentException();
		}
		for (DishType dishType : values()) {
			if (value.equalsIgnoreCase(dishType.getType())) {
				return dishType;
			}
		}
		throw new IllegalArgumentException();
	}

	@Override
	public String toString() {
		return super.toString().toLowerCase();
	}

	/**
	 * @return A String
	 */
	public String getType() {
		return type;
	}

	/** The type */
	private String type;

}
