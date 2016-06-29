package com.epickur.api.rest;

import com.epickur.api.entity.Key;
import com.epickur.api.entity.User;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.helper.EntityGenerator;
import com.epickur.api.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;

public class LoginControllerTest {

	@Mock
	private UserService userBusiness;
	@Mock
	private HttpServletRequest context;
	@InjectMocks
	private LoginController controller;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		Key key = EntityGenerator.generateRandomAdminKey();
		when(context.getAttribute("key")).thenReturn(key);
	}

	@Test
	public void testLogin() throws EpickurException {
		User user = EntityGenerator.generateRandomUserWithId();
		User userAfterCreate = EntityGenerator.mockUserAfterCreate(user);

		when(userBusiness.login(isA(String.class), isA(String.class))).thenReturn(userAfterCreate);

		ResponseEntity<?> actual = controller.login(user.getEmail(), user.getPassword());
		assertNotNull(actual);
		assertEquals(200, actual.getStatusCode().value());
		User actualUser = (User) actual.getBody();
		assertNotNull(actualUser.getId());
		assertNotNull(actualUser.getId());
		assertEquals(user.getName(), actualUser.getName());
		assertEquals(user.getEmail(), actualUser.getEmail());
		assertNotNull(actualUser.getAllow());
		assertNotNull(actualUser.getCreatedAt());
		assertNotNull(actualUser.getUpdatedAt());
	}
}
