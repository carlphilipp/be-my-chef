package com.epickur.api.exception;


/**
 * @author cph
 * @version 1.0
 *
 */
public final class OrderStatusException extends EpickurException {

	/** Serializer */
	private static final long serialVersionUID = 1L;

	/**
	 * Construct a order status exception
	 * 
	 * @param message
	 *            The message
	 */
	public OrderStatusException(final String message) {
		super(message);
	}
}
