package com.epickur.api.exception;

/**
 * Called whenever a parsing exception occurs.
 * 
 * @author cph
 * @version 1.0
 */
public class EpickurParsingException extends EpickurException {

	/** Serializer */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 * 
	 * @param message
	 *            The message
	 * @param e
	 *            The exception
	 */
	public EpickurParsingException(final String message, final Exception e) {
		super(message, e);
	}
}
