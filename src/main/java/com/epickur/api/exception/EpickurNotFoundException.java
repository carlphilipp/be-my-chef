package com.epickur.api.exception;

/**
 * @author cph
 * @version 1.0
 * 
 */
public final class EpickurNotFoundException extends EpickurException {

	/** **/
	private static final long serialVersionUID = 1L;

	/**
	 * @param message
	 *            The message
	 * @param id
	 *            The Id
	 */
	public EpickurNotFoundException(final String message, final String id) {
		super(message + ": " + id);
	}
}
