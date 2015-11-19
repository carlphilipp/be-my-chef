package com.epickur.api.mapper;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.commons.lang3.StringUtils;

import com.epickur.api.entity.message.ErrorMessage;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

/**
 * Called whenever an Unrecognized Property Exception occurs. It logs an error and build the response.
 * 
 * @author cph
 * @version 1.0
 */
@Provider
@Produces(MediaType.APPLICATION_JSON)
public final class UnrecognizedPropertyExceptionMapper implements ExceptionMapper<UnrecognizedPropertyException> {

	@Override
	public Response toResponse(final UnrecognizedPropertyException exception) {
		ErrorMessage errorMessage = new ErrorMessage();
		errorMessage.setError(Response.Status.BAD_REQUEST.getStatusCode());
		errorMessage.setMessage(Response.Status.BAD_REQUEST.getReasonPhrase());
		if (exception != null && !StringUtils.isBlank(exception.getMessage())) {
			errorMessage.addDescription("Unrecognized field " + exception.getPropertyName() + "");
		}
		return Response.status(Status.BAD_REQUEST).entity(errorMessage).type(MediaType.APPLICATION_JSON).build();
	}
}
