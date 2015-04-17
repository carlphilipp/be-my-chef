package com.epickur.api.exception.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.ws.rs.core.Response;

import org.junit.Test;

import com.epickur.api.exception.EpickurException;
import com.epickur.api.service.ErrorService;
import com.mongodb.DBObject;

public class EpickurExceptionMapperTest {

	@Test
	public void testCreate() {
		EpickurExceptionMapper mapper = new EpickurExceptionMapper();
		EpickurException exception = new EpickurException();
		Response response = mapper.toResponse(exception);
		assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
		assertNotNull(response.getEntity());

		DBObject dbo = (DBObject) response.getEntity();
		assertNotNull(dbo.get("error"));
		assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), dbo.get("error"));
		assertNotNull(dbo.get("message"));
		assertEquals(ErrorService.INTERNAL_SERVER_ERROR, dbo.get("message"));
	}

}
