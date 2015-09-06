package com.epickur.api.mapper;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.epickur.api.exception.HereException;
import com.epickur.api.utils.ErrorUtils;

/**
 * @author cph
 * @version 1.0
 *
 */
public final class HereExceptionMapper implements ExceptionMapper<HereException> {
	
	/** Logger */
	private static final Logger LOG = LogManager.getLogger(HereExceptionMapper.class.getSimpleName());

	@Override
	public Response toResponse(final HereException exception) {
		LOG.error("Here exception: " + exception.getLocalizedMessage(), exception);
		return ErrorUtils.error(Response.Status.INTERNAL_SERVER_ERROR, exception.getLocalizedMessage());
	}
}
