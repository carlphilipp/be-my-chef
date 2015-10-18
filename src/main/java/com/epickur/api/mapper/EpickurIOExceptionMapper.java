package com.epickur.api.mapper;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.epickur.api.entity.message.ErrorMessage;
import com.epickur.api.exception.EpickurIOException;

/**
 * @author cph
 * @author 1.0
 * 
 */
@Provider
@Produces(MediaType.APPLICATION_JSON)
public final class EpickurIOExceptionMapper implements ExceptionMapper<EpickurIOException> {

	/** Logger */
	private static final Logger LOG = LogManager.getLogger(EpickurIllegalArgumentMapper.class.getSimpleName());

	@Override
	public Response toResponse(final EpickurIOException exception) {
		ErrorMessage errorMessage = new ErrorMessage();
		errorMessage.setError(Response.Status.BAD_REQUEST.getStatusCode());
		errorMessage.setMessage(Response.Status.BAD_REQUEST.getReasonPhrase());
		if (exception != null && !StringUtils.isBlank(exception.getMessage())) {
			errorMessage.setDescription(exception.getMessage());
		}
		LOG.error("Error: ", exception);
		return Response.status(Status.BAD_REQUEST).entity(errorMessage).type(MediaType.APPLICATION_JSON).build();
	}
}
