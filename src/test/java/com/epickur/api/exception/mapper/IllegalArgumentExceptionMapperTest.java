package com.epickur.api.exception.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.ws.rs.core.Response;

import org.junit.Test;

import com.epickur.api.mapper.IllegalArgumentExceptionMapper;
import com.mongodb.DBObject;

public class IllegalArgumentExceptionMapperTest {

	@Test
	public void testCreate() {
		IllegalArgumentExceptionMapper mapper = new IllegalArgumentExceptionMapper();
		IllegalArgumentException exception = new IllegalArgumentException("Argument null");
		Response response = mapper.toResponse(exception);

		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
		assertNotNull(response.getEntity());

		DBObject dbo = (DBObject) response.getEntity();
		assertNotNull(dbo.get("error"));
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), dbo.get("error"));
		assertNotNull(dbo.get("message"));
		assertEquals(Response.Status.BAD_REQUEST.getReasonPhrase(), dbo.get("message"));
		assertNotNull(dbo.get("description"));
		assertEquals("Argument null", dbo.get("description"));
	}
}
