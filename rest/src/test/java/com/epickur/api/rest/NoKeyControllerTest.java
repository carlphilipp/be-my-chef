package com.epickur.api.rest;

import com.epickur.api.entity.Key;
import com.epickur.api.entity.Order;
import com.epickur.api.entity.User;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.helper.EntityGenerator;
import com.epickur.api.service.OrderService;
import com.epickur.api.service.UserService;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;

@RunWith(MockitoJUnitRunner.class)
public class NoKeyControllerTest {

	@Mock
	private UserService userBusiness;
	@Mock
	private OrderService orderBusiness;
	@Mock
	private HttpServletRequest context;
	@InjectMocks
	private NoKeyController controller;

	@Before
	public void setUp() {
		Key key = EntityGenerator.generateRandomAdminKey();
		given(context.getAttribute("key")).willReturn(key);
	}

	@Test
	public void testCheckUserService() throws EpickurException {
		// Given
		User user = EntityGenerator.generateRandomUserWithId();
		user.setCode(EntityGenerator.generateRandomString());
		given(userBusiness.checkCode(anyString(), anyString())).willReturn(user);

		// When
		ResponseEntity<?> actual = controller.checkUser(user.getEmail(), user.getCode());

		// Then
		assertNotNull(actual);
		assertEquals(200, actual.getStatusCode().value());
		User actualUser = (User) actual.getBody();
		assertNotNull(actualUser.getId());
	}

	@Test
	public void testExecuteOrder() throws EpickurException {
		// Given
		User user = EntityGenerator.generateRandomUserWithId();
		Order order = EntityGenerator.generateRandomOrderWithId();
		given(orderBusiness.executeOrder(anyString(), anyString(), anyBoolean(), anyBoolean(), anyString())).willReturn(order);

		// When
		ResponseEntity<?> actual = controller.executeOrder(user.getId().toHexString(), new ObjectId().toHexString(), true, new ObjectId().toHexString(), true);

		// Then
		assertNotNull(actual);
		assertEquals(200, actual.getStatusCode().value());
		Order actualOrder = (Order) actual.getBody();
		assertNotNull(actualOrder.getId());
	}

	@Test
	public void testResetPassordSecondStep() throws EpickurException {
		// Given
		User user = EntityGenerator.generateRandomUserWithId();
		user.setCode(EntityGenerator.generateRandomString());
		given(userBusiness.resetPasswordSecondStep(anyString(), anyString(), anyString())).willReturn(user);
		ObjectNode objectNode = JsonNodeFactory.instance.objectNode();
		objectNode.set("password", JsonNodeFactory.instance.textNode("newpassord"));

		// When
		ResponseEntity<?> actual = controller.resetPasswordSecondStep(user.getId().toHexString(), user.getCode(), objectNode);

		// Then
		assertNotNull(actual);
		assertEquals(200, actual.getStatusCode().value());
		User actualUser = (User) actual.getBody();
		assertNotNull(actualUser.getId());
	}
}
