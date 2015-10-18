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
import com.epickur.api.entity.Key;
import com.epickur.api.entity.User;
import com.epickur.api.entity.message.SuccessMessage;
import com.epickur.api.exception.EpickurException;

public class LogoutServiceTest {

	private static LoginService loginService;
	private static LogoutService logoutService;
	private static UserService userService;
	private static List<ObjectId> idsToDelete;
	private static ContainerRequestContext context;

	@BeforeClass
	public static void beforeClass() {
		loginService = new LoginService();
		logoutService = new LogoutService();
		userService = new UserService();
		idsToDelete = new ArrayList<ObjectId>();
		context = mock(ContainerRequestContext.class);
		Key key = TestUtils.generateRandomAdminKey();
		Mockito.when(context.getProperty("key")).thenReturn(key);
	}

	@AfterClass
	public static void afterClass() throws EpickurException {
		for (ObjectId id : idsToDelete) {
			userService.delete(id.toHexString(), context);
		}
	}

	@Test
	public void testLogout() throws EpickurException {
		User user = TestUtils.generateRandomUser();
		String password = user.getPassword();
		Response result = userService.create(false, true, user, context);
		if (result.getEntity() != null) {
			User userResult = (User) result.getEntity();
			assertNotNull(userResult.getId());
			idsToDelete.add(userResult.getId());
			
			Response result2 = loginService.login(user.getEmail(), password);
			if (result2.getEntity() != null) {
				User userResult2 = (User) result2.getEntity();
				assertNotNull(userResult2.getId());
				assertEquals(user.getName(), userResult2.getName());
				assertEquals(user.getEmail(), userResult2.getEmail());
				assertNotNull(userResult2.getAllow());
				assertNotNull(userResult2.getCreatedAt());
				assertNotNull(userResult2.getUpdatedAt());
				assertNotNull(userResult2.getKey());
				Response result3 = logoutService.logout(userResult2.getKey());
				if (result3.getEntity() != null) {
					SuccessMessage obj = (SuccessMessage) result3.getEntity();
					assertEquals("success", obj.getResult());
				} else {
					fail("Logout returned is null");
				}
			} else {
				fail("Login returned is null");
			}
		} else {
			fail("User returned is null");
		}
	}
}
