package com.epickur.api.exception;

import java.io.IOException;

public final class EpickurIOException extends IOException {

	/** **/
	private static final long serialVersionUID = 1L;

	public EpickurIOException() {
		super();
	}

	public EpickurIOException(final String message) {
		super(message);
	}

	public EpickurIOException(final String message, final Exception e) {
		super(message, e);
	}
}
