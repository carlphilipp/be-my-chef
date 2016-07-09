package com.epickur.api.enumeration;

/**
 * Order Mode
 *
 * @author cph
 * @version 1.0
 *
 */
public enum OrderMode {
	/** Pickup */
	PICKUP("pickup"),
	/** Chef */
	CHEF("chef");

	/**
	 * The constructor
	 *
	 * @param type
	 *            The type
	 */
	OrderMode(final String type) {
		this.type = type;
	}

	/**
	 * @param value
	 *            The value to convert
	 * @return A OrderMode
	 */
	public static OrderMode fromString(final String value) {
		if (value == null) {
			throw new IllegalArgumentException();
		}
		for (OrderMode dishType : values()) {
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
	private final String type;
}
