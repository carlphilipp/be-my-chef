package com.epickur.api.exception.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.ws.rs.core.Response;

import org.junit.Test;

import com.epickur.api.entity.message.ErrorMessage;
import com.epickur.api.exception.EpickurIllegalArgument;
import com.epickur.api.mapper.EpickurIllegalArgumentMapper;

public class EpickurIllegalArgumentMapperTest {

	@Test
	public void testCreate() {
		EpickurIllegalArgumentMapper mapper = new EpickurIllegalArgumentMapper();
		EpickurIllegalArgument exception = new EpickurIllegalArgument("bad");
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
		assertEquals("bad", errorMessage.getDescriptions().get(0));
	}

}
