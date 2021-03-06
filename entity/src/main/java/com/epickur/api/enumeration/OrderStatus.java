package com.epickur.api.enumeration;

/**
 * Order status
 * 
 * @author cph
 * @version 1.0
 *
 */
public enum OrderStatus {
	/** Pending */
	PENDING,
	/** Declined */
	DECLINED,
	/** Failed */
	FAILED,
	/** Successful */
	SUCCESSFUL,
	/** Canceled */
	CANCELED;

	/**
	 * Convert from string to order status.
	 * 
	 * @param value
	 *            The order status
	 * @return The Order status
	 */
	public static OrderStatus fromString(final String value) {
		if (value == null) {
			throw new IllegalArgumentException();
		}
		for (OrderStatus orderStatus : values()) {
			if (value.toLowerCase().equalsIgnoreCase(orderStatus.toString().toLowerCase())) {
				return orderStatus;
			}
		}
		throw new IllegalArgumentException();
	}
}