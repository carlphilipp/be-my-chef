package com.epickur.api.mapper;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.epickur.api.entity.message.ErrorMessage;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * Called whenever an JSON mapping Exception occurs. It logs an error and build the response.
 * 
 * @author cph
 * @version 1.0
 */
@Provider
@Produces(MediaType.APPLICATION_JSON)
public final class JsonMappingExceptionMapper implements ExceptionMapper<JsonMappingException> {

	/** Logger */
	private static final Logger LOG = LogManager.getLogger(JsonMappingExceptionMapper.class.getSimpleName());

	@Override
	public Response toResponse(final JsonMappingException exception) {
		ErrorMessage errorMessage = new ErrorMessage();
		errorMessage.setError(Response.Status.BAD_REQUEST.getStatusCode());
		errorMessage.setMessage(Response.Status.BAD_REQUEST.getReasonPhrase());
		errorMessage.addDescription(exception.getOriginalMessage());
		LOG.error(exception.getLocalizedMessage(), exception);
		return Response.status(Status.BAD_REQUEST).entity(errorMessage).type(MediaType.APPLICATION_JSON).build();
	}

}
