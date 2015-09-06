package com.epickur.api.exception.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;

import org.junit.Test;

import com.epickur.api.mapper.NotFoundExceptionMapper;
import com.mongodb.DBObject;

public class NotFoundExceptionMapperTest {
	@Test
	public void testCreate() {
		NotFoundExceptionMapper mapper = new NotFoundExceptionMapper();
		NotFoundException exception = new NotFoundException();
		Response response = mapper.toResponse(exception);
		assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
		assertNotNull(response.getEntity());

		DBObject dbo = (DBObject) response.getEntity();
		assertNotNull(dbo.get("error"));
		assertEquals(Response.Status.NOT_FOUND.getStatusCode(), dbo.get("error"));
		assertNotNull(dbo.get("message"));
		assertEquals(Response.Status.NOT_FOUND.getReasonPhrase(), dbo.get("message"));
		;
	}
}
