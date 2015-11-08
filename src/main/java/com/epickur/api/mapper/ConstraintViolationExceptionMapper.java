package com.epickur.api.mapper;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.epickur.api.utils.ErrorUtils;

@Provider
@Produces(MediaType.APPLICATION_JSON)
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

	@Override
	public Response toResponse(ConstraintViolationException exception) {
		Set<ConstraintViolation<?>> constraints = exception.getConstraintViolations();
		// We only return the first error found
		ConstraintViolation<?> constraint = constraints.iterator().next();
		constraint.getMessage();
		return ErrorUtils.error(Response.Status.BAD_REQUEST, constraint.getMessage());
	}
}
