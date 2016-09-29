package com.epickur.api.enumeration;

/**
 * Currency enumeration
 *
 * @author cph
 * @version 1.0
 */
public enum Currency {
	/** The enum */
	AUD("Australia Dollar", "$", "AUD"), EUR("Euro", "€", "EUR"), USD("United States Dollar", "$", "USD");

	/**
	 * Constructor
	 *
	 * @param n
	 *            the name
	 * @param sy
	 *            the symbol
	 * @param c
	 *            the code
	 */
	Currency(final String n, final String sy, final String c) {
		this.name = n;
		this.symbol = sy;
		this.code = c;
	}

	/**
	 * Get the name
	 *
	 * @return the name
	 */
	public final String getName() {
		return name;
	}

	/**
	 * Get Symbol
	 *
	 * @return the symbol
	 */
	public final String getSymbol() {
		return symbol;
	}

	/**
	 * Get the code
	 *
	 * @return the code
	 */
	public final String getCode() {
		return code;
	}

	/**
	 * Get enum from str
	 *
	 * @param value
	 *            The str value.
	 * @return The currency
	 */
	public static Currency getEnum(final String value) {
		if (value == null) {
			throw new IllegalArgumentException();
		}
		for (final Currency c : values()) {
			if (value.equalsIgnoreCase(c.getCode())) {
				return c;
			}
		}
		throw new IllegalArgumentException();
	}

	/** name */
	private final String name;
	/** symbol */
	private final String symbol;
	/** code */
	private final String code;
}
