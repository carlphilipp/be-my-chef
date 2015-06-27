package com.epickur.api.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This class is an ObjectMapper Wrapper
 * 
 * @author cph
 * @version 1.0
 */
public final class ObjectMapperWrapperAPI {

	/** ObjectMapper */
	private static volatile ObjectMapper mapper;
	/** Lock object */
	private static final Object LOCK = new Object();

	/** Constructor */
	private ObjectMapperWrapperAPI() {
	}

	/**
	 * Access singleton instance
	 * 
	 * @return An ObjectMapper
	 */
	public static ObjectMapper getInstance() {
		if (mapper == null) {
			synchronized (LOCK) {
				if (mapper == null) {
					mapper = new ObjectMapper();
				}
			}
		}
		return mapper;
	}
}
