package com.epickur.api.exception;

/**
 * Generic Epickur Exception.
 * 
 * @author cph
 * @version 1.0
 */
public class EpickurException extends Exception {

	/** Serializer **/
	private static final long serialVersionUID = 1L;

	/** Constructor **/
	public EpickurException() {
		super();
	}

	/**
	 * Constructor
	 * 
	 * @param message
	 *            The message
	 */
	public EpickurException(final String message) {
		super(message);
	}

	/**
	 * Constructor
	 * 
	 * @param message
	 *            The message
	 * @param e
	 *            The exception
	 */
	public EpickurException(final String message, final Exception e) {
		super(message, e);
	}
}
