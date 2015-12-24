package com.epickur.api.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;

import com.epickur.api.ApplicationConfigTest;
import org.bson.types.ObjectId;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.epickur.api.IntegrationTestUtils;
import com.epickur.api.dao.mongo.OrderDAO;
import com.epickur.api.entity.Key;
import com.epickur.api.entity.Order;
import com.epickur.api.entity.User;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.helper.EntityGenerator;
import com.epickur.api.stripe.StripeTestUtils;
import com.epickur.api.rest.UserController;
import com.epickur.api.service.OrderService;
import com.epickur.api.service.UserService;
import com.stripe.exception.APIConnectionException;
import com.stripe.exception.APIException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.servlet.http.HttpServletRequest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ApplicationConfigTest.class)
public class OrderDAOIT {
	
	@Mock
	private OrderService orderService;
	@Mock
	private UserService userService;
	@Mock
	private HttpServletRequest context;
	@InjectMocks
	private static UserController userController;
	@Autowired
	private IntegrationTestUtils integrationTestUtils;
	@Autowired
	private OrderDAO dao;

	@BeforeClass
	public static void setUpBeforeClass() throws IOException {
		StripeTestUtils.setupStripe();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		IntegrationTestUtils.cleanDB();
	}

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		Key key = EntityGenerator.generateRandomAdminKey();
		when(context.getAttribute("key")).thenReturn(key);
	}

	@Test
	public void readAllWithCatererIdTest() throws EpickurException, AuthenticationException, InvalidRequestException, APIConnectionException,
			CardException, APIException {
		// Setup DB before test
		User user = integrationTestUtils.createUserAndLogin();

		Order order = integrationTestUtils.createOrder(user.getId());
		ObjectId catererId = order.getDish().getCaterer().getId();
		integrationTestUtils.createOrder(user.getId(), catererId);

		List<Order> orders = dao.readAllWithCatererId(catererId.toHexString(), null, null);
		assertNotNull(orders);
		assertEquals(2, orders.size());
	}

	@Test
	public void readAllWithCatererIdTestWithDates() throws EpickurException {
		// Setup DB before test
		User user = integrationTestUtils.createUserAndLogin();
		Order order = integrationTestUtils.createOrder(user.getId());
		ObjectId catererId = order.getDish().getCaterer().getId();
		Order order2 = integrationTestUtils.createOrder(user.getId(), catererId);

		List<Order> orders = dao.readAllWithCatererId(catererId.toHexString(), order.getCreatedAt(), order2.getCreatedAt());
		assertNotNull(orders);
		assertEquals(2, orders.size());
	}

	@Test
	public void readAllWithCatererIdTestWithDates2() throws EpickurException {
		// Setup DB before test
		User user = integrationTestUtils.createUserAndLogin();
		Order order = integrationTestUtils.createOrder(user.getId());
		ObjectId catererId = order.getDish().getCaterer().getId();
		Order order2 = integrationTestUtils.createOrder(user.getId(), catererId);

		List<Order> orders = dao.readAllWithCatererId(catererId.toHexString(), order.getCreatedAt().plus(1), order2.getCreatedAt());
		assertNotNull(orders);
		assertEquals(1, orders.size());
	}

	@Test
	public void readAllWithCatererIdTestWithDates3() throws EpickurException {
		// Setup DB before test
		User user = integrationTestUtils.createUserAndLogin();
		Order order = integrationTestUtils.createOrder(user.getId());
		ObjectId catererId = order.getDish().getCaterer().getId();
		Order order2 = integrationTestUtils.createOrder(user.getId(), catererId);

		List<Order> orders = dao.readAllWithCatererId(catererId.toHexString(), order.getCreatedAt(), order2.getCreatedAt().minus(1));
		assertNotNull(orders);
		assertEquals(1, orders.size());
	}

	@Test
	public void readAllWithCatererIdTestWithDates4() throws EpickurException {
		// Setup DB before test
		User user = integrationTestUtils.createUserAndLogin();
		Order order = integrationTestUtils.createOrder(user.getId());
		ObjectId catererId = order.getDish().getCaterer().getId();
		Order order2 = integrationTestUtils.createOrder(user.getId(), catererId);

		// Test
		List<Order> orders = dao.readAllWithCatererId(catererId.toHexString(), order.getCreatedAt().plus(1), order2.getCreatedAt().minus(1));
		assertNotNull(orders);
		assertEquals(0, orders.size());
	}
}
