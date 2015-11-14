package com.epickur.api.utils;

/**
 * @author cph
 * @version 1.0
 *
 */
public final class EpickurPriorities {
	/**
	 * Prevents construction
	 */
	private EpickurPriorities() {
	}

	/**
	 * Log priority.
	 */
	public static final int LOG = 1000;
	/**
	 * Security authentication filter/interceptor priority.
	 */
	public static final int AUTHENTICATION = 2000;
	/**
	 * Security authorization filter/interceptor priority.
	 */
	public static final int AUTHORIZATION = 3000;
	/**
	 * Header decorator filter/interceptor priority.
	 */
	public static final int HEADER_DECORATOR = 4000;
	/**
	 * Message encoder or decoder filter/interceptor priority.
	 */
	public static final int ENTITY_CODER = 5000;
	/**
	 * User-level filter/interceptor priority.
	 */
	public static final int USER = 6000;

}
