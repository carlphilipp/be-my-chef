package com.epickur.api.exception.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.ws.rs.core.Response;

import org.junit.Test;

import com.epickur.api.entity.message.ErrorMessage;
import com.epickur.api.mapper.UnrecognizedPropertyExceptionMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

public class UnrecognizedPropertyExceptionMapperTest {

	@Test
	public void testCreate() {
		UnrecognizedPropertyExceptionMapper mapper = new UnrecognizedPropertyExceptionMapper();
		UnrecognizedPropertyException exception = new UnrecognizedPropertyException("error", null, null, "propName", null);
		Response response = mapper.toResponse(exception);
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
		assertNotNull(response.getEntity());

		ErrorMessage errorMessage = (ErrorMessage) response.getEntity();
		assertNotNull(errorMessage.getError());
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), errorMessage.getError().intValue());
		assertNotNull(errorMessage.getMessage());
		assertEquals(Response.Status.BAD_REQUEST.getReasonPhrase(), errorMessage.getMessage());
		assertNotNull(errorMessage.getDescription());
		assertEquals("Unrecognized field " + exception.getPropertyName(), errorMessage.getDescription());
	}
}
