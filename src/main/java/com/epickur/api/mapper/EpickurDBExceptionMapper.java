package com.epickur.api.mapper;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.epickur.api.exception.EpickurDBException;
import com.epickur.api.utils.ErrorUtils;

/**
 * Called whenever a DB exception occurs. It logs an error and build the response.
 * 
 * @author cph
 * @version 1.0
 */
@Provider
@Produces(MediaType.APPLICATION_JSON)
public final class EpickurDBExceptionMapper implements ExceptionMapper<EpickurDBException> {

	/** Logger */
	private static final Logger LOG = LogManager.getLogger(EpickurDBExceptionMapper.class.getSimpleName());

	@Override
	public Response toResponse(final EpickurDBException exception) {
		StringBuilder stb = new StringBuilder();
		stb.append("Request " + exception.getOperation() + " failed");
		
		if (exception.getDocument() != null) {
			stb.append(" with: " + exception.getDocument());
		}
		if (exception.getId() != null) {
			stb.append(" - id: " + exception.getId());
		}
		if (exception.getUpdate() != null) {
			stb.append(" - update: " + exception.getUpdate());
		}
		LOG.error(exception.getLocalizedMessage() + " - " + stb, exception);
		return ErrorUtils.error(Response.Status.INTERNAL_SERVER_ERROR, ErrorUtils.INTERNAL_SERVER_ERROR);
	}
}
