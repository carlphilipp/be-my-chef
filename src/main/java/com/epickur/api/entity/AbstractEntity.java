package com.epickur.api.entity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import com.epickur.api.exception.EpickurParsingException;
import com.epickur.api.utils.ObjectMapperWrapperAPI;
import com.epickur.api.utils.ObjectMapperWrapperDB;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.util.JSONParseException;

/**
 * Abstract entity
 * 
 * @author cph
 * @version 1.0
 */
public abstract class AbstractEntity implements IEntity {

	/** Logger **/
	private static final Logger LOG = LogManager.getLogger(AbstractEntity.class.getSimpleName());

	@JsonIgnore
	@Override
	public final Document getAPIView() throws EpickurParsingException {
		String json = null;
		try {
			ObjectMapper om = ObjectMapperWrapperAPI.getInstance();
			json = om.writeValueAsString(this);
			return Document.parse(json);
		} catch (JsonProcessingException e) {
			throw new EpickurParsingException("Can not convert object to string", e);
		} catch (JSONParseException e) {
			throw new EpickurParsingException(json, e);
		}
	}

	@JsonIgnore
	@Override
	public final Document getDBView() throws EpickurParsingException {
		String json = null;
		try {
			ObjectMapper om = ObjectMapperWrapperDB.getInstance();
			json = om.writeValueAsString(this);
			return Document.parse(json);
		} catch (JsonProcessingException e) {
			throw new EpickurParsingException("Can not convert object to string", e);
		} catch (JSONParseException e) {
			throw new EpickurParsingException(json, e);
		}
	}

	/**
	 * Get a api view for the current object
	 * 
	 * @return A String
	 * @throws EpickurParsingException
	 *             If an epickur exception occurred
	 */
	public final String toStringAPIView() throws EpickurParsingException {
		return getAPIView().toJson();
	}

	@Override
	public final String toString() {
		try {
			ObjectMapper om = ObjectMapperWrapperAPI.getInstance();
			return om.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			LOG.error(e.getLocalizedMessage(), e);
			return "error";
		}
	}

	@Override
	public abstract int hashCode();

	@Override
	public abstract boolean equals(Object obj);
}
