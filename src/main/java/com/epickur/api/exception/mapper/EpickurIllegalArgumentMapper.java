package com.epickur.api.exception.mapper;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.epickur.api.exception.EpickurIllegalArgument;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;

/**
 * Called whenever an Epickur Illegal Exception occurs. It logs an error and build the response.
 * 
 * @author cph
 * @version 1.0
 */
@Provider
@Produces(MediaType.APPLICATION_JSON)
public final class EpickurIllegalArgumentMapper implements ExceptionMapper<EpickurIllegalArgument> {

	/** Logger **/
	private static final Logger LOG = LogManager.getLogger(EpickurIllegalArgumentMapper.class.getSimpleName());

	@Override
	public Response toResponse(final EpickurIllegalArgument exception) {
		DBObject bdb = BasicDBObjectBuilder.start().get();
		bdb.put("error", Response.Status.BAD_REQUEST.getStatusCode());
		bdb.put("message", Response.Status.BAD_REQUEST.getReasonPhrase());
		if (exception != null && exception.getMessage() != null && !exception.getMessage().equals("")) {
			bdb.put("description", exception.getMessage());
		}
		LOG.error("Error: ", exception);
		return Response.status(Status.BAD_REQUEST).entity(bdb).build();
	}
}
