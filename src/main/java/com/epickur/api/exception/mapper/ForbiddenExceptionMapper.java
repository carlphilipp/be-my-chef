package com.epickur.api.exception.mapper;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.epickur.api.service.ErrorService;

/**
 * Called whenever an forbidden Exception occurs. It logs an error and build the response.
 * 
 * @author cph
 * @version 1.0
 */
@Provider
@Produces(MediaType.APPLICATION_JSON)
public final class ForbiddenExceptionMapper implements ExceptionMapper<ForbiddenException> {

	/** Logger **/
	private static final Logger LOG = LogManager.getLogger(ForbiddenExceptionMapper.class.getSimpleName());

	@Override
	public Response toResponse(final ForbiddenException exception) {
		LOG.error("Forbidden exception: " + exception.getLocalizedMessage(), exception);
		return ErrorService.error(Response.Status.FORBIDDEN, ErrorService.FORBIDDEN);
	}
}