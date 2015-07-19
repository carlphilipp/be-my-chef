package com.epickur.api.exception.mapper;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;

/**
 * Called whenever an Unrecognized Property Exception occurs. It logs an error and build the response.
 * 
 * @author cph
 * @version 1.0
 */
@Provider
@Produces(MediaType.APPLICATION_JSON)
public final class UnrecognizedPropertyExceptionMapper implements ExceptionMapper<UnrecognizedPropertyException> {

	@Override
	public Response toResponse(final UnrecognizedPropertyException exception) {
		DBObject bdb = BasicDBObjectBuilder.start().get();
		bdb.put("error", Response.Status.BAD_REQUEST.getStatusCode());
		bdb.put("message", Response.Status.BAD_REQUEST.getReasonPhrase());
		bdb.put("description", "Unrecognized field " + exception.getPropertyName() + "");
		return Response.status(Status.BAD_REQUEST).entity(bdb).type(MediaType.APPLICATION_JSON).build();
	}

}
