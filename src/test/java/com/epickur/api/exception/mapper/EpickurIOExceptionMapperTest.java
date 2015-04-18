package com.epickur.api.exception.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.ws.rs.core.Response;

import org.junit.Test;

import com.epickur.api.exception.EpickurIOException;
import com.mongodb.DBObject;

public class EpickurIOExceptionMapperTest {

	@Test
	public void testCreate() {
		EpickurIOExceptionMapper mapper = new EpickurIOExceptionMapper();
		EpickurIOException exception = new EpickurIOException("bad");
		Response response = mapper.toResponse(exception);
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
		assertNotNull(response.getEntity());

		DBObject dbo = (DBObject) response.getEntity();
		assertNotNull(dbo.get("error"));
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), dbo.get("error"));
		assertNotNull(dbo.get("message"));
		assertEquals(Response.Status.BAD_REQUEST.getReasonPhrase(), dbo.get("message"));
		assertNotNull(dbo.get("description"));
		assertEquals("bad", dbo.get("description"));
	}
}
