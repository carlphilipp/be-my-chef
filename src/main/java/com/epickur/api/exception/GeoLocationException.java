package com.epickur.api.exception;

/**
 * Calls whenever a geo location Exception occurs.
 * 
 * @author cph
 * @version 1.0
 */
public class GeoLocationException extends EpickurException {

	/** Serializer */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 * 
	 * @param message
	 *            The message
	 */
	public GeoLocationException(final String message) {
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
	public GeoLocationException(final String message, final Exception e) {
		super(message, e);
	}

}
