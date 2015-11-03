package com.epickur.api.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Response;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.epickur.api.InitMocks;
import com.epickur.api.TestUtils;
import com.epickur.api.business.UserBusiness;
import com.epickur.api.entity.Key;
import com.epickur.api.entity.User;
import com.epickur.api.exception.EpickurException;

public class LoginServiceTest extends InitMocks {

	private LoginService loginService;
	@Mock
	private UserBusiness userBusiness;
	@Mock
	private ContainerRequestContext context;
	
	@Before
	public void setUp(){
		loginService = new LoginService(userBusiness);
		Key key = TestUtils.generateRandomAdminKey();
		when(context.getProperty("key")).thenReturn(key);
	}
	
	@After
	public void tearDown() {
		loginService = null;
	}

	@Test
	public void testLogin() throws EpickurException {
		User user = TestUtils.generateRandomUserWithId();
		User userAfterCreate = TestUtils.mockUserAfterCreate(user);
		
		when(userBusiness.login(anyString(), anyString())).thenReturn(userAfterCreate);
		
		Response actual = loginService.login(user.getEmail(), user.getPassword());
		assertNotNull(actual);
		assertEquals(200, actual.getStatus());
		User actualUser = (User) actual.getEntity();
		assertNotNull(actualUser.getId());
		assertNotNull(actualUser.getId());
		assertEquals(user.getName(), actualUser.getName());
		assertEquals(user.getEmail(), actualUser.getEmail());
		assertNotNull(actualUser.getAllow());
		assertNotNull(actualUser.getCreatedAt());
		assertNotNull(actualUser.getUpdatedAt());
	}
}
