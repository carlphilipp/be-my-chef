package com.epickur.api.exception.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.ws.rs.core.Response;

import org.junit.Test;

import com.epickur.api.entity.message.ErrorMessage;
import com.epickur.api.mapper.ThrowableExeptionMapper;

public class ThrowableExeptionMapperTest {

	@Test
	public void testCreate() {
		ThrowableExeptionMapper mapper = new ThrowableExeptionMapper();
		Throwable exception = new Throwable();
		Response response = mapper.toResponse(exception);
		assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
		assertNotNull(response.getEntity());

		ErrorMessage errorMessage = (ErrorMessage) response.getEntity();
		assertNotNull(errorMessage.getError());
		assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), errorMessage.getError().intValue());
		assertNotNull(errorMessage.getMessage());
		assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase(), errorMessage.getMessage());
	}
}
