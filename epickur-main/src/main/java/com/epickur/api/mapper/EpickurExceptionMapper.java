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
import com.epickur.api.exception.EpickurDBException;
import com.epickur.api.exception.EpickurDuplicateKeyException;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.exception.EpickurNotFoundException;
import com.epickur.api.exception.EpickurParsingException;
import com.epickur.api.exception.GeoLocationException;
import com.epickur.api.exception.OrderStatusException;
import com.epickur.api.utils.ErrorUtils;

/**
 * Called whenever an Epickur Exception occurs. It logs an error and build the response.
 * 
 * @author cph
 * @version 1.0
 */
@Provider
@Produces(MediaType.APPLICATION_JSON)
public final class EpickurExceptionMapper implements ExceptionMapper<EpickurException> {

	/** Logger */
	private static final Logger LOG = LogManager.getLogger(EpickurExceptionMapper.class.getSimpleName());

	@Override
	public Response toResponse(final EpickurException exception) {
		if (exception instanceof EpickurNotFoundException) {
			ErrorMessage errorMessage = new ErrorMessage();
			errorMessage.setError(Response.Status.NOT_FOUND.getStatusCode());
			errorMessage.setMessage(Response.Status.NOT_FOUND.getReasonPhrase());
			if (exception != null && !StringUtils.isBlank(exception.getMessage())) {
				errorMessage.addDescription(exception.getMessage());
			}
			return Response.status(Status.NOT_FOUND).entity(errorMessage).type(MediaType.APPLICATION_JSON).build();
		} else if (exception instanceof EpickurParsingException) {
			LOG.error(exception.getLocalizedMessage(), exception);
			return ErrorUtils.error(Response.Status.INTERNAL_SERVER_ERROR, ErrorUtils.INTERNAL_SERVER_ERROR);
		} else if (exception instanceof EpickurDuplicateKeyException) {
			return ErrorUtils.error(Response.Status.CONFLICT, exception.getLocalizedMessage());
		} else if (exception instanceof EpickurDBException) {
			EpickurDBException ex = (EpickurDBException) exception;
			StringBuilder stb = new StringBuilder();
			stb.append("Request " + ex.getOperation() + " failed");

			if (ex.getDocument() != null) {
				stb.append(" with: " + ex.getDocument());
			}
			if (ex.getId() != null) {
				stb.append(" - id: " + ex.getId());
			}
			if (ex.getUpdate() != null) {
				stb.append(" - update: " + ex.getUpdate());
			}
			LOG.error(exception.getLocalizedMessage() + " - " + stb, ex);
			return ErrorUtils.error(Response.Status.INTERNAL_SERVER_ERROR, ErrorUtils.INTERNAL_SERVER_ERROR);
		} else if (exception instanceof GeoLocationException) {
			LOG.error("Here exception: {}", exception.getLocalizedMessage(), exception);
			return ErrorUtils.error(Response.Status.INTERNAL_SERVER_ERROR, exception.getLocalizedMessage());
		} else if (exception instanceof OrderStatusException) {
			ErrorMessage errorMessage = new ErrorMessage();
			errorMessage.setError(Response.Status.BAD_REQUEST.getStatusCode());
			errorMessage.setMessage(Response.Status.BAD_REQUEST.getReasonPhrase());
			if (exception != null && !StringUtils.isBlank(exception.getMessage())) {
				errorMessage.addDescription(exception.getMessage());
			}
			return Response.status(Status.BAD_REQUEST).entity(errorMessage).type(MediaType.APPLICATION_JSON).build();
		} else {
			LOG.error(exception.getLocalizedMessage(), exception);
			return ErrorUtils.error(Response.Status.INTERNAL_SERVER_ERROR, ErrorUtils.INTERNAL_SERVER_ERROR);
		}
	}

}
