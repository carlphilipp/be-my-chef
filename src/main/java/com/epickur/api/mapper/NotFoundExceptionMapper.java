package com.epickur.api.mapper;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.epickur.api.entity.message.ErrorMessage;

/**
 * Called whenever a not found Exception occurs. It logs an error and build the response.
 * 
 * @author cph
 * @version 1.0
 */
@Provider
@Produces(MediaType.APPLICATION_JSON)
public final class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {

	@Override
	public Response toResponse(final NotFoundException exception) {
		ErrorMessage errorMessage = new ErrorMessage();
		errorMessage.setError(Response.Status.NOT_FOUND.getStatusCode());
		errorMessage.setMessage(Response.Status.NOT_FOUND.getReasonPhrase());
		errorMessage.setDescription("Endpoint not found");
		return Response.status(Status.NOT_FOUND).type(MediaType.APPLICATION_JSON).entity(errorMessage).build();
	}
}
