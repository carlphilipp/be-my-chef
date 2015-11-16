package com.epickur.api.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.epickur.api.business.KeyBusiness;
import com.epickur.api.entity.Key;
import com.epickur.api.entity.message.SuccessMessage;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.helper.EntityGenerator;

public class LogoutServiceTest {

	@Mock
	private KeyBusiness keyBusiness;
	@Mock
	private ContainerRequestContext context;
	@InjectMocks
	private LogoutService logoutService;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		
		Key key = EntityGenerator.generateRandomAdminKey();
		when(context.getProperty("key")).thenReturn(key);
	}

	@Test
	public void testLogout() throws EpickurException {
		Response actual = logoutService.logout(EntityGenerator.generateRandomString());
		assertNotNull(actual);
		assertEquals(200, actual.getStatus());
		SuccessMessage message = (SuccessMessage) actual.getEntity();
		assertNotNull(message);
		assertEquals("success", message.getResult());
	}
}
