package com.epickur.api.exception.mapper;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;

import com.epickur.api.exception.EpickurNotFoundException;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;

/**
 * @author cph
 * @version 1.0
 * 
 */
public final class EpickurNotFoundExceptionMapper implements ExceptionMapper<EpickurNotFoundException> {

	@Override
	public Response toResponse(final EpickurNotFoundException exception) {
		DBObject bdb = BasicDBObjectBuilder.start().get();
		bdb.put("error", Response.Status.NOT_FOUND.getStatusCode());
		bdb.put("message", Response.Status.NOT_FOUND.getReasonPhrase());
		if (exception != null && exception.getMessage() != null && !exception.getMessage().equals("")) {
			bdb.put("description", exception.getMessage());
		}
		return Response.status(Status.NOT_FOUND).entity(bdb).build();
	}
}
