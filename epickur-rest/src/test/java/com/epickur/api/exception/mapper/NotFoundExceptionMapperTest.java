package com.epickur.api.exception.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;

import org.junit.Test;

import com.epickur.api.entity.message.ErrorMessage;
import com.epickur.api.mapper.NotFoundExceptionMapper;

public class NotFoundExceptionMapperTest {
	@Test
	public void testCreate() {
		NotFoundExceptionMapper mapper = new NotFoundExceptionMapper();
		NotFoundException exception = new NotFoundException();
		Response response = mapper.toResponse(exception);
		assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
		assertNotNull(response.getEntity());

		ErrorMessage errorMessage = (ErrorMessage) response.getEntity();
		assertNotNull(errorMessage.getError());
		assertEquals(Response.Status.NOT_FOUND.getStatusCode(), errorMessage.getError().intValue());
		assertNotNull(errorMessage.getMessage());
		assertEquals(Response.Status.NOT_FOUND.getReasonPhrase(), errorMessage.getMessage());
	}
}
