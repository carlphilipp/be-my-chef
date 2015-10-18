package com.epickur.api.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Response;

import org.bson.types.ObjectId;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import com.epickur.api.TestUtils;
import com.epickur.api.business.KeyBusiness;
import com.epickur.api.entity.Key;
import com.epickur.api.entity.User;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.exception.EpickurIllegalArgument;

public class LoginServiceTest {

	private static LoginService loginService;
	private static UserService userService;
	private static KeyBusiness keyBusiness;
	private static List<ObjectId> idsToDelete;
	private static List<String> keysToDelete;
	private static ContainerRequestContext context;

	@BeforeClass
	public static void beforeClass() {
		loginService = new LoginService();
		userService = new UserService();
		keyBusiness = new KeyBusiness();
		idsToDelete = new ArrayList<ObjectId>();
		keysToDelete = new ArrayList<String>();
		context = mock(ContainerRequestContext.class);
		Key key = TestUtils.generateRandomAdminKey();
		Mockito.when(context.getProperty("key")).thenReturn(key);
	}

	@AfterClass
	public static void afterClass() throws EpickurException {
		for (ObjectId id : idsToDelete) {
			userService.delete(id.toHexString(), context);
		}
		for (String id : keysToDelete) {
			keyBusiness.deleteWithKey(id);
		}
	}

	@Test
	public void testLogin() throws EpickurException {
		User user = TestUtils.generateRandomUser();
		String password = user.getPassword();
		Response result = userService.create(false, true, user, context);
		if (result.getEntity() != null) {
			User userResult = (User) result.getEntity();
			assertNotNull(userResult.getId());
			idsToDelete.add(userResult.getId());

			Response result2 = loginService.login(user.getEmail(), password);
			if (result2.getEntity() != null) {
				User catererResult2 = (User) result2.getEntity();
				keysToDelete.add(catererResult2.getKey());
				assertNotNull(catererResult2.getId());
				assertEquals(user.getName(), catererResult2.getName());
				assertEquals(user.getEmail(), catererResult2.getEmail());
				assertNotNull(catererResult2.getAllow());
				assertNotNull(catererResult2.getCreatedAt());
				assertNotNull(catererResult2.getUpdatedAt());
			} else {
				fail("Login returned is null");
			}
		} else {
			fail("User returned is null");
		}
	}

	@Test(expected = EpickurIllegalArgument.class)
	public void testLoginFail() throws EpickurException {
		User user = TestUtils.generateRandomUser();
		String password = user.getPassword();
		loginService.login(null, password);
	}
}
