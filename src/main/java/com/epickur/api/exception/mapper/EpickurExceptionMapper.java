package com.epickur.api.exception.mapper;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.epickur.api.exception.EpickurException;
import com.epickur.api.exception.EpickurParsingException;
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
		if (exception instanceof EpickurParsingException) {
			LOG.error("Epickur parsing exception: " + exception.getLocalizedMessage(), exception);
		} else {
			LOG.error("Epickur exception: " + exception.getLocalizedMessage(), exception);
		}
		return ErrorUtils.error(Response.Status.INTERNAL_SERVER_ERROR, ErrorUtils.INTERNAL_SERVER_ERROR);
	}

}
