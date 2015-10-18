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

/**
 * Called whenever an Throwable occurs. It logs an error and build the response.
 * 
 * @author cph
 * @version 1.0
 */
@Provider
@Produces(MediaType.APPLICATION_JSON)
public final class ThrowableExeptionMapper implements ExceptionMapper<Throwable> {

	/** Logger */
	private static final Logger LOG = LogManager.getLogger(ThrowableExeptionMapper.class.getSimpleName());

	@Override
	public Response toResponse(final Throwable throwable) {
		ErrorMessage errorMessage = new ErrorMessage();
		errorMessage.setError(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
		errorMessage.setMessage(Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase());
		LOG.error("Fatal Error: " + throwable.getLocalizedMessage(), throwable);
		return Response.status(Status.INTERNAL_SERVER_ERROR).entity(errorMessage).type(MediaType.APPLICATION_JSON).build();
	}
}