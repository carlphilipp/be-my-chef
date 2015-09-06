package com.epickur.api.exception.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

import javax.ws.rs.NotAllowedException;
import javax.ws.rs.core.Response;

import org.junit.Test;

import com.epickur.api.mapper.NotAllowedExceptionMapper;
import com.mongodb.DBObject;

public class NotAllowedExceptionMapperTest {

	@Test
	public void testCreate() {
		NotAllowedException exception = mock(NotAllowedException.class);
		NotAllowedExceptionMapper mapper = new NotAllowedExceptionMapper();
		Response response = mapper.toResponse(exception);
		assertEquals(Response.Status.METHOD_NOT_ALLOWED.getStatusCode(), response.getStatus());
		assertNotNull(response.getEntity());

		DBObject dbo = (DBObject) response.getEntity();
		assertNotNull(dbo.get("error"));
		assertEquals(Response.Status.METHOD_NOT_ALLOWED.getStatusCode(), dbo.get("error"));
		assertNotNull(dbo.get("message"));
		assertEquals(Response.Status.METHOD_NOT_ALLOWED.getReasonPhrase(), dbo.get("message"));
	}
}
