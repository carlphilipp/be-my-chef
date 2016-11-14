package com.epickur.api.rest;

import com.epickur.api.entity.Key;
import com.epickur.api.entity.User;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.helper.EntityGenerator;
import com.epickur.api.service.UserService;
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
import static org.mockito.Matchers.anyString;

@RunWith(MockitoJUnitRunner.class)
public class LoginControllerTest {

	@Mock
	private UserService userBusiness;
	@Mock
	private HttpServletRequest context;
	@InjectMocks
	private LoginController controller;

	@Before
	public void setUp() {
		Key key = EntityGenerator.generateRandomAdminKey();
		given(context.getAttribute("key")).willReturn(key);
	}

	@Test
	public void testLogin() throws EpickurException {
		// Given
		User user = EntityGenerator.generateRandomUserWithId();
		User userAfterCreate = EntityGenerator.mockUserAfterCreate(user);
		given(userBusiness.login(anyString(), anyString())).willReturn(userAfterCreate);

		// When
		ResponseEntity<?> actual = controller.login(user.getEmail(), user.getPassword());

		// Then
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
