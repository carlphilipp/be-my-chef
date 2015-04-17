package com.epickur.api.exception.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.ws.rs.core.Response;

import org.junit.Test;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.mongodb.DBObject;

public class UnrecognizedPropertyExceptionMapperTest {

	@Test
	public void testCreate() {
		UnrecognizedPropertyExceptionMapper mapper = new UnrecognizedPropertyExceptionMapper();
		UnrecognizedPropertyException exception = new UnrecognizedPropertyException("error", null, null, "propName", null);
		Response response = mapper.toResponse(exception);
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
		assertNotNull(response.getEntity());

		DBObject dbo = (DBObject) response.getEntity();
		assertNotNull(dbo.get("error"));
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), dbo.get("error"));
		assertNotNull(dbo.get("message"));
		assertEquals(Response.Status.BAD_REQUEST.getReasonPhrase(), dbo.get("message"));
		assertNotNull(dbo.get("description"));
		assertEquals("Unrecognized field " + exception.getPropertyName(), dbo.get("description"));
	}
}
