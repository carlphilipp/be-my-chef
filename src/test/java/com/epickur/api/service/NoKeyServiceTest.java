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
import com.epickur.api.utils.Security;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class NoKeyServiceTest {

	private static NoKeyService noKeyService;
	private static UserService userService;
	private static List<ObjectId> idsToDelete;
	private static ContainerRequestContext context;
	private static ObjectMapper mapper;

	@BeforeClass
	public static void beforeClass() {
		mapper = new ObjectMapper();
		noKeyService = new NoKeyService();
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
	public void testCheckUserService() throws EpickurException {
		User user = new User();
		String userName = TestUtils.generateRandomString();
		String userPassword = "epickur";
		String userEmail = "mail@gmail.com";
		user.setName(userName);
		user.setFirst("test");
		user.setLast("test");
		user.setEmail(userEmail);
		user.setPassword(userPassword);
		user.setState("Illinois");
		user.setCountry("USA");
		user.setZipcode("60614");

		Response result = userService.create(false, false, user, context);
		if (result.getEntity() != null) {
			User userResult = (User) result.getEntity();
			assertNotNull(userResult.getId());
			idsToDelete.add(userResult.getId());

			String check = result.getHeaderString("check");
			Response result2 = noKeyService.checkUser(userResult.getEmail(), check);
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
		noKeyService.checkUser(null, null);
	}

	@Test(expected = EpickurIllegalArgument.class)
	public void testCheckUserServiceFail2() throws EpickurException {
		noKeyService.checkUser(new String(), null);
	}

	@Test
	public void testResetPassord() throws EpickurException {
		// Create User
		User user = TestUtils.generateRandomUser();
		Response result = userService.create(false, false, user, context);
		if (result.getEntity() != null) {
			User userResult = (User) result.getEntity();
			assertNotNull(userResult.getId());
			idsToDelete.add(userResult.getId());

			String resetCode = Security.createResetCode(userResult.getId(), userResult.getEmail());

			ObjectNode node = mapper.createObjectNode();
			node.put("password", "newPassword");
			Response result2 = noKeyService.resetPasswordSecondStep(userResult.getId().toHexString(), resetCode, node);
			assertNotNull(result2.getEntity());
			User userResult2 = (User) result2.getEntity();
			assertNotNull(userResult2.getEmail());
		} else {
			fail("User returned is null");
		}
	}
}
