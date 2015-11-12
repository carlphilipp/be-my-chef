package com.epickur.api.exception.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.inject.Provider;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.server.ContainerRequest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.epickur.api.entity.Key;
import com.epickur.api.entity.message.ErrorMessage;
import com.epickur.api.exception.EpickurForbiddenException;
import com.epickur.api.mapper.EpickurForbiddenExceptionMapper;
import com.epickur.api.utils.ErrorUtils;

public class EpickurForbiddenExceptionMapperTest {

	@Mock
	private Provider<ContainerRequest> provider;
	@Mock
	private ContainerRequest context;
	@InjectMocks
	private EpickurForbiddenExceptionMapper mapper;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		Key key = new Key();
		Mockito.when(provider.get()).thenReturn(context);
		Mockito.when(context.getProperty("key")).thenReturn(key);
	}

	@Test
	public void testCreate() {
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
