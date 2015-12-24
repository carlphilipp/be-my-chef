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

import javax.servlet.http.HttpServletRequest;

import com.epickur.api.validator.UserValidator;
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
import com.epickur.api.stripe.StripeTestUtils;
import com.epickur.api.service.OrderService;
import com.epickur.api.service.UserService;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.springframework.http.ResponseEntity;

public class UserControllerTest {

	@Mock
	private UserService userService;
	@Mock
	private OrderService orderService;
	@Mock
	private HttpServletRequest context;
	@Mock
	private UserValidator validator;
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
		Mockito.when(context.getAttribute("key")).thenReturn(key);
	}

	@Test
	public void testCreate() throws EpickurException {
		User user = EntityGenerator.generateRandomUser();
		User userAfterCreate = EntityGenerator.mockUserAfterCreate(user);

		when(userService.create(anyObject(), anyBoolean())).thenReturn(userAfterCreate);

		ResponseEntity<?> actual = controller.create(false, user);
		assertNotNull(actual);
		assertEquals(200, actual.getStatusCode().value());
		User actualUser = (User) actual.getBody();
		assertNotNull(actualUser.getId());
	}

	@Test
	public void testRead() throws EpickurException {
		User user = EntityGenerator.generateRandomUserWithId();
		User userAfterRead = EntityGenerator.mockUserAfterCreate(user);

		when(userService.read(anyString())).thenReturn(userAfterRead);

		ResponseEntity<?> actual = controller.read(user.getId().toHexString());
		assertNotNull(actual);
		assertEquals(200, actual.getStatusCode().value());
		User actualUser = (User) actual.getBody();
		assertNotNull(actualUser.getId());
	}

	@Test
	public void testUpdate() throws EpickurException {
		User user = EntityGenerator.generateRandomUserWithId();
		User userAfterUpdate = EntityGenerator.mockUserAfterCreate(user);

		when(userService.update(anyObject())).thenReturn(userAfterUpdate);

		ResponseEntity<?> actual = controller.update(user.getId().toHexString(), user);
		assertNotNull(actual);
		assertEquals(200, actual.getStatusCode().value());
		User actualUser = (User) actual.getBody();
		assertNotNull(actualUser.getId());
	}

	@Test
	public void testUpdatePassword() throws EpickurException {
		User user = EntityGenerator.generateRandomUserWithId();
		user.setNewPassword("newpassword");
		user.setPassword("oldpassword");
		User userAfterCreate = EntityGenerator.mockUserAfterCreate(user);
		userAfterCreate.setNewPassword(null);

		when(userService.update(anyObject())).thenReturn(userAfterCreate);

		ResponseEntity<?> actual = controller.update(user.getId().toHexString(), user);
		assertNotNull(actual);
		assertEquals(200, actual.getStatusCode().value());
		User actualUser = (User) actual.getBody();
		assertNotNull(actualUser.getId());
		assertNull(actualUser.getNewPassword());
	}

	@Test
	public void testUpdatePasswordFail() throws EpickurException {
		User user = EntityGenerator.generateRandomUserWithId();
		user.setPassword("oldpassword");
		User userAfterCreate = EntityGenerator.mockUserAfterCreate(user);
		userAfterCreate.setNewPassword(null);

		when(userService.update(anyObject())).thenReturn(userAfterCreate);

		ResponseEntity<?> actual = controller.update(user.getId().toHexString(), user);
		assertNotNull(actual);
		assertEquals(200, actual.getStatusCode().value());
		User actualUser = (User) actual.getBody();
		assertNotNull(actualUser.getId());
		assertNull(actualUser.getNewPassword());
	}

	@Test
	public void testDelete() throws EpickurException {
		User user = EntityGenerator.generateRandomUserWithId();

		when(userService.delete(anyString())).thenReturn(true);

		ResponseEntity<?> actual = controller.delete(user.getId().toHexString());
		assertNotNull(actual);
		assertEquals(200, actual.getStatusCode().value());
		DeletedMessage actualDeletedMessage = (DeletedMessage) actual.getBody();
		assertNotNull(actualDeletedMessage.getId());
		assertNotNull(actualDeletedMessage.getDeleted());
		assertTrue(actualDeletedMessage.getDeleted());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testReadAll() throws EpickurException {
		User user = EntityGenerator.generateRandomUserWithId();
		List<User> usersAfterReadAll = new ArrayList<>();
		usersAfterReadAll.add(user);

		when(userService.readAll()).thenReturn(usersAfterReadAll);

		ResponseEntity<?> actual = controller.readAll();
		assertNotNull(actual);
		assertEquals(200, actual.getStatusCode().value());
		List<User> actualUsers = (List<User>) actual.getBody();
		assertEquals(1, actualUsers.size());
	}

	@Test
	public void testAddOneOrder() throws EpickurException {
		Order order = EntityGenerator.generateRandomOrder();
		Order orderAfterCreate = EntityGenerator.mockOrderAfterCreate(order);

		when(orderService.create(anyString(), anyObject())).thenReturn(orderAfterCreate);

		ResponseEntity<?> actual = controller.createOneOrder(orderAfterCreate.getId().toHexString(), order);
		assertNotNull(actual);
		assertEquals(200, actual.getStatusCode().value());
		Order actualUser = (Order) actual.getBody();
		assertNotNull(actualUser.getId());
	}

	@Test
	public void testReadOneOrder() throws EpickurException {
		Order order = EntityGenerator.generateRandomOrder();
		Order orderAfterRead = EntityGenerator.mockOrderAfterCreate(order);

		when(orderService.readOrder(anyString())).thenReturn(orderAfterRead);

		ResponseEntity<?> actual = controller.readOneOrder(new ObjectId().toHexString(), new ObjectId().toHexString());
		assertNotNull(actual);
		assertEquals(200, actual.getStatusCode().value());
		Order actualUser = (Order) actual.getBody();
		assertNotNull(actualUser.getId());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testReadAllOrderAdmin() throws EpickurException {
		Order order = EntityGenerator.generateRandomOrder();
		List<Order> orders = new ArrayList<>();
		orders.add(order);

		when(orderService.readAllWithUserId(anyString())).thenReturn(orders);

		ResponseEntity<?> actual = controller.readAllOrders(new ObjectId().toHexString());
		assertNotNull(actual);
		assertEquals(200, actual.getStatusCode().value());
		List<Order> actualUsers = (List<Order>) actual.getBody();
		assertNotNull(actualUsers);
		assertEquals(1, actualUsers.size());
	}

	@Test
	public void testUpdateOneOrder() throws EpickurException {
		Order order = EntityGenerator.generateRandomOrderWithId();
		order.setId(new ObjectId());
		Order orderAfterCreate = EntityGenerator.mockOrderAfterCreate(order);

		when(orderService.update(anyObject())).thenReturn(orderAfterCreate);

		ResponseEntity<?> actual = controller.updateOneOrder(new ObjectId().toHexString(), order.getId().toHexString(), order);
		assertNotNull(actual);
		assertEquals(200, actual.getStatusCode().value());
		Order actualUser = (Order) actual.getBody();
		assertNotNull(actualUser.getId());
	}

	@Test
	public void testdeleteOneOrder() throws EpickurException {
		Order order = EntityGenerator.generateRandomOrderWithId();

		when(orderService.delete(anyString())).thenReturn(true);

		ResponseEntity<?> actual = controller.deleteOneOrder(new ObjectId().toHexString(), order.getId().toHexString());
		assertNotNull(actual);
		assertEquals(200, actual.getStatusCode().value());
		DeletedMessage actualDeletedMessage = (DeletedMessage) actual.getBody();
		assertNotNull(actualDeletedMessage.getId());
		assertNotNull(actualDeletedMessage.getDeleted());
		assertTrue(actualDeletedMessage.getDeleted());
	}

	@Test
	public void testResetPasswordFirstStep() throws EpickurException {
		ObjectNode node = JsonNodeFactory.instance.objectNode();
		TextNode emailNode = JsonNodeFactory.instance.textNode("name@example.com");
		node.set("email", emailNode);

		ResponseEntity<?> actual = controller.resetPasswordFirstStep(node);
		assertNotNull(actual);
		assertEquals(200, actual.getStatusCode().value());
		ObjectNode actualNode = (ObjectNode) actual.getBody();
		assertNotNull(actualNode.get("status"));
		assertEquals("email sent", actualNode.get("status").textValue());
	}
}
