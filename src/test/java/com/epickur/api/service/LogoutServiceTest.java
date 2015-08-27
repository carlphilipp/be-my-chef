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
import com.epickur.api.exception.EpickurException;
import com.mongodb.DBObject;

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
				User catererResult2 = (User) result2.getEntity();
				assertNotNull(catererResult2.getId());
				assertEquals(user.getName(), catererResult2.getName());
				assertEquals(user.getEmail(), catererResult2.getEmail());
				assertNotNull(catererResult2.getAllow());
				assertNotNull(catererResult2.getCreatedAt());
				assertNotNull(catererResult2.getUpdatedAt());
				assertNotNull(catererResult2.getKey());
				Response result3 = logoutService.logout(catererResult2.getKey());
				if (result3.getEntity() != null) {
					DBObject obj = (DBObject) result3.getEntity();
					assertEquals("success", obj.get("result"));
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
