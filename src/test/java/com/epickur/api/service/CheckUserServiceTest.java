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
import com.epickur.api.exception.EpickurIllegalArgument;
import com.mongodb.DBObject;

public class CheckUserServiceTest {

	private static NoKeyService noKeyService;
	private static UserService userService;
	private static List<ObjectId> idsToDelete;
	private static ContainerRequestContext context;

	@BeforeClass
	public static void beforeClass() {
		noKeyService = new NoKeyService();
		userService = new UserService();
		idsToDelete = new ArrayList<ObjectId>();
		context = mock(ContainerRequestContext.class);
		Key key = TestUtils.generateRandomKey();
		Mockito.when(context.getProperty("key")).thenReturn(key);
	}

	@AfterClass
	public static void afterClass() throws EpickurException {
		for (ObjectId id : idsToDelete) {
			userService.delete(id.toHexString(), context);
		}
	}

	@Test
	public void testCheckUserService() throws EpickurException {
		User user = new User();
		String userName = TestUtils.generateRandomString();
		String userPassword = "epickur";
		String userEmail = "mail@mail.com";
		user.setName(userName);
		user.setEmail(userEmail);
		user.setPassword(userPassword);

		Response result = userService.create(false, false, user, context);
		if (result.getEntity() != null) {
			User userResult = (User) result.getEntity();
			assertNotNull(userResult.getId());
			idsToDelete.add(userResult.getId());

			String check = result.getHeaderString("check");
			Response result2 = noKeyService.checkUser(userResult.getName(), check);
			if (result2.getEntity() != null) {
				User catererResult2 = (User) result2.getEntity();
				assertEquals(1, catererResult2.getAllow().intValue());
			} else {
				fail("User returned is null");
			}
		} else {
			fail("User returned is null");
		}
	}

	@Test(expected = EpickurIllegalArgument.class)
	public void testCheckUserServiceFail() throws EpickurException {
		Response result = noKeyService.checkUser(null, null);
		if (result.getEntity() != null) {
			DBObject obj = (DBObject) result.getEntity();
			assertEquals(500, obj.get("error"));
		} else {
			fail("Failed");
		}
	}

	@Test(expected = EpickurIllegalArgument.class)
	public void testCheckUserServiceFail2() throws EpickurException {
		Response result = noKeyService.checkUser(new String(), null);
		if (result.getEntity() != null) {
			DBObject obj = (DBObject) result.getEntity();
			assertEquals(500, obj.get("error"));
		} else {
			fail("Failed");
		}
	}

}
