package com.epickur.api.exception.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.ws.rs.core.Response;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.mongodb.DBObject;

public class JsonMappingExceptionMapperTest {

	@Test
	public void testCreate() {
		JsonMappingExceptionMapper mapper = new JsonMappingExceptionMapper();
		JsonMappingException exception = new JsonMappingException("message");
		Response response = mapper.toResponse(exception);
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
		assertNotNull(response.getEntity());

		DBObject dbo = (DBObject) response.getEntity();
		assertNotNull(dbo.get("error"));
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), dbo.get("error"));
		assertNotNull(dbo.get("message"));
		assertEquals(Response.Status.BAD_REQUEST.getReasonPhrase(), dbo.get("message"));
		assertNotNull(dbo.get("description"));
		assertEquals("message", dbo.get("description"));
	}
}
