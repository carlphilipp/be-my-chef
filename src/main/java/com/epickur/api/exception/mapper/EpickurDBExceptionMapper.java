package com.epickur.api.exception.mapper;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.epickur.api.exception.EpickurDBException;
import com.epickur.api.service.ErrorService;

/**
 * Called whenever a DB exception occurs. It logs an error and build the response.
 * 
 * @author cph
 * @version 1.0
 */
@Provider
public final class EpickurDBExceptionMapper implements ExceptionMapper<EpickurDBException> {

	/** Logger **/
	private static final Logger LOG = LogManager.getLogger(EpickurDBExceptionMapper.class.getSimpleName());

	@Override
	public Response toResponse(final EpickurDBException exception) {
		LOG.error("Request " + exception.getOperation() + " failed");
		if (exception.getDocument() != null) {
			LOG.error("With: " + exception.getDocument());
		}
		if (exception.getId() != null) {
			LOG.error("Id: " + exception.getId());
		}
		if (exception.getUpdate() != null) {
			LOG.error("Update: " + exception.getUpdate());
		}
		LOG.error(exception.getLocalizedMessage(), exception);
		return ErrorService.error(Response.Status.INTERNAL_SERVER_ERROR, ErrorService.INTERNAL_SERVER_ERROR);
	}
}
