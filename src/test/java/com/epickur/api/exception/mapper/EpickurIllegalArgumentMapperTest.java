package com.epickur.api.exception.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.ws.rs.core.Response;

import org.junit.Test;

import com.epickur.api.exception.EpickurIllegalArgument;
import com.epickur.api.mapper.EpickurIllegalArgumentMapper;
import com.mongodb.DBObject;

public class EpickurIllegalArgumentMapperTest {

	@Test
	public void testCreate() {
		EpickurIllegalArgumentMapper mapper = new EpickurIllegalArgumentMapper();
		EpickurIllegalArgument exception = new EpickurIllegalArgument("bad");
		Response response = mapper.toResponse(exception);
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
		assertNotNull(response.getEntity());

		DBObject dbo = (DBObject) response.getEntity();
		assertNotNull(dbo.get("error"));
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), dbo.get("error"));
		assertNotNull(dbo.get("message"));
		assertEquals(Response.Status.BAD_REQUEST.getReasonPhrase(), dbo.get("message"));
		assertNotNull(dbo.get("description"));
		assertEquals("bad", dbo.get("description"));
	}

}
