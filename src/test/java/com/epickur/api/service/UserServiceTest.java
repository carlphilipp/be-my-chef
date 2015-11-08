package com.epickur.api.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Response;

import org.bson.types.ObjectId;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.epickur.api.TestUtils;
import com.epickur.api.business.OrderBusiness;
import com.epickur.api.business.UserBusiness;
import com.epickur.api.entity.Key;
import com.epickur.api.entity.Order;
import com.epickur.api.entity.User;
import com.epickur.api.entity.message.DeletedMessage;
import com.epickur.api.entity.message.ErrorMessage;
import com.epickur.api.exception.EpickurException;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.stripe.exception.APIConnectionException;
import com.stripe.exception.APIException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;

public class UserServiceTest {

	@Mock
	private UserBusiness userBusiness;
	@Mock
	private OrderBusiness orderBusiness;
	@Mock
	private ContainerRequestContext context;
	@InjectMocks
	private UserService service;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		TestUtils.setupStripe();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		TestUtils.resetStripe();
	}

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		Key key = TestUtils.generateRandomAdminKey();
		Mockito.when(context.getProperty("key")).thenReturn(key);
	}

	@Test
	public void testCreate() throws EpickurException {
		User user = TestUtils.generateRandomUser();
		User userAfterCreate = TestUtils.mockUserAfterCreate(user);

		when(userBusiness.create((User) anyObject(), anyBoolean())).thenReturn(userAfterCreate);

		Response actual = service.create(false, user);
		assertNotNull(actual);
		assertEquals(200, actual.getStatus());
		User actualUser = (User) actual.getEntity();
		assertNotNull(actualUser.getId());
	}

	@Test
	public void testRead() throws EpickurException {
		User user = TestUtils.generateRandomUserWithId();
		User userAfterRead = TestUtils.mockUserAfterCreate(user);

		when(userBusiness.read(anyString(), (Key) anyObject())).thenReturn(userAfterRead);

		Response actual = service.read(user.getId().toHexString());
		assertNotNull(actual);
		assertEquals(200, actual.getStatus());
		User actualUser = (User) actual.getEntity();
		assertNotNull(actualUser.getId());
	}

	@Test
	public void testReadUserNotFound() throws EpickurException {
		User user = TestUtils.generateRandomUserWithId();

		when(userBusiness.read(anyString(), (Key) anyObject())).thenReturn(null);

		Response actual = service.read(user.getId().toHexString());
		assertNotNull(actual);
		assertEquals(404, actual.getStatus());
		ErrorMessage error = (ErrorMessage) actual.getEntity();
		assertEquals(Response.Status.NOT_FOUND.getStatusCode(), error.getError().intValue());
		assertEquals(Response.Status.NOT_FOUND.getReasonPhrase(), error.getMessage());
	}

	@Test
	public void testUpdate() throws EpickurException {
		User user = TestUtils.generateRandomUserWithId();
		User userAfterUpdate = TestUtils.mockUserAfterCreate(user);

		when(userBusiness.update((User) anyObject(), (Key) anyObject())).thenReturn(userAfterUpdate);

		Response actual = service.update(user.getId().toHexString(), user);
		assertNotNull(actual);
		assertEquals(200, actual.getStatus());
		User actualUser = (User) actual.getEntity();
		assertNotNull(actualUser.getId());
	}

	@Test
	public void testUpdateUserNotFound() throws EpickurException {
		User user = TestUtils.generateRandomUserWithId();

		when(userBusiness.update((User) anyObject(), (Key) anyObject())).thenReturn(null);

		Response actual = service.update(user.getId().toHexString(), user);
		assertNotNull(actual);
		assertEquals(404, actual.getStatus());
		ErrorMessage error = (ErrorMessage) actual.getEntity();
		assertEquals(Response.Status.NOT_FOUND.getStatusCode(), error.getError().intValue());
		assertEquals(Response.Status.NOT_FOUND.getReasonPhrase(), error.getMessage());
	}

	@Test
	public void testUpdatePassword() throws EpickurException {
		User user = TestUtils.generateRandomUserWithId();
		user.setNewPassword("newpassword");
		user.setPassword("oldpassword");
		User userAfterCreate = TestUtils.mockUserAfterCreate(user);
		userAfterCreate.setNewPassword(null);

		when(userBusiness.update((User) anyObject(), (Key) anyObject())).thenReturn(userAfterCreate);

		Response actual = service.update(user.getId().toHexString(), user);
		assertNotNull(actual);
		assertEquals(200, actual.getStatus());
		User actualUser = (User) actual.getEntity();
		assertNotNull(actualUser.getId());
		assertNull(actualUser.getNewPassword());
	}

	@Test
	public void testUpdatePasswordFail() throws EpickurException {
		User user = TestUtils.generateRandomUserWithId();
		user.setPassword("oldpassword");
		User userAfterCreate = TestUtils.mockUserAfterCreate(user);
		userAfterCreate.setNewPassword(null);

		when(userBusiness.update((User) anyObject(), (Key) anyObject())).thenReturn(userAfterCreate);

		Response actual = service.update(user.getId().toHexString(), user);
		assertNotNull(actual);
		assertEquals(200, actual.getStatus());
		User actualUser = (User) actual.getEntity();
		assertNotNull(actualUser.getId());
		assertNull(actualUser.getNewPassword());
	}

	@Test
	public void testDelete() throws EpickurException {
		User user = TestUtils.generateRandomUserWithId();

		when(userBusiness.delete(anyString())).thenReturn(true);

		Response actual = service.delete(user.getId().toHexString());
		assertNotNull(actual);
		assertEquals(200, actual.getStatus());
		DeletedMessage actualDeletedMessage = (DeletedMessage) actual.getEntity();
		assertNotNull(actualDeletedMessage.getId());
		assertNotNull(actualDeletedMessage.getDeleted());
		assertTrue(actualDeletedMessage.getDeleted());
	}

	@Test
	public void testDeleteUserNotFound() throws EpickurException {
		User user = TestUtils.generateRandomUserWithId();

		when(userBusiness.delete(anyString())).thenReturn(false);

		Response actual = service.delete(user.getId().toHexString());
		assertNotNull(actual);
		assertEquals(404, actual.getStatus());
		ErrorMessage error = (ErrorMessage) actual.getEntity();
		assertEquals(Response.Status.NOT_FOUND.getStatusCode(), error.getError().intValue());
		assertEquals(Response.Status.NOT_FOUND.getReasonPhrase(), error.getMessage());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testReadAll() throws EpickurException {
		User user = TestUtils.generateRandomUserWithId();
		List<User> usersAfterReadAll = new ArrayList<User>();
		usersAfterReadAll.add(user);

		when(userBusiness.readAll()).thenReturn(usersAfterReadAll);

		Response actual = service.readAll();
		assertNotNull(actual);
		assertEquals(200, actual.getStatus());
		List<User> actualUsers = (List<User>) actual.getEntity();
		assertEquals(1, actualUsers.size());
	}

	@Test
	public void testAddOneOrder() throws EpickurException {
		try {
			Order order = TestUtils.generateRandomOrder();
			Order orderAfterCreate = TestUtils.mockOrderAfterCreate(order);

			when(orderBusiness.create(anyString(), (Order) anyObject())).thenReturn(orderAfterCreate);

			Response actual = service.createOneOrder(orderAfterCreate.getId().toHexString(), order);
			assertNotNull(actual);
			assertEquals(200, actual.getStatus());
			Order actualUser = (Order) actual.getEntity();
			assertNotNull(actualUser.getId());
		} catch (AuthenticationException | InvalidRequestException | APIConnectionException | CardException | APIException e) {
			fail(TestUtils.STRIPE_MESSAGE);
		}
	}

	@Test
	public void testReadOneOrder() throws EpickurException {
		try {
			Order order = TestUtils.generateRandomOrder();
			Order orderAfterRead = TestUtils.mockOrderAfterCreate(order);

			when(orderBusiness.read(anyString(), (Key) anyObject())).thenReturn(orderAfterRead);

			Response actual = service.readOneOrder(new ObjectId().toHexString(), new ObjectId().toHexString());
			assertNotNull(actual);
			assertEquals(200, actual.getStatus());
			Order actualUser = (Order) actual.getEntity();
			assertNotNull(actualUser.getId());
		} catch (AuthenticationException | InvalidRequestException | APIConnectionException | CardException | APIException e) {
			fail(TestUtils.STRIPE_MESSAGE);
		}
	}

	@Test
	public void testReadOneOrderNotFound()
			throws EpickurException, AuthenticationException, InvalidRequestException, APIConnectionException, CardException,
			APIException {
		when(orderBusiness.read(anyString(), (Key) anyObject())).thenReturn(null);

		Response actual = service.readOneOrder(new ObjectId().toHexString(), new ObjectId().toHexString());
		assertNotNull(actual);
		assertEquals(404, actual.getStatus());
		ErrorMessage error = (ErrorMessage) actual.getEntity();
		assertEquals(Response.Status.NOT_FOUND.getStatusCode(), error.getError().intValue());
		assertEquals(Response.Status.NOT_FOUND.getReasonPhrase(), error.getMessage());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testReadAllOrderAdmin() throws EpickurException {
		try {
			Order order = TestUtils.generateRandomOrder();
			List<Order> orders = new ArrayList<Order>();
			orders.add(order);

			when(orderBusiness.readAllWithUserId(anyString())).thenReturn(orders);

			Response actual = service.readAllOrders(new ObjectId().toHexString());
			assertNotNull(actual);
			assertEquals(200, actual.getStatus());
			List<Order> actualUsers = (List<Order>) actual.getEntity();
			assertNotNull(actualUsers);
			assertEquals(1, actualUsers.size());
		} catch (AuthenticationException | InvalidRequestException | APIConnectionException | CardException | APIException e) {
			fail(TestUtils.STRIPE_MESSAGE);
		}
	}

	@Test
	public void testUpdateOneOrder() throws EpickurException {
		try {
			Order order = TestUtils.generateRandomOrderWithId();
			order.setId(new ObjectId());
			Order orderAfterCreate = TestUtils.mockOrderAfterCreate(order);

			when(orderBusiness.update((Order) anyObject(), (Key) anyObject())).thenReturn(orderAfterCreate);

			Response actual = service.updateOneOrder(new ObjectId().toHexString(), order.getId().toHexString(), order);
			assertNotNull(actual);
			assertEquals(200, actual.getStatus());
			Order actualUser = (Order) actual.getEntity();
			assertNotNull(actualUser.getId());
		} catch (AuthenticationException | InvalidRequestException | APIConnectionException | CardException | APIException e) {
			fail(TestUtils.STRIPE_MESSAGE);
		}
	}

	@Test
	public void testUpdateOneOrderNotFound() throws EpickurException {
		try {
			Order order = TestUtils.generateRandomOrderWithId();
			order.setId(new ObjectId());

			when(orderBusiness.update((Order) anyObject(), (Key) anyObject())).thenReturn(null);

			Response actual = service.updateOneOrder(new ObjectId().toHexString(), order.getId().toHexString(), order);
			assertNotNull(actual);
			assertEquals(404, actual.getStatus());
			ErrorMessage error = (ErrorMessage) actual.getEntity();
			assertEquals(Response.Status.NOT_FOUND.getStatusCode(), error.getError().intValue());
			assertEquals(Response.Status.NOT_FOUND.getReasonPhrase(), error.getMessage());
		} catch (AuthenticationException | InvalidRequestException | APIConnectionException | CardException | APIException e) {
			fail(TestUtils.STRIPE_MESSAGE);
		}
	}

	@Test
	public void testdeleteOneOrder() throws EpickurException {
		try {
			Order order = TestUtils.generateRandomOrderWithId();

			when(orderBusiness.delete(anyString())).thenReturn(true);

			Response actual = service.deleteOneOrder(new ObjectId().toHexString(), order.getId().toHexString());
			assertNotNull(actual);
			assertEquals(200, actual.getStatus());
			DeletedMessage actualDeletedMessage = (DeletedMessage) actual.getEntity();
			assertNotNull(actualDeletedMessage.getId());
			assertNotNull(actualDeletedMessage.getDeleted());
			assertTrue(actualDeletedMessage.getDeleted());
		} catch (AuthenticationException | InvalidRequestException | APIConnectionException | CardException | APIException e) {
			fail(TestUtils.STRIPE_MESSAGE);
		}
	}

	@Test
	public void testdeleteOneOrderFail() throws EpickurException {
		try {
			Order order = TestUtils.generateRandomOrderWithId();

			when(orderBusiness.delete(anyString())).thenReturn(false);

			Response actual = service.deleteOneOrder(new ObjectId().toHexString(), order.getId().toHexString());
			assertNotNull(actual);
			assertEquals(404, actual.getStatus());
			ErrorMessage error = (ErrorMessage) actual.getEntity();
			assertEquals(Response.Status.NOT_FOUND.getStatusCode(), error.getError().intValue());
			assertEquals(Response.Status.NOT_FOUND.getReasonPhrase(), error.getMessage());
		} catch (AuthenticationException | InvalidRequestException | APIConnectionException | CardException | APIException e) {
			fail(TestUtils.STRIPE_MESSAGE);
		}
	}

	@Test
	public void testResetPasswordFirstStep() throws EpickurException {
		ObjectNode node = JsonNodeFactory.instance.objectNode();
		TextNode emailNode = JsonNodeFactory.instance.textNode("name@example.com");
		node.set("email", emailNode);

		Response actual = service.resetPasswordFirstStep(node);
		assertNotNull(actual);
		assertEquals(200, actual.getStatus());
		ObjectNode actualNode = (ObjectNode) actual.getEntity();
		assertNotNull(actualNode.get("status"));
		assertEquals("email sent", actualNode.get("status").textValue());
	}
}
