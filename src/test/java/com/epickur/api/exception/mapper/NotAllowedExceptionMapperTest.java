package com.epickur.api.exception.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

import javax.ws.rs.NotAllowedException;
import javax.ws.rs.core.Response;

import org.junit.Test;

import com.epickur.api.entity.message.ErrorMessage;
import com.epickur.api.mapper.NotAllowedExceptionMapper;

public class NotAllowedExceptionMapperTest {

	@Test
	public void testCreate() {
		NotAllowedException exception = mock(NotAllowedException.class);
		NotAllowedExceptionMapper mapper = new NotAllowedExceptionMapper();
		Response response = mapper.toResponse(exception);
		assertEquals(Response.Status.METHOD_NOT_ALLOWED.getStatusCode(), response.getStatus());
		assertNotNull(response.getEntity());

		ErrorMessage errorMessage = (ErrorMessage) response.getEntity();
		assertNotNull(errorMessage.getError());
		assertEquals(Response.Status.METHOD_NOT_ALLOWED.getStatusCode(), errorMessage.getError().intValue());
		assertNotNull(errorMessage.getMessage());
		assertEquals(Response.Status.METHOD_NOT_ALLOWED.getReasonPhrase(), errorMessage.getMessage());
	}
}
