package com.epickur.api.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.epickur.api.entity.Key;
import com.epickur.api.entity.message.SuccessMessage;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.helper.EntityGenerator;
import com.epickur.api.rest.LogoutController;
import com.epickur.api.service.KeyService;

public class LogoutControllerTest {

	@Mock
	private KeyService keyService;
	@Mock
	private ContainerRequestContext context;
	@InjectMocks
	private LogoutController controller;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		
		Key key = EntityGenerator.generateRandomAdminKey();
		when(context.getProperty("key")).thenReturn(key);
	}

	@Test
	public void testLogout() throws EpickurException {
		String key = EntityGenerator.generateRandomString();
		Response actual = controller.logout(key);
		assertNotNull(actual);
		assertEquals(200, actual.getStatus());
		SuccessMessage message = (SuccessMessage) actual.getEntity();
		assertNotNull(message);
		assertEquals("success", message.getResult());
		verify(keyService, times(1)).deleteWithKey(key);
	}
}
