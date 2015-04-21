package com.epickur.api.exception.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.ws.rs.core.Response;

import org.bson.Document;
import org.junit.Test;

import com.epickur.api.exception.EpickurDBException;
import com.epickur.api.service.ErrorService;
import com.mongodb.DBObject;
import com.mongodb.MongoException;

public class EpickurDBExceptionMapperTest {

	@Test
	public void testCreate() {
		EpickurDBExceptionMapper mapper = new EpickurDBExceptionMapper();
		EpickurDBException exception = new EpickurDBException("read", "message", new Document(), new Document(), new MongoException("message"));
		Response response = mapper.toResponse(exception);

		assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
		assertNotNull(response.getEntity());

		DBObject dbo = (DBObject) response.getEntity();
		assertNotNull(dbo.get("error"));
		assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), dbo.get("error"));
		assertNotNull(dbo.get("message"));
		assertEquals(ErrorService.INTERNAL_SERVER_ERROR, dbo.get("message"));
	}

	@Test
	public void testCreate2() {
		EpickurDBExceptionMapper mapper = new EpickurDBExceptionMapper();
		EpickurDBException exception = new EpickurDBException("read", "message", "id", new MongoException("message"));
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
