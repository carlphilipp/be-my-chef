package com.epickur.api.exception.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.ws.rs.core.Response;

import org.bson.Document;
import org.junit.Test;

import com.epickur.api.entity.message.ErrorMessage;
import com.epickur.api.exception.EpickurDBException;
import com.epickur.api.mapper.EpickurDBExceptionMapper;
import com.epickur.api.utils.ErrorUtils;
import com.mongodb.MongoException;

public class EpickurDBExceptionMapperTest {

	@Test
	public void testCreate() {
		EpickurDBExceptionMapper mapper = new EpickurDBExceptionMapper();
		EpickurDBException exception = new EpickurDBException("read", "message", new Document(), new Document(), new MongoException("message"));
		Response response = mapper.toResponse(exception);

		assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
		assertNotNull(response.getEntity());

		ErrorMessage dbo = (ErrorMessage) response.getEntity();
		assertNotNull(dbo.getError());
		assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), dbo.getError().intValue());
		assertNotNull(dbo.getMessage());
		assertEquals(ErrorUtils.INTERNAL_SERVER_ERROR, dbo.getMessage());
	}

	@Test
	public void testCreate2() {
		EpickurDBExceptionMapper mapper = new EpickurDBExceptionMapper();
		EpickurDBException exception = new EpickurDBException("read", "message", "id", new MongoException("message"));
		Response response = mapper.toResponse(exception);

		assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
		assertNotNull(response.getEntity());

		ErrorMessage dbo = (ErrorMessage) response.getEntity();
		assertNotNull(dbo.getError());
		assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), dbo.getError().intValue());
		assertNotNull(dbo.getMessage());
		assertEquals(ErrorUtils.INTERNAL_SERVER_ERROR, dbo.getMessage());
	}
}
