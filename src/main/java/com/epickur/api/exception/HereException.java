package com.epickur.api.exception;

/**
 * Called whenever a Here Exception occurs.
 * 
 * @author cph
 * @version 1.0
 */
public class HereException extends GeoLocationException {

	/** Serializer */
	private static final long serialVersionUID = 1L;
	/** Connect error */
	public static final String CONNECT_ERROR = "Can't connect to here API";

	/**
	 * @param message
	 *            The message
	 */
	public HereException(final String message) {
		super(message);
	}

	/**
	 * @param message
	 *            The message
	 * @param e
	 *            The exception
	 */
	public HereException(final String message, final Exception e) {
		super(message, e);
	}
}
