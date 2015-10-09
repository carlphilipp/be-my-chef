package com.epickur.api.entity;

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

	@JsonIgnore
	@Override
	public final Document getDocumentAPIView() throws EpickurParsingException {
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
	public final Document getDocumentDBView() throws EpickurParsingException {
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
		return getDocumentAPIView().toJson();
	}

	@Override
	public abstract int hashCode();

	@Override
	public abstract boolean equals(Object obj);
}
