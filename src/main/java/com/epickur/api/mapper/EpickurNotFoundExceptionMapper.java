package com.epickur.api.mapper;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.commons.lang3.StringUtils;

import com.epickur.api.entity.message.ErrorMessage;
import com.epickur.api.exception.EpickurNotFoundException;

/**
 * @author cph
 * @version 1.0
 * 
 */
@Provider
@Produces(MediaType.APPLICATION_JSON)
public final class EpickurNotFoundExceptionMapper implements ExceptionMapper<EpickurNotFoundException> {

	@Override
	public Response toResponse(final EpickurNotFoundException exception) {
		ErrorMessage errorMessage = new ErrorMessage();
		errorMessage.setError(Response.Status.NOT_FOUND.getStatusCode());
		errorMessage.setMessage(Response.Status.NOT_FOUND.getReasonPhrase());
		if (exception != null && !StringUtils.isBlank(exception.getMessage())) {
			errorMessage.addDescription(exception.getMessage());
		}
		return Response.status(Status.NOT_FOUND).entity(errorMessage).type(MediaType.APPLICATION_JSON).build();
	}
}
