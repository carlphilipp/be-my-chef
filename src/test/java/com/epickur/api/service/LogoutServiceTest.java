package com.epickur.api.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Response;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.epickur.api.InitMocks;
import com.epickur.api.TestUtils;
import com.epickur.api.business.KeyBusiness;
import com.epickur.api.entity.Key;
import com.epickur.api.entity.message.SuccessMessage;
import com.epickur.api.exception.EpickurException;

public class LogoutServiceTest extends InitMocks {

	private LogoutService logoutService;
	@Mock
	private KeyBusiness keyBusiness;
	@Mock
	private ContainerRequestContext context;
	
	@Before
	public void setUp(){
		logoutService = new LogoutService(keyBusiness);
		Key key = TestUtils.generateRandomAdminKey();
		when(context.getProperty("key")).thenReturn(key);
	}
	
	@After
	public void tearDown() {
		logoutService = null;
	}

	@Test
	public void testLogout() throws EpickurException {
		Response actual = logoutService.logout(TestUtils.generateRandomString());
		assertNotNull(actual);
		assertEquals(200, actual.getStatus());
		SuccessMessage message = (SuccessMessage) actual.getEntity();
		assertNotNull(message);
		assertEquals("success", message.getResult());
	}
}
