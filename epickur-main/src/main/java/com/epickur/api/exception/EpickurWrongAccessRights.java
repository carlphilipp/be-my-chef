package com.epickur.api.exception;

/**
 * @author cph
 * @version 1.0
 *
 */
public class EpickurWrongAccessRights extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Construct a Epickur Wrong Access Rights
	 */
	public EpickurWrongAccessRights() {
		super();
	}

	/**
	 * Construct a Epickur Wrong Access Rights
	 * 
	 * @param message
	 *            The message
	 */
	public EpickurWrongAccessRights(final String message) {
		super(message);
	}

}
