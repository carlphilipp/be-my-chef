package com.epickur.api.mapper;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.epickur.api.exception.EpickurForbiddenException;
import com.epickur.api.utils.ErrorUtils;

/**
 * Called whenever an forbidden Exception occurs. It logs an error and build the response.
 * 
 * @author cph
 * @version 1.0
 */
@Provider
@Produces(MediaType.APPLICATION_JSON)
public final class EpickurForbiddenExceptionMapper implements ExceptionMapper<EpickurForbiddenException> {

	/** Logger */
	private static final Logger LOG = LogManager.getLogger(EpickurForbiddenExceptionMapper.class.getSimpleName());

	@Override
	public Response toResponse(final EpickurForbiddenException exception) {
		LOG.warn("Forbidden exception: " + exception.getLocalizedMessage(), exception);
		return ErrorUtils.error(Response.Status.FORBIDDEN, ErrorUtils.FORBIDDEN);
	}
}