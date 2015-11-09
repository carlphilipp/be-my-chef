package com.epickur.api.mapper;

import java.util.Iterator;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.epickur.api.entity.message.ErrorMessage;

@Provider
@Produces(MediaType.APPLICATION_JSON)
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

	@Override
	public Response toResponse(ConstraintViolationException exception) {
		ErrorMessage message = new ErrorMessage();
		message.setError(Response.Status.BAD_REQUEST.getStatusCode());
		message.setMessage(Response.Status.BAD_REQUEST.getReasonPhrase());
		Set<ConstraintViolation<?>> constraints = exception.getConstraintViolations();
		Iterator<ConstraintViolation<?>> iterator = constraints.iterator();
		while(iterator.hasNext()){
			ConstraintViolation<?> constraint = iterator.next();
			message.addDescription(constraint.getMessage());
		}
		return Response.status(Status.BAD_REQUEST).entity(message).type(MediaType.APPLICATION_JSON).build();
	}
}
