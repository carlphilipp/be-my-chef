package com.epickur.api.exception.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.ws.rs.core.Response;

import org.junit.Test;

import com.epickur.api.entity.message.ErrorMessage;
import com.epickur.api.exception.EpickurForbiddenException;
import com.epickur.api.mapper.EpickurForbiddenExceptionMapper;
import com.epickur.api.utils.ErrorUtils;

public class EpickurForbiddenExceptionMapperTest {
	@Test
	public void testCreate() {
		EpickurForbiddenExceptionMapper mapper = new EpickurForbiddenExceptionMapper();
		EpickurForbiddenException exception = new EpickurForbiddenException();
		Response response = mapper.toResponse(exception);

		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
		assertNotNull(response.getEntity());

		ErrorMessage dbo = (ErrorMessage) response.getEntity();
		assertNotNull(dbo.getError());
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), dbo.getError().intValue());
		assertNotNull(dbo.getMessage());
		assertEquals(ErrorUtils.FORBIDDEN, dbo.getMessage());
	}
}
