package com.epickur.api.exception.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.core.Response;

import org.junit.Test;

import com.epickur.api.service.ErrorService;
import com.mongodb.DBObject;

public class ForbiddenExceptionMapperTest {
	@Test
	public void testCreate() {
		ForbiddenExceptionMapper mapper = new ForbiddenExceptionMapper();
		ForbiddenException exception = new ForbiddenException();
		Response response = mapper.toResponse(exception);

		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
		assertNotNull(response.getEntity());

		DBObject dbo = (DBObject) response.getEntity();
		assertNotNull(dbo.get("error"));
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), dbo.get("error"));
		assertNotNull(dbo.get("message"));
		assertEquals(ErrorService.FORBIDDEN, dbo.get("message"));
	}
}
