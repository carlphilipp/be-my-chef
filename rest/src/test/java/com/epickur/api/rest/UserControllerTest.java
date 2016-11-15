package com.epickur.api.rest;

import com.epickur.api.entity.Order;
import com.epickur.api.entity.User;
import com.epickur.api.entity.message.DeletedMessage;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.helper.EntityGenerator;
import com.epickur.api.service.OrderService;
import com.epickur.api.service.UserService;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest {

	@Mock
	private UserService userService;
	@Mock
	private OrderService orderService;
	@Mock
	private HttpServletRequest context;
	@InjectMocks
	private UserController controller;

	@Test
	public void testCreate() throws EpickurException {
		// Given
		User user = EntityGenerator.generateRandomUser();
		User userAfterCreate = EntityGenerator.mockUserAfterCreate(user);
		given(userService.create(any(User.class), any(Boolean.class))).willReturn(userAfterCreate);

		// When
		ResponseEntity<?> actual = controller.create(false, user);

		// Then
		assertNotNull(actual);
		assertEquals(200, actual.getStatusCode().value());
		User actualUser = (User) actual.getBody();
		assertNotNull(actualUser.getId());
	}

	@Test
	public void testRead() throws EpickurException {
		// Given
		User user = EntityGenerator.generateRandomUserWithId();
		User userAfterRead = EntityGenerator.mockUserAfterCreate(user);
		given(userService.read(any())).willReturn(Optional.of(userAfterRead));

		// When
		ResponseEntity<?> actual = controller.read(user.getId().toHexString());

		// Then
		assertNotNull(actual);
		assertEquals(200, actual.getStatusCode().value());
		User actualUser = (User) actual.getBody();
		assertNotNull(actualUser.getId());
	}

	@Test
	public void testUpdate() throws EpickurException {
		// Given
		User user = EntityGenerator.generateRandomUserWithId();
		User userAfterUpdate = EntityGenerator.mockUserAfterCreate(user);
		given(userService.update(any(User.class))).willReturn(userAfterUpdate);

		// When
		ResponseEntity<?> actual = controller.update(user.getId().toHexString(), user);

		// Then
		assertNotNull(actual);
		assertEquals(200, actual.getStatusCode().value());
		User actualUser = (User) actual.getBody();
		assertNotNull(actualUser.getId());
	}

	@Test
	public void testUpdatePassword() throws EpickurException {
		// Given
		User user = EntityGenerator.generateRandomUserWithId();
		user.setNewPassword("newpassword");
		user.setPassword("oldpassword");
		User userAfterCreate = EntityGenerator.mockUserAfterCreate(user);
		userAfterCreate.setNewPassword(null);
		given(userService.update(any(User.class))).willReturn(userAfterCreate);

		// When
		ResponseEntity<?> actual = controller.update(user.getId().toHexString(), user);

		// Then
		assertNotNull(actual);
		assertEquals(200, actual.getStatusCode().value());
		User actualUser = (User) actual.getBody();
		assertNotNull(actualUser.getId());
		assertNull(actualUser.getNewPassword());
	}

	@Test
	public void testUpdatePasswordFail() throws EpickurException {
		// Given
		User user = EntityGenerator.generateRandomUserWithId();
		user.setPassword("oldpassword");
		User userAfterCreate = EntityGenerator.mockUserAfterCreate(user);
		userAfterCreate.setNewPassword(null);
		given(userService.update(any(User.class))).willReturn(userAfterCreate);

		// When
		ResponseEntity<?> actual = controller.update(user.getId().toHexString(), user);

		// Then
		assertNotNull(actual);
		assertEquals(200, actual.getStatusCode().value());
		User actualUser = (User) actual.getBody();
		assertNotNull(actualUser.getId());
		assertNull(actualUser.getNewPassword());
	}

	@Test
	public void testDelete() throws EpickurException {
		// Given
		User user = EntityGenerator.generateRandomUserWithId();
		given(userService.delete(any())).willReturn(true);

		// When
		ResponseEntity<?> actual = controller.delete(user.getId().toHexString());

		// Then
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
		// Given
		User user = EntityGenerator.generateRandomUserWithId();
		List<User> usersAfterReadAll = new ArrayList<>();
		usersAfterReadAll.add(user);
		given(userService.readAll()).willReturn(usersAfterReadAll);

		// When
		ResponseEntity<?> actual = controller.readAll();

		// Then
		assertNotNull(actual);
		assertEquals(200, actual.getStatusCode().value());
		List<User> actualUsers = (List<User>) actual.getBody();
		assertEquals(1, actualUsers.size());
	}

	@Test
	public void testAddOneOrder() throws EpickurException {
		// Given
		Order order = EntityGenerator.generateRandomOrder();
		Order orderAfterCreate = EntityGenerator.mockOrderAfterCreate(order);
		given(orderService.create(any(), any(Order.class))).willReturn(orderAfterCreate);

		// When
		ResponseEntity<?> actual = controller.createOneOrder(orderAfterCreate.getId().toHexString(), order);

		// Then
		assertNotNull(actual);
		assertEquals(200, actual.getStatusCode().value());
		Order actualUser = (Order) actual.getBody();
		assertNotNull(actualUser.getId());
	}

	@Test
	public void testReadOneOrder() throws EpickurException {
		// Given
		Order order = EntityGenerator.generateRandomOrder();
		Order orderAfterRead = EntityGenerator.mockOrderAfterCreate(order);
		given(orderService.readOrder(any())).willReturn(Optional.of(orderAfterRead));

		// When
		ResponseEntity<?> actual = controller.readOneOrder(new ObjectId().toHexString(), new ObjectId().toHexString());

		// Then
		assertNotNull(actual);
		assertEquals(200, actual.getStatusCode().value());
		Order actualUser = (Order) actual.getBody();
		assertNotNull(actualUser.getId());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testReadAllOrderAdmin() throws EpickurException {
		// Given
		Order order = EntityGenerator.generateRandomOrder();
		List<Order> orders = new ArrayList<>();
		orders.add(order);
		given(orderService.readAllWithUserId(any())).willReturn(orders);

		// When
		ResponseEntity<?> actual = controller.readAllOrders(new ObjectId().toHexString());

		// Then
		assertNotNull(actual);
		assertEquals(200, actual.getStatusCode().value());
		List<Order> actualUsers = (List<Order>) actual.getBody();
		assertNotNull(actualUsers);
		assertEquals(1, actualUsers.size());
	}

	@Test
	public void testUpdateOneOrder() throws EpickurException {
		// Given
		Order order = EntityGenerator.generateRandomOrderWithId();
		order.setId(new ObjectId());
		Order orderAfterCreate = EntityGenerator.mockOrderAfterCreate(order);
		given(orderService.update(any(Order.class))).willReturn(orderAfterCreate);

		// When
		ResponseEntity<?> actual = controller.updateOneOrder(new ObjectId().toHexString(), order.getId().toHexString(), order);

		// Then
		assertNotNull(actual);
		assertEquals(200, actual.getStatusCode().value());
		Order actualUser = (Order) actual.getBody();
		assertNotNull(actualUser.getId());
	}

	@Test
	public void testdeleteOneOrder() throws EpickurException {
		// Given
		Order order = EntityGenerator.generateRandomOrderWithId();
		given(orderService.delete(any())).willReturn(true);

		// When
		ResponseEntity<?> actual = controller.deleteOneOrder(new ObjectId().toHexString(), order.getId().toHexString());

		// Then
		assertNotNull(actual);
		assertEquals(200, actual.getStatusCode().value());
		DeletedMessage actualDeletedMessage = (DeletedMessage) actual.getBody();
		assertNotNull(actualDeletedMessage.getId());
		assertNotNull(actualDeletedMessage.getDeleted());
		assertTrue(actualDeletedMessage.getDeleted());
	}

	@Test
	public void testResetPasswordFirstStep() throws EpickurException {
		// Given
		ObjectNode node = JsonNodeFactory.instance.objectNode();
		TextNode emailNode = JsonNodeFactory.instance.textNode("name@example.com");
		node.set("email", emailNode);

		// When
		ResponseEntity<?> actual = controller.resetPasswordFirstStep(node);

		// Then
		assertNotNull(actual);
		assertEquals(200, actual.getStatusCode().value());
		ObjectNode actualNode = (ObjectNode) actual.getBody();
		assertNotNull(actualNode.get("status"));
		assertEquals("email sent", actualNode.get("status").textValue());
	}
}
