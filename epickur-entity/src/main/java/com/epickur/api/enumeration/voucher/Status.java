package com.epickur.api.enumeration.voucher;

/**
 * @author cph
 * @version 1.0
 *
 */
public enum Status {
	/**
	 * Expired
	 */
	EXPIRED("expired"),
	/**
	 * Valid
	 */
	VALID("valid");
	
	/**
	 * The constructor
	 * 
	 * @param type
	 *            The type
	 */
	Status(final String type) {
		this.type = type;
	}

	/**
	 * @param value
	 *            The value to convert
	 * @return A Status
	 */
	public static Status fromString(final String value) {
		if (value == null) {
			throw new IllegalArgumentException();
		}
		for (Status status : values()) {
			if (value.equalsIgnoreCase(status.getType())) {
				return status;
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
		return type.toLowerCase();
	}

	/** The type */
	private String type;
}
