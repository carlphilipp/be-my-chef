package com.epickur.api.exception;

/**
 * Called whenever an argument is illegal.
 * 
 * @author cph
 * @version 1.0
 */
public class EpickurIllegalArgument extends IllegalArgumentException {

	/** Serializer */
	private static final long serialVersionUID = 1L;

	/** Constructor */
	public EpickurIllegalArgument() {
		super();
	}

	/**
	 * @param message
	 *            The message
	 */
	public EpickurIllegalArgument(final String message) {
		super(message);
	}
}
