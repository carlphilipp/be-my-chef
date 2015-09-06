package com.epickur.api.enumeration.voucher;

/**
 * @author cph
 * @version 1.0
 *
 */
public enum ExpirationType {
	/**
	 * One time voucher
	 */
	ONETIME("onetime"),
	/**
	 * Until voucher
	 */
	UNTIL("until");
	
	/**
	 * The constructor
	 * 
	 * @param type
	 *            The type
	 */
	ExpirationType(final String type) {
		this.type = type;
	}

	/**
	 * @param value
	 *            The value to convert
	 * @return A ExpirationType
	 */
	public static ExpirationType fromString(final String value) {
		if (value == null) {
			throw new IllegalArgumentException();
		}
		for (ExpirationType expType : values()) {
			if (value.equalsIgnoreCase(expType.getType())) {
				return expType;
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
