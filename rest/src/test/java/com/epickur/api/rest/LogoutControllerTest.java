package com.epickur.api.rest;

import com.epickur.api.entity.Key;
import com.epickur.api.entity.message.SuccessMessage;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.helper.EntityGenerator;
import com.epickur.api.service.KeyService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@RunWith(MockitoJUnitRunner.class)
public class LogoutControllerTest {

	@Mock
	private KeyService keyService;
	@Mock
	private HttpServletRequest context;
	@InjectMocks
	private LogoutController controller;

	@Before
	public void setUp() {
		Key key = EntityGenerator.generateRandomAdminKey();
		given(context.getAttribute("key")).willReturn(key);
	}

	@Test
	public void testLogout() throws EpickurException {
		// Given
		String key = EntityGenerator.generateRandomString();

		// When
		ResponseEntity<?> actual = controller.logout(key);

		// Then
		assertNotNull(actual);
		assertEquals(200, actual.getStatusCode().value());
		SuccessMessage message = (SuccessMessage) actual.getBody();
		assertNotNull(message);
		assertEquals("success", message.getResult());
		then(keyService).should().deleteWithKey(key);
	}
}
