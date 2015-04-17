package com.epickur.api.filter;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

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
import com.epickur.api.service.LoginService;
import com.epickur.api.service.UserService;

public class KeyRequestFilterTest {

	private static UserService service;
	private static LoginService loginService;
	private static List<ObjectId> idsToDeleteUser;
	private static KeyBusiness keyBusiness;
	private static List<String> keysToDelete;
	private static ContainerRequestContext context;

	@BeforeClass
	public static void beforeClass() {
		service = new UserService();
		loginService = new LoginService();
		idsToDeleteUser = new ArrayList<ObjectId>();
		keyBusiness = new KeyBusiness();
		keysToDelete = new ArrayList<String>();
		context = mock(ContainerRequestContext.class);
		Key key = TestUtils.generateRandomKey();
		Mockito.when(context.getProperty("key")).thenReturn(key);
	}

	@AfterClass
	public static void afterClass() throws EpickurException {
		for (ObjectId id : idsToDeleteUser) {
			service.delete(id.toHexString(), context);
		}

		for (String id : keysToDelete) {
			keyBusiness.delete(id);
		}

	}

	@Test
	public void testCreate() throws IOException, EpickurException {
		User user = TestUtils.generateRandomUser();
		user.setPassword("password");
		Response result = service.create(false, true, user, context);
		if (result.getEntity() != null) {
			User userResult = (User) result.getEntity();
			assertNotNull(userResult.getId());
			idsToDeleteUser.add(userResult.getId());

			Response response2 = loginService.login(user.getEmail(), "password");
			if (result.getEntity() != null) {
				User userResult2 = (User) response2.getEntity();
				assertNotNull(userResult2.getId());
				assertNotNull(userResult2.getKey());
				String key = userResult2.getKey();
				keysToDelete.add(key);
				ContainerRequestContext requestContext = mock(ContainerRequestContext.class);

				UriInfo uriInfo = mock(UriInfo.class);

				Mockito.when(requestContext.getUriInfo()).thenReturn(uriInfo);
				MultivaluedMap<String, String> map = new MultivaluedHashMap<String, String>();
				Mockito.when(requestContext.getUriInfo().getQueryParameters()).thenReturn(map);
				requestContext.getUriInfo().getQueryParameters().add("key", key);
				Mockito.when(requestContext.getUriInfo().getPath()).thenReturn("path");

				KeyRequestFilter filter = new KeyRequestFilter();
				filter.filter(requestContext);

			} else {
				fail("Can not loggin");
			}
		} else {
			fail("User returned is null");
		}
	}

	@Test
	public void testCreateFail() throws IOException {
		ContainerRequestContext requestContext = mock(ContainerRequestContext.class);

		UriInfo uriInfo = mock(UriInfo.class);

		Mockito.when(requestContext.getUriInfo()).thenReturn(uriInfo);
		MultivaluedMap<String, String> map = new MultivaluedHashMap<String, String>();
		Mockito.when(requestContext.getUriInfo().getQueryParameters()).thenReturn(map);
		requestContext.getUriInfo().getQueryParameters().add("key", "unic_key");
		Mockito.when(requestContext.getUriInfo().getPath()).thenReturn("path");

		KeyRequestFilter filter = new KeyRequestFilter();
		filter.filter(requestContext);
	}
	
	@Test
	public void testCreateFail2() throws IOException {
		ContainerRequestContext requestContext = mock(ContainerRequestContext.class);

		UriInfo uriInfo = mock(UriInfo.class);

		Mockito.when(requestContext.getUriInfo()).thenReturn(uriInfo);
		MultivaluedMap<String, String> map = new MultivaluedHashMap<String, String>();
		Mockito.when(requestContext.getUriInfo().getQueryParameters()).thenReturn(map);
		requestContext.getUriInfo().getQueryParameters().add("key", "unic_key");
		Mockito.when(requestContext.getUriInfo().getPath()).thenReturn("check");

		KeyRequestFilter filter = new KeyRequestFilter();
		filter.filter(requestContext);
	}
	
	@Test
	public void testCreateFail3() throws IOException {
		ContainerRequestContext requestContext = mock(ContainerRequestContext.class);

		UriInfo uriInfo = mock(UriInfo.class);

		Mockito.when(requestContext.getUriInfo()).thenReturn(uriInfo);
		MultivaluedMap<String, String> map = new MultivaluedHashMap<String, String>();
		Mockito.when(requestContext.getUriInfo().getQueryParameters()).thenReturn(map);
		requestContext.getUriInfo().getQueryParameters().add("key", "unic_key");
		Mockito.when(requestContext.getUriInfo().getPath()).thenReturn(null);

		KeyRequestFilter filter = new KeyRequestFilter();
		filter.filter(requestContext);
	}
	
	@Test
	public void testCreateFail4() throws IOException {
		ContainerRequestContext requestContext = mock(ContainerRequestContext.class);

		UriInfo uriInfo = mock(UriInfo.class);

		Mockito.when(requestContext.getUriInfo()).thenReturn(uriInfo);
		MultivaluedMap<String, String> map = new MultivaluedHashMap<String, String>();
		Mockito.when(requestContext.getUriInfo().getQueryParameters()).thenReturn(map);
		requestContext.getUriInfo().getQueryParameters().add("key", null);
		Mockito.when(requestContext.getUriInfo().getPath()).thenReturn("path");

		KeyRequestFilter filter = new KeyRequestFilter();
		filter.filter(requestContext);
	}
}
