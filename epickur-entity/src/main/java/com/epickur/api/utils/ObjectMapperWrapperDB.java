package com.epickur.api.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;

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
					mapper.setPropertyNamingStrategy(new MyStrategy());
				}
			}
		}
		return mapper;
	}
	
	/**
	 * @author cph
	 *
	 */
	public static final class MyStrategy extends PropertyNamingStrategy {
		/** Serializer **/
		private static final long serialVersionUID = 1L;

		@Override
		public String nameForField(final MapperConfig<?> config, final AnnotatedField field, final String defaultName) {
			return convert(defaultName);
		}

		@Override
		public String nameForGetterMethod(final MapperConfig<?> config, final AnnotatedMethod method, final String defaultName) {
			return convert(defaultName);
		}

		@Override
		public String nameForSetterMethod(final MapperConfig<?> config, final AnnotatedMethod method, final String defaultName) {
			return convert(defaultName);
		}

		/**
		 * @param defaultName
		 *            The default name
		 * @return a String
		 */
		private String convert(final String defaultName) {
			if (defaultName.equals("id")) {
				return "_id";
			}
			return defaultName;
		}
	}
}
