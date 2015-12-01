package com.epickur.api.rest;

import com.epickur.api.entity.Key;
import com.epickur.api.entity.message.SuccessMessage;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.helper.EntityGenerator;
import com.epickur.api.service.KeyService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

public class LogoutControllerTest {

	@Mock
	private KeyService keyService;
	@Mock
	private HttpServletRequest context;
	@InjectMocks
	private LogoutController controller;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		
		Key key = EntityGenerator.generateRandomAdminKey();
		when(context.getAttribute("key")).thenReturn(key);
	}

	@Test
	public void testLogout() throws EpickurException {
		String key = EntityGenerator.generateRandomString();
		ResponseEntity<?> actual = controller.logout(key);
		assertNotNull(actual);
		assertEquals(200, actual.getStatusCode().value());
		SuccessMessage message = (SuccessMessage)  actual.getBody();
		assertNotNull(message);
		assertEquals("success", message.getResult());
		verify(keyService, times(1)).deleteWithKey(key);
	}
}
