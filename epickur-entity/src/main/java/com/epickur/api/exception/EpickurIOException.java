package com.epickur.api.exception;

import java.io.IOException;

/**
 * @author cph
 * @author 1.0
 * 
 */
public final class EpickurIOException extends IOException {

	/** Serializer */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 */
	public EpickurIOException() {
		super();
	}

	/**
	 * Constructor
	 * 
	 * @param message
	 *            The message
	 */
	public EpickurIOException(final String message) {
		super(message);
	}

	/**
	 * @param message
	 *            The message
	 * @param e
	 *            An Exception
	 */
	public EpickurIOException(final String message, final Exception e) {
		super(message, e);
	}
}
