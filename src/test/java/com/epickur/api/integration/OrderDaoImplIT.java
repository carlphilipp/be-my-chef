package com.epickur.api.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.container.ContainerRequestContext;

import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;

import com.epickur.api.InitMocks;
import com.epickur.api.TestUtils;
import com.epickur.api.business.OrderBusiness;
import com.epickur.api.business.UserBusiness;
import com.epickur.api.dao.mongo.OrderDAO;
import com.epickur.api.entity.Key;
import com.epickur.api.entity.Order;
import com.epickur.api.entity.User;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.service.UserService;
import com.stripe.exception.APIConnectionException;
import com.stripe.exception.APIException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;

public class OrderDaoImplIT extends InitMocks {

	private static List<ObjectId> idsToDeleteUser;
	private static Map<String, List<ObjectId>> idsToDeleteOrder;
	private static UserService userService;
	@Mock
	private ContainerRequestContext context;

	@BeforeClass
	public static void beforeClass() throws IOException {
		TestUtils.setupStripe();
		idsToDeleteUser = new ArrayList<ObjectId>();
		idsToDeleteOrder = new HashMap<String, List<ObjectId>>();
	}

	@AfterClass
	public static void afterClass() throws EpickurException {
		for (ObjectId id : idsToDeleteUser) {
			userService.delete(id.toHexString());
		}
		for (Entry<String, List<ObjectId>> entry : idsToDeleteOrder.entrySet()) {
			List<ObjectId> list = entry.getValue();
			for (ObjectId id : list) {
				userService.deleteOneOrder(entry.getKey(), id.toHexString());
			}
		}
	}

	@Before
	public void setUp() {
		Key key = TestUtils.generateRandomAdminKey();
		when(context.getProperty("key")).thenReturn(key);
		userService = new UserService(new UserBusiness(), new OrderBusiness(), context);
	}

	@Test
	public void readAllWithCatererIdTest() throws EpickurException, AuthenticationException, InvalidRequestException, APIConnectionException,
			CardException, APIException {
		// Setup DB before test
		User user = TestUtils.createUserAndLogin();
		idsToDeleteUser.add(user.getId());

		Order order = TestUtils.createOrder(user.getId());
		addOrderToDelete(user.getId().toHexString(), order.getId());
		ObjectId catererId = order.getDish().getCaterer().getId();
		Order order2 = TestUtils.createOrder(user.getId(), catererId);
		addOrderToDelete(user.getId().toHexString(), order2.getId());

		OrderDAO dao = new OrderDAO();
		List<Order> orders = dao.readAllWithCatererId(catererId.toHexString(), null, null);
		assertNotNull(orders);
		assertEquals(2, orders.size());
	}

	@Test
	public void readAllWithCatererIdTestWithDates() throws EpickurException, AuthenticationException, InvalidRequestException, APIConnectionException,
			CardException, APIException {
		// Setup DB before test
		User user = TestUtils.createUserAndLogin();
		idsToDeleteUser.add(user.getId());
		Order order = TestUtils.createOrder(user.getId());
		addOrderToDelete(user.getId().toHexString(), order.getId());
		ObjectId catererId = order.getDish().getCaterer().getId();
		Order order2 = TestUtils.createOrder(user.getId(), catererId);
		addOrderToDelete(user.getId().toHexString(), order2.getId());

		OrderDAO dao = new OrderDAO();
		List<Order> orders = dao.readAllWithCatererId(catererId.toHexString(), order.getCreatedAt(), order2.getCreatedAt());
		assertNotNull(orders);
		assertEquals(2, orders.size());
	}

	@Test
	public void readAllWithCatererIdTestWithDates2()
			throws EpickurException, AuthenticationException, InvalidRequestException, APIConnectionException,
			CardException, APIException {
		// Setup DB before test
		User user = TestUtils.createUserAndLogin();
		idsToDeleteUser.add(user.getId());
		Order order = TestUtils.createOrder(user.getId());
		addOrderToDelete(user.getId().toHexString(), order.getId());
		ObjectId catererId = order.getDish().getCaterer().getId();
		Order order2 = TestUtils.createOrder(user.getId(), catererId);
		addOrderToDelete(user.getId().toHexString(), order2.getId());

		OrderDAO dao = new OrderDAO();
		DateTime start = new DateTime();
		start = start.minusSeconds(15);
		List<Order> orders = dao.readAllWithCatererId(catererId.toHexString(), order.getCreatedAt().plus(1), order2.getCreatedAt());
		assertNotNull(orders);
		assertEquals(1, orders.size());
	}

	@Test
	public void readAllWithCatererIdTestWithDates3()
			throws EpickurException, AuthenticationException, InvalidRequestException, APIConnectionException,
			CardException, APIException {
		// Setup DB before test
		User user = TestUtils.createUserAndLogin();
		idsToDeleteUser.add(user.getId());
		Order order = TestUtils.createOrder(user.getId());
		addOrderToDelete(user.getId().toHexString(), order.getId());
		ObjectId catererId = order.getDish().getCaterer().getId();
		Order order2 = TestUtils.createOrder(user.getId(), catererId);
		addOrderToDelete(user.getId().toHexString(), order2.getId());

		OrderDAO dao = new OrderDAO();
		DateTime start = new DateTime();
		start = start.minusSeconds(15);
		List<Order> orders = dao.readAllWithCatererId(catererId.toHexString(), order.getCreatedAt(), order2.getCreatedAt().minus(1));
		assertNotNull(orders);
		assertEquals(1, orders.size());
	}

	@Test
	public void readAllWithCatererIdTestWithDates4()
			throws EpickurException, AuthenticationException, InvalidRequestException, APIConnectionException,
			CardException, APIException {
		// Setup DB before test
		User user = TestUtils.createUserAndLogin();
		idsToDeleteUser.add(user.getId());
		Order order = TestUtils.createOrder(user.getId());
		addOrderToDelete(user.getId().toHexString(), order.getId());
		ObjectId catererId = order.getDish().getCaterer().getId();
		Order order2 = TestUtils.createOrder(user.getId(), catererId);
		addOrderToDelete(user.getId().toHexString(), order2.getId());

		// Test
		OrderDAO dao = new OrderDAO();
		DateTime start = new DateTime();
		start = start.minusSeconds(15);
		List<Order> orders = dao.readAllWithCatererId(catererId.toHexString(), order.getCreatedAt().plus(1), order2.getCreatedAt().minus(1));
		assertNotNull(orders);
		assertEquals(0, orders.size());
	}

	private void addOrderToDelete(final String userId, final ObjectId orderId) {
		List<ObjectId> list = null;
		if (!idsToDeleteOrder.containsKey(userId)) {
			list = new ArrayList<ObjectId>();
			idsToDeleteOrder.put(userId, list);
		} else {
			list = idsToDeleteOrder.get(userId);
		}
		list.add(orderId);
	}
}