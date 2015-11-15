package com.epickur.api.mapper;

import javax.ws.rs.NotAllowedException;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.epickur.api.entity.message.ErrorMessage;

/**
 * Called whenever a not allowed exception happens. It logs an error and build the response.
 * 
 * @author cph
 * @version 1.0
 */
@Provider
@Produces(MediaType.APPLICATION_JSON)
public final class NotAllowedExceptionMapper implements ExceptionMapper<NotAllowedException> {

	@Override
	public Response toResponse(final NotAllowedException exception) {
		ErrorMessage errorMessage = new ErrorMessage();
		errorMessage.setError(Response.Status.METHOD_NOT_ALLOWED.getStatusCode());
		errorMessage.setMessage(Response.Status.METHOD_NOT_ALLOWED.getReasonPhrase());
		return Response.status(Status.METHOD_NOT_ALLOWED).entity(errorMessage).type(MediaType.APPLICATION_JSON).build();
	}
}
