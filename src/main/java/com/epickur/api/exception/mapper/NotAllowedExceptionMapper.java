package com.epickur.api.exception.mapper;

import javax.ws.rs.NotAllowedException;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;

/**
 * Called whenever a not allowed exception happens. It logs an error and build the response.
 * 
 * @author cph
 * @version 1.0
 */
@Provider
@Produces(MediaType.APPLICATION_JSON)
public final class NotAllowedExceptionMapper implements ExceptionMapper<NotAllowedException> {

	@Override
	public Response toResponse(final NotAllowedException exception) {
		DBObject bdb = BasicDBObjectBuilder.start().get();
		bdb.put("error", Response.Status.METHOD_NOT_ALLOWED.getStatusCode());
		bdb.put("message", Response.Status.METHOD_NOT_ALLOWED.getReasonPhrase());
		return Response.status(Status.METHOD_NOT_ALLOWED).entity(bdb).build();
	}
}
