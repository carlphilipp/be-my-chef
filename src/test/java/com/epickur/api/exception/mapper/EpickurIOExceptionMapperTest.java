package com.epickur.api.exception.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.ws.rs.core.Response;

import org.junit.Test;

import com.epickur.api.entity.message.ErrorMessage;
import com.epickur.api.exception.EpickurIOException;
import com.epickur.api.mapper.EpickurIOExceptionMapper;

public class EpickurIOExceptionMapperTest {

	@Test
	public void testCreate() {
		EpickurIOExceptionMapper mapper = new EpickurIOExceptionMapper();
		EpickurIOException exception = new EpickurIOException("bad");
		Response response = mapper.toResponse(exception);
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
		assertNotNull(response.getEntity());

		ErrorMessage errorMessage = (ErrorMessage) response.getEntity();
		assertNotNull(errorMessage.getError());
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), errorMessage.getError().intValue());
		assertNotNull(errorMessage.getMessage());
		assertEquals(Response.Status.BAD_REQUEST.getReasonPhrase(), errorMessage.getMessage());
		assertNotNull(errorMessage.getDescription());
		assertEquals("bad", errorMessage.getDescription());
	}
}
