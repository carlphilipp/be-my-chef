package com.epickur.api.exception;

/**
 * @author cph
 * @version 1.0
 *
 */
public class EpickurDuplicateKeyException extends EpickurDBException {

	/** Serialize */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 * 
	 * @param message
	 *            The message
	 */
	public EpickurDuplicateKeyException(final String message) {
		super(message);
	}

}
