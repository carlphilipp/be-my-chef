package com.epickur.api.exception.mapper;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.epickur.api.exception.EpickurWrongAccessRights;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;

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
		LOG.error("Wrong access rights exception: " + exception.getMessage(), exception);
		DBObject bdb = BasicDBObjectBuilder.start().get();
		bdb.put("error", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
		bdb.put("message", Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase());
		return Response.status(Status.INTERNAL_SERVER_ERROR).entity(bdb).build();
	}

}
