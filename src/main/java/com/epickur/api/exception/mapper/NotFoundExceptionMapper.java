package com.epickur.api.exception.mapper;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;

/**
 * Called whenever a not found Exception occurs. It logs an error and build the response.
 * 
 * @author cph
 * @version 1.0
 */
@Provider
@Produces(MediaType.APPLICATION_JSON)
public final class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {

	@Override
	public Response toResponse(final NotFoundException exception) {
		DBObject bdb = BasicDBObjectBuilder.start().get();
		bdb.put("error", Response.Status.NOT_FOUND.getStatusCode());
		bdb.put("message", Response.Status.NOT_FOUND.getReasonPhrase());
		bdb.put("description", "Endpoint not found");
		return Response.status(Status.NOT_FOUND).entity(bdb).build();
	}
}
