package com.epickur.api.exception.mapper;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;

/**
 * Called whenever an Throwable occurs. It logs an error and build the response.
 * 
 * @author cph
 * @version 1.0
 */
@Provider
@Produces(MediaType.APPLICATION_JSON)
public final class ThrowableExeptionMapper implements ExceptionMapper<Throwable> {

	/** Logger */
	private static final Logger LOG = LogManager.getLogger(ThrowableExeptionMapper.class.getSimpleName());

	@Override
	public Response toResponse(final Throwable throwable) {
		LOG.error("Fatal Error: " + throwable.getMessage(), throwable);
		DBObject bdb = BasicDBObjectBuilder.start().get();
		bdb.put("error", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
		bdb.put("message", Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase());
		return Response.status(Status.INTERNAL_SERVER_ERROR).entity(bdb).type(MediaType.APPLICATION_JSON).build();
	}
}