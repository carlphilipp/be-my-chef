package com.epickur.api.mapper;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.epickur.api.entity.message.ErrorMessage;
import com.epickur.api.exception.EpickurWrongAccessRights;

/**
 * @author cph
 * @version 1.0
 *
 */
public final class EpickurWrongAccessRightsMapper implements ExceptionMapper<EpickurWrongAccessRights> {

	/** Logger */
	private static final Logger LOG = LogManager.getLogger(EpickurWrongAccessRightsMapper.class.getSimpleName());

	@Override
	public Response toResponse(final EpickurWrongAccessRights exception) {
		ErrorMessage errorMessage = new ErrorMessage();
		errorMessage.setError(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
		errorMessage.setMessage(Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase());
		errorMessage.addDescription(exception.getMessage());
		LOG.error("Wrong access rights exception: " + exception.getMessage(), exception);
		return Response.status(Status.INTERNAL_SERVER_ERROR).entity(errorMessage).type(MediaType.APPLICATION_JSON).build();
	}
}
