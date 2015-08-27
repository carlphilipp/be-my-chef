package com.epickur.api.enumeration.voucher;

/**
 * @author cph
 * @version 1.0
 *
 */
public enum DiscountType {
	/** Amount discount */
	AMOUNT("amount"),
	/** Percentage discount */
	PERCENTAGE("percentage");

	/**
	 * The constructor
	 * 
	 * @param type
	 *            The type
	 */
	DiscountType(final String type) {
		this.type = type;
	}

	/**
	 * @param value
	 *            The value to convert
	 * @return A DiscountType
	 */
	public static DiscountType fromString(final String value) {
		if (value == null) {
			throw new IllegalArgumentException();
		}
		for (DiscountType discType : values()) {
			if (value.equalsIgnoreCase(discType.getType())) {
				return discType;
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
