package com.epickur.api.mapper;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.epickur.api.exception.EpickurDuplicateKeyException;
import com.epickur.api.utils.ErrorUtils;

/**
 * @author cph
 * @version 1.0
 *
 */
@Provider
@Produces(MediaType.APPLICATION_JSON)
public final class EpickurDuplicateKeyExceptionMapper implements ExceptionMapper<EpickurDuplicateKeyException> {

	@Override
	public Response toResponse(final EpickurDuplicateKeyException exception) {
		return ErrorUtils.error(Response.Status.CONFLICT, exception.getLocalizedMessage());
	}
}
