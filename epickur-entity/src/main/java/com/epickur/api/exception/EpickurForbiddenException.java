package com.epickur.api.exception;

import javax.ws.rs.ForbiddenException;

/**
 * @author cph
 * @version 1.0
 *
 */
public class EpickurForbiddenException extends ForbiddenException {

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
