package com.epickur.api.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This class is an ObjectMapper Wrapper
 * 
 * @author cph
 * @version 1.0
 */
public final class ObjectMapperWrapperDB {

	/** ObjectMapper */
	private static volatile ObjectMapper mapper;
	/** Lock object */
	private static final Object LOCK = new Object();

	/** Constructor */
	private ObjectMapperWrapperDB() {
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
					mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
					mapper.setPropertyNamingStrategy(new Utils.MyStrategy());
				}
			}
		}
		return mapper;
	}
}
