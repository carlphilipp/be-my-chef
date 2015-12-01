package com.epickur.api.exception;

/**
 * @author cph
 * @version 1.0
 *
 */
public class EpickurForbiddenException extends RuntimeException {

	/**
	 * Serializer
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Construct a Epickur Forbidden Exception.
	 * 
	 */
	public EpickurForbiddenException() {
		super();
	}

	/**
	 * Construct a Epickur Forbidden Exception.
	 * 
	 * @param message
	 *            The message.
	 */
	public EpickurForbiddenException(final String message) {
		super(message);
	}

}
