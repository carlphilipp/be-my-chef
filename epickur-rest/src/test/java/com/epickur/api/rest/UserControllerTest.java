package com.epickur.api.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
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

import com.epickur.api.entity.Key;
import com.epickur.api.entity.Order;
import com.epickur.api.entity.User;
import com.epickur.api.entity.message.DeletedMessage;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.helper.EntityGenerator;
import com.epickur.api.payment.stripe.StripeTestUtils;
import com.epickur.api.service.OrderService;
import com.epickur.api.service.UserService;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

public class UserControllerTest {

	@Mock
	private UserService userBusiness;
	@Mock
	private OrderService orderBusiness;
	@Mock
	private ContainerRequestContext context;
	@InjectMocks
	private UserController controller;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		StripeTestUtils.setupStripe();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		StripeTestUtils.resetStripe();
	}

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		Key key = EntityGenerator.generateRandomAdminKey();
		Mockito.when(context.getProperty("key")).thenReturn(key);
	}

	@Test
	public void testCreate() throws EpickurException {
		User user = EntityGenerator.generateRandomUser();
		User userAfterCreate = EntityGenerator.mockUserAfterCreate(user);

		when(userBusiness.create((User) anyObject(), anyBoolean())).thenReturn(userAfterCreate);

		Response actual = controller.create(false, user);
		assertNotNull(actual);
		assertEquals(200, actual.getStatus());
		User actualUser = (User) actual.getEntity();
		assertNotNull(actualUser.getId());
	}

	@Test
	public void testRead() throws EpickurException {
		User user = EntityGenerator.generateRandomUserWithId();
		User userAfterRead = EntityGenerator.mockUserAfterCreate(user);

		when(userBusiness.read(anyString(), (Key) anyObject())).thenReturn(userAfterRead);

		Response actual = controller.read(user.getId().toHexString());
		assertNotNull(actual);
		assertEquals(200, actual.getStatus());
		User actualUser = (User) actual.getEntity();
		assertNotNull(actualUser.getId());
	}

	@Test
	public void testUpdate() throws EpickurException {
		User user = EntityGenerator.generateRandomUserWithId();
		User userAfterUpdate = EntityGenerator.mockUserAfterCreate(user);

		when(userBusiness.update((User) anyObject(), (Key) anyObject())).thenReturn(userAfterUpdate);

		Response actual = controller.update(user.getId().toHexString(), user);
		assertNotNull(actual);
		assertEquals(200, actual.getStatus());
		User actualUser = (User) actual.getEntity();
		assertNotNull(actualUser.getId());
	}

	@Test
	public void testUpdatePassword() throws EpickurException {
		User user = EntityGenerator.generateRandomUserWithId();
		user.setNewPassword("newpassword");
		user.setPassword("oldpassword");
		User userAfterCreate = EntityGenerator.mockUserAfterCreate(user);
		userAfterCreate.setNewPassword(null);

		when(userBusiness.update((User) anyObject(), (Key) anyObject())).thenReturn(userAfterCreate);

		Response actual = controller.update(user.getId().toHexString(), user);
		assertNotNull(actual);
		assertEquals(200, actual.getStatus());
		User actualUser = (User) actual.getEntity();
		assertNotNull(actualUser.getId());
		assertNull(actualUser.getNewPassword());
	}

	@Test
	public void testUpdatePasswordFail() throws EpickurException {
		User user = EntityGenerator.generateRandomUserWithId();
		user.setPassword("oldpassword");
		User userAfterCreate = EntityGenerator.mockUserAfterCreate(user);
		userAfterCreate.setNewPassword(null);

		when(userBusiness.update((User) anyObject(), (Key) anyObject())).thenReturn(userAfterCreate);

		Response actual = controller.update(user.getId().toHexString(), user);
		assertNotNull(actual);
		assertEquals(200, actual.getStatus());
		User actualUser = (User) actual.getEntity();
		assertNotNull(actualUser.getId());
		assertNull(actualUser.getNewPassword());
	}

	@Test
	public void testDelete() throws EpickurException {
		User user = EntityGenerator.generateRandomUserWithId();

		when(userBusiness.delete(anyString())).thenReturn(true);

		Response actual = controller.delete(user.getId().toHexString());
		assertNotNull(actual);
		assertEquals(200, actual.getStatus());
		DeletedMessage actualDeletedMessage = (DeletedMessage) actual.getEntity();
		assertNotNull(actualDeletedMessage.getId());
		assertNotNull(actualDeletedMessage.getDeleted());
		assertTrue(actualDeletedMessage.getDeleted());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testReadAll() throws EpickurException {
		User user = EntityGenerator.generateRandomUserWithId();
		List<User> usersAfterReadAll = new ArrayList<User>();
		usersAfterReadAll.add(user);

		when(userBusiness.readAll()).thenReturn(usersAfterReadAll);

		Response actual = controller.readAll();
		assertNotNull(actual);
		assertEquals(200, actual.getStatus());
		List<User> actualUsers = (List<User>) actual.getEntity();
		assertEquals(1, actualUsers.size());
	}

	@Test
	public void testAddOneOrder() throws EpickurException {
		Order order = EntityGenerator.generateRandomOrder();
		Order orderAfterCreate = EntityGenerator.mockOrderAfterCreate(order);

		when(orderBusiness.create(anyString(), (Order) anyObject())).thenReturn(orderAfterCreate);

		Response actual = controller.createOneOrder(orderAfterCreate.getId().toHexString(), order);
		assertNotNull(actual);
		assertEquals(200, actual.getStatus());
		Order actualUser = (Order) actual.getEntity();
		assertNotNull(actualUser.getId());
	}

	@Test
	public void testReadOneOrder() throws EpickurException {
		Order order = EntityGenerator.generateRandomOrder();
		Order orderAfterRead = EntityGenerator.mockOrderAfterCreate(order);

		when(orderBusiness.read(anyString(), (Key) anyObject())).thenReturn(orderAfterRead);

		Response actual = controller.readOneOrder(new ObjectId().toHexString(), new ObjectId().toHexString());
		assertNotNull(actual);
		assertEquals(200, actual.getStatus());
		Order actualUser = (Order) actual.getEntity();
		assertNotNull(actualUser.getId());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testReadAllOrderAdmin() throws EpickurException {
		Order order = EntityGenerator.generateRandomOrder();
		List<Order> orders = new ArrayList<Order>();
		orders.add(order);

		when(orderBusiness.readAllWithUserId(anyString())).thenReturn(orders);

		Response actual = controller.readAllOrders(new ObjectId().toHexString());
		assertNotNull(actual);
		assertEquals(200, actual.getStatus());
		List<Order> actualUsers = (List<Order>) actual.getEntity();
		assertNotNull(actualUsers);
		assertEquals(1, actualUsers.size());
	}

	@Test
	public void testUpdateOneOrder() throws EpickurException {
		Order order = EntityGenerator.generateRandomOrderWithId();
		order.setId(new ObjectId());
		Order orderAfterCreate = EntityGenerator.mockOrderAfterCreate(order);

		when(orderBusiness.update((Order) anyObject(), (Key) anyObject())).thenReturn(orderAfterCreate);

		Response actual = controller.updateOneOrder(new ObjectId().toHexString(), order.getId().toHexString(), order);
		assertNotNull(actual);
		assertEquals(200, actual.getStatus());
		Order actualUser = (Order) actual.getEntity();
		assertNotNull(actualUser.getId());
	}

	@Test
	public void testdeleteOneOrder() throws EpickurException {
		Order order = EntityGenerator.generateRandomOrderWithId();

		when(orderBusiness.delete(anyString())).thenReturn(true);

		Response actual = controller.deleteOneOrder(new ObjectId().toHexString(), order.getId().toHexString());
		assertNotNull(actual);
		assertEquals(200, actual.getStatus());
		DeletedMessage actualDeletedMessage = (DeletedMessage) actual.getEntity();
		assertNotNull(actualDeletedMessage.getId());
		assertNotNull(actualDeletedMessage.getDeleted());
		assertTrue(actualDeletedMessage.getDeleted());
	}

	@Test
	public void testResetPasswordFirstStep() throws EpickurException {
		ObjectNode node = JsonNodeFactory.instance.objectNode();
		TextNode emailNode = JsonNodeFactory.instance.textNode("name@example.com");
		node.set("email", emailNode);

		Response actual = controller.resetPasswordFirstStep(node);
		assertNotNull(actual);
		assertEquals(200, actual.getStatus());
		ObjectNode actualNode = (ObjectNode) actual.getEntity();
		assertNotNull(actualNode.get("status"));
		assertEquals("email sent", actualNode.get("status").textValue());
	}
}
