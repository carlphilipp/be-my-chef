package com.epickur.api.exception.mapper;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;

/**
 * Called whenever an JSON mapping Exception occurs. It logs an error and build the response.
 * 
 * @author cph
 * @version 1.0
 */
@Provider
public final class JsonMappingExceptionMapper implements ExceptionMapper<JsonMappingException> {

	/** Logger **/
	private static final Logger LOG = LogManager.getLogger(JsonMappingExceptionMapper.class.getSimpleName());

	@Override
	public Response toResponse(final JsonMappingException exception) {
		DBObject bdb = BasicDBObjectBuilder.start().get();
		bdb.put("error", Response.Status.BAD_REQUEST.getStatusCode());
		bdb.put("message", Response.Status.BAD_REQUEST.getReasonPhrase());
		bdb.put("description", exception.getOriginalMessage());
		LOG.error(exception.getLocalizedMessage(), exception);
		return Response.status(Status.BAD_REQUEST).entity(bdb).build();
	}

}
