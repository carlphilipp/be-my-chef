package com.epickur.api.exception.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.ws.rs.core.Response;

import org.junit.Test;

import com.epickur.api.mapper.ThrowableExeptionMapper;
import com.mongodb.DBObject;

public class ThrowableExeptionMapperTest {

	@Test
	public void testCreate() {
		ThrowableExeptionMapper mapper = new ThrowableExeptionMapper();
		Throwable exception = new Throwable();
		Response response = mapper.toResponse(exception);
		assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
		assertNotNull(response.getEntity());

		DBObject dbo = (DBObject) response.getEntity();
		assertNotNull(dbo.get("error"));
		assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), dbo.get("error"));
		assertNotNull(dbo.get("message"));
		assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase(), dbo.get("message"));
	}
}
