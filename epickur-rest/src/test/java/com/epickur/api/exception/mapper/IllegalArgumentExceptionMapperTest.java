package com.epickur.api.exception.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.ws.rs.core.Response;

import org.junit.Test;

import com.epickur.api.entity.message.ErrorMessage;
import com.epickur.api.mapper.IllegalArgumentExceptionMapper;

public class IllegalArgumentExceptionMapperTest {

	@Test
	public void testCreate() {
		IllegalArgumentExceptionMapper mapper = new IllegalArgumentExceptionMapper();
		IllegalArgumentException exception = new IllegalArgumentException("Argument null");
		Response response = mapper.toResponse(exception);

		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
		assertNotNull(response.getEntity());

		ErrorMessage errorMessage = (ErrorMessage) response.getEntity();
		assertNotNull(errorMessage.getError());
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), errorMessage.getError().intValue());
		assertNotNull(errorMessage.getMessage());
		assertEquals(Response.Status.BAD_REQUEST.getReasonPhrase(), errorMessage.getMessage());
		assertNotNull(errorMessage.getDescriptions());
		assertEquals(1, errorMessage.getDescriptions().size());
		assertEquals("Argument null", errorMessage.getDescriptions().get(0));
	}
}