package com.epickur.api.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Response;

import org.bson.types.ObjectId;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.epickur.api.entity.Key;
import com.epickur.api.entity.Order;
import com.epickur.api.entity.User;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.helper.EntityGenerator;
import com.epickur.api.payment.stripe.StripeTestUtils;
import com.epickur.api.service.OrderService;
import com.epickur.api.service.UserService;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class NoKeyControllerTest {

	@Mock
	private UserService userBusiness;
	@Mock
	private OrderService orderBusiness;
	@Mock
	private ContainerRequestContext context;
	@InjectMocks
	private NoKeyController controller;

	@BeforeClass
	public static void setUpBeforeClass() {
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
		when(context.getProperty("key")).thenReturn(key);
	}

	@Test
	public void testCheckUserService() throws EpickurException {
		User user = EntityGenerator.generateRandomUserWithId();
		user.setCode(EntityGenerator.generateRandomString());

		when(userBusiness.checkCode(anyString(), anyString())).thenReturn(user);

		Response actual = controller.checkUser(user.getEmail(), user.getCode());
		assertNotNull(actual);
		assertEquals(200, actual.getStatus());
		User actualUser = (User) actual.getEntity();
		assertNotNull(actualUser.getId());
	}

	@Test
	public void testExecuteOrder() throws EpickurException {
		User user = EntityGenerator.generateRandomUserWithId();
		Order order = EntityGenerator.generateRandomOrderWithId();

		when(orderBusiness.executeOrder(anyString(), anyString(), anyBoolean(), anyBoolean(), anyString())).thenReturn(order);

		Response actual = controller.executeOrder(user.getId().toHexString(), new ObjectId().toHexString(), true, new ObjectId().toHexString(), true);
		assertNotNull(actual);
		assertEquals(200, actual.getStatus());
		Order actualOrder = (Order) actual.getEntity();
		assertNotNull(actualOrder.getId());
	}

	@Test
	public void testResetPassordSecondStep() throws EpickurException {
		User user = EntityGenerator.generateRandomUserWithId();
		user.setCode(EntityGenerator.generateRandomString());

		when(userBusiness.resetPasswordSecondStep(anyString(), anyString(), anyString())).thenReturn(user);
		ObjectNode objectNode = JsonNodeFactory.instance.objectNode();
		objectNode.set("password", JsonNodeFactory.instance.textNode("newpassord"));
		Response actual = controller.resetPasswordSecondStep(user.getId().toHexString(), user.getCode(), objectNode);
		assertNotNull(actual);
		assertEquals(200, actual.getStatus());
		User actualUser = (User) actual.getEntity();
		assertNotNull(actualUser.getId());
	}
}