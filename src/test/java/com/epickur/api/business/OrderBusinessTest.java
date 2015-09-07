package com.epickur.api.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.bson.types.ObjectId;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.epickur.api.TestUtils;
import com.epickur.api.entity.Key;
import com.epickur.api.entity.Order;
import com.epickur.api.entity.User;
import com.epickur.api.entity.Voucher;
import com.epickur.api.enumeration.OrderStatus;
import com.epickur.api.enumeration.Role;
import com.epickur.api.enumeration.voucher.DiscountType;
import com.epickur.api.enumeration.voucher.ExpirationType;
import com.epickur.api.enumeration.voucher.Status;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.exception.EpickurForbiddenException;
import com.epickur.api.exception.EpickurNotFoundException;
import com.epickur.api.integration.UserIntegrationTest;
import com.epickur.api.utils.Info;
import com.epickur.api.utils.Security;
import com.stripe.Stripe;
import com.stripe.exception.APIConnectionException;
import com.stripe.exception.APIException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.model.Token;

public class OrderBusinessTest {

	private static OrderBusiness orderBusiness;
	private static UserBusiness userBusiness;
	private static VoucherBusiness voucherBusiness;
	private static List<String> toDeleteOrder;
	private static List<ObjectId> toDeleteUser;
	private static List<String> temp;

	@BeforeClass
	public static void beforeClass() {
		orderBusiness = new OrderBusiness();
		userBusiness = new UserBusiness();
		voucherBusiness = new VoucherBusiness();
		InputStreamReader in = null;
		try {
			in = new InputStreamReader(UserIntegrationTest.class.getClass().getResourceAsStream("/test.properties"));
			Properties prop = new Properties();
			prop.load(in);
			in.close();
			Stripe.apiKey = prop.getProperty("stripe.key");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
		toDeleteOrder = new ArrayList<String>();
		toDeleteUser = new ArrayList<ObjectId>();
		temp = new ArrayList<String>(Info.admins);
		List<String> list = new ArrayList<String>();
		list.add("");
		Info.admins = list;
	}

	@AfterClass
	public static void afterClass() throws EpickurException {
		for (String key : toDeleteOrder) {
			orderBusiness.delete(key);
		}
		for (ObjectId id : toDeleteUser) {
			userBusiness.delete(id.toHexString());
		}
		Info.admins = temp;
	}

	@Before
	public void before() throws EpickurException {
		voucherBusiness.clean();
	}

	@Test
	public void testUpdate() throws EpickurException, AuthenticationException, InvalidRequestException, APIConnectionException, CardException,
			APIException {
		Order order = TestUtils.generateRandomOrder();
		order.setId(new ObjectId());
		Key key = new Key();
		key.setUserId(order.getCreatedBy());
		Order res = orderBusiness.update(order, key);
		assertNull(res);
	}

	@Test
	public void testCreate() throws EpickurException, AuthenticationException, InvalidRequestException, APIConnectionException, CardException,
			APIException {
		User user = TestUtils.generateRandomUser();
		User userRes = userBusiness.create(user, true, false);
		toDeleteUser.add(userRes.getId());

		Order order = TestUtils.generateRandomOrder();

		Map<String, Object> tokenParams = new HashMap<String, Object>();
		Map<String, Object> cardParams = new HashMap<String, Object>();
		cardParams.put("number", "4242424242424242");
		cardParams.put("exp_month", 2);
		cardParams.put("exp_year", 2016);
		cardParams.put("cvc", "314");
		tokenParams.put("card", cardParams);
		Token token = Token.create(tokenParams);
		order.setCardToken(token.getId());
		Order res = orderBusiness.create(userRes.getId().toHexString(), order, false);
		assertNotNull(res);
		toDeleteOrder.add(res.getId().toHexString());
		assertEquals(token.getId(), res.getCardToken());
		assertEquals(OrderStatus.PENDING, res.getStatus());
	}

	@Test
	public void testCreate2() throws EpickurException, AuthenticationException, InvalidRequestException, APIConnectionException, CardException,
			APIException {
		User user = TestUtils.generateRandomUser();
		User userRes = userBusiness.create(user, true, false);
		toDeleteUser.add(userRes.getId());

		Order order = TestUtils.generateRandomOrder();
		order.setAmount(-15);

		Order res = orderBusiness.create(userRes.getId().toHexString(), order, false);
		assertNotNull(res);
		toDeleteOrder.add(res.getId().toHexString());
		assertEquals(OrderStatus.PENDING, res.getStatus());
	}

	@Test(expected = EpickurNotFoundException.class)
	public void testCreate3() throws EpickurException, AuthenticationException, InvalidRequestException, APIConnectionException, CardException,
			APIException {
		Order order = TestUtils.generateRandomOrder();
		order.setCardToken(null);
		orderBusiness.create(new ObjectId().toHexString(), order, false);
	}

	@Test
	public void testCreate4() throws EpickurException, AuthenticationException, InvalidRequestException, APIConnectionException, CardException,
			APIException {
		Set<Voucher> vouchers = voucherBusiness.generate(1, DiscountType.AMOUNT, 8, ExpirationType.ONETIME, null);
		assertNotNull(vouchers);
		assertEquals(1, vouchers.size());
		Voucher voucher = vouchers.iterator().next();
		assertNotNull(voucher);
		User user = TestUtils.generateRandomUser();
		User userRes = userBusiness.create(user, true, false);
		toDeleteUser.add(userRes.getId());

		Order order = TestUtils.generateRandomOrder();
		order.setVoucher(voucher);

		Map<String, Object> tokenParams = new HashMap<String, Object>();
		Map<String, Object> cardParams = new HashMap<String, Object>();
		cardParams.put("number", "4242424242424242");
		cardParams.put("exp_month", 2);
		cardParams.put("exp_year", 2016);
		cardParams.put("cvc", "314");
		tokenParams.put("card", cardParams);
		Token token = Token.create(tokenParams);
		order.setCardToken(token.getId());
		Order res = orderBusiness.create(userRes.getId().toHexString(), order, false);
		assertNotNull(res);
		toDeleteOrder.add(res.getId().toHexString());
		assertEquals(token.getId(), res.getCardToken());
		assertEquals(OrderStatus.PENDING, res.getStatus());
		assertEquals(ExpirationType.ONETIME, res.getVoucher().getExpirationType());
	}

	@Test
	public void testCreate5() throws EpickurException, AuthenticationException, InvalidRequestException, APIConnectionException, CardException,
			APIException {
		User user = TestUtils.generateRandomUser();
		User userRes = userBusiness.create(user, true, false);
		toDeleteUser.add(userRes.getId());

		Order order = TestUtils.generateRandomOrder();

		Map<String, Object> tokenParams = new HashMap<String, Object>();
		Map<String, Object> cardParams = new HashMap<String, Object>();
		cardParams.put("number", "4242424242424242");
		cardParams.put("exp_month", 2);
		cardParams.put("exp_year", 2016);
		cardParams.put("cvc", "314");
		tokenParams.put("card", cardParams);
		Token token = Token.create(tokenParams);
		order.setCardToken(token.getId());
		Order res = orderBusiness.create(userRes.getId().toHexString(), order, true);
		assertNotNull(res);
		toDeleteOrder.add(res.getId().toHexString());
		assertEquals(token.getId(), res.getCardToken());
		assertEquals(OrderStatus.PENDING, res.getStatus());
	}

	@Test
	public void testExecuteOrder() throws EpickurException, AuthenticationException, InvalidRequestException, APIConnectionException,
			CardException, APIException {
		User user = TestUtils.generateRandomUser();
		User userRes = userBusiness.create(user, true, false);
		toDeleteUser.add(userRes.getId());

		Order order = TestUtils.generateRandomOrder();
		order.setAmount(150);

		Order res = orderBusiness.create(userRes.getId().toHexString(), order, false);
		assertNotNull(res);
		toDeleteOrder.add(res.getId().toHexString());
		String orderCode = Security.createOrderCode(res.getId(), res.getCardToken());

		Order orderAfterCharge = orderBusiness.executeOrder(userRes.getId().toHexString(), res.getId().toHexString(), true, false, true, orderCode);
		assertTrue(orderAfterCharge.getPaid());
		assertEquals(OrderStatus.SUCCESSFUL, orderAfterCharge.getStatus());
	}

	@Test
	public void testExecuteOrderFail() throws EpickurException, AuthenticationException, InvalidRequestException, APIConnectionException,
			CardException, APIException {
		User user = TestUtils.generateRandomUser();
		User userRes = userBusiness.create(user, true, false);
		toDeleteUser.add(userRes.getId());

		Order order = TestUtils.generateRandomOrder();
		order.setAmount(-15);

		order.setCardToken(null);

		Order res = orderBusiness.create(userRes.getId().toHexString(), order, false);
		assertNotNull(res);
		toDeleteOrder.add(res.getId().toHexString());
		Key key = new Key();
		key.setRole(Role.ADMIN);
		key.setUserId(userRes.getId());
		String orderCode = Security.createOrderCode(res.getId(), res.getCardToken());
		Order orderAfterCharge = orderBusiness.executeOrder(userRes.getId().toHexString(), res.getId().toHexString(), true, false, true, orderCode);
		assertFalse(orderAfterCharge.getPaid());
		assertEquals(OrderStatus.FAILED, orderAfterCharge.getStatus());
	}

	@Test(expected = EpickurNotFoundException.class)
	public void testExecuteOrderFail2() throws EpickurException, AuthenticationException, InvalidRequestException, APIConnectionException,
			CardException, APIException {
		User user = TestUtils.generateRandomUser();
		User userRes = userBusiness.create(user, true, false);
		toDeleteUser.add(userRes.getId());

		Order order = TestUtils.generateRandomOrder();
		order.setAmount(150);

		Map<String, Object> tokenParams = new HashMap<String, Object>();
		Map<String, Object> cardParams = new HashMap<String, Object>();
		cardParams.put("number", "4242424242424242");
		cardParams.put("exp_month", 2);
		cardParams.put("exp_year", 2016);
		cardParams.put("cvc", "314");
		tokenParams.put("card", cardParams);
		Token token = Token.create(tokenParams);
		order.setCardToken(token.getId());
		Order res = orderBusiness.create(userRes.getId().toHexString(), order, false);
		assertNotNull(res);
		assertNotNull(res.getReadableId());
		toDeleteOrder.add(res.getId().toHexString());
		String orderCode = Security.createOrderCode(res.getId(), res.getCardToken());
		orderBusiness.executeOrder(userRes.getId().toHexString(), new ObjectId().toHexString(), true, false, true, orderCode);
		assertEquals(OrderStatus.FAILED, res.getStatus());
	}

	@Test(expected = EpickurNotFoundException.class)
	public void testExecuteOrderFail3() throws EpickurException, AuthenticationException, InvalidRequestException, APIConnectionException,
			CardException, APIException {
		User user = TestUtils.generateRandomUser();
		User userRes = userBusiness.create(user, true, false);
		toDeleteUser.add(userRes.getId());

		Order order = TestUtils.generateRandomOrder();
		order.setAmount(150);

		Order res = orderBusiness.create(userRes.getId().toHexString(), order, false);
		assertNotNull(res);
		toDeleteOrder.add(res.getId().toHexString());
		Key key = new Key();
		key.setRole(Role.ADMIN);
		key.setUserId(userRes.getId());
		String orderCode = Security.createOrderCode(res.getId(), res.getCardToken());
		orderBusiness.executeOrder(new ObjectId().toHexString(), res.getId().toHexString(), true, false, true, orderCode);
	}

	@Test
	public void testExecuteOrder2() throws EpickurException, AuthenticationException, InvalidRequestException, APIConnectionException,
			CardException, APIException {
		User user = TestUtils.generateRandomUser();
		User userRes = userBusiness.create(user, true, false);
		toDeleteUser.add(userRes.getId());

		Order order = TestUtils.generateRandomOrder();
		order.setAmount(150);

		Order res = orderBusiness.create(userRes.getId().toHexString(), order, false);
		assertNotNull(res);
		toDeleteOrder.add(res.getId().toHexString());
		Key key = new Key();
		key.setRole(Role.ADMIN);
		key.setUserId(userRes.getId());
		String orderCode = Security.createOrderCode(res.getId(), res.getCardToken());
		Order orderAfterCharge = orderBusiness.executeOrder(userRes.getId().toHexString(), res.getId().toHexString(), false, true, true, orderCode);
		assertNull(orderAfterCharge.getPaid());
		assertEquals(OrderStatus.DECLINED, orderAfterCharge.getStatus());
	}

	@Test
	public void testExecuteOrder3() throws EpickurException, AuthenticationException, InvalidRequestException, APIConnectionException, CardException,
			APIException {
		User user = TestUtils.generateRandomUser();
		User userRes = userBusiness.create(user, true, false);
		toDeleteUser.add(userRes.getId());

		Order order = TestUtils.generateRandomOrder();
		order.setAmount(150);

		Order res = orderBusiness.create(userRes.getId().toHexString(), order, false);
		assertNotNull(res);
		toDeleteOrder.add(res.getId().toHexString());
		Key key = new Key();
		key.setRole(Role.ADMIN);
		key.setUserId(userRes.getId());
		String orderCode = Security.createOrderCode(res.getId(), res.getCardToken());
		Order orderAfterCharge = orderBusiness.executeOrder(userRes.getId().toHexString(), res.getId().toHexString(), true, true, true, orderCode);
		assertTrue(orderAfterCharge.getPaid());
		assertEquals(OrderStatus.SUCCESSFUL, orderAfterCharge.getStatus());
	}
	
	@Test(expected = EpickurForbiddenException.class)
	public void testExecuteOrder4() throws EpickurException, AuthenticationException, InvalidRequestException, APIConnectionException,
			CardException, APIException {
		User user = TestUtils.generateRandomUser();
		User userRes = userBusiness.create(user, true, false);
		toDeleteUser.add(userRes.getId());

		Order order = TestUtils.generateRandomOrder();
		order.setAmount(150);

		Order res = orderBusiness.create(userRes.getId().toHexString(), order, false);
		assertNotNull(res);
		toDeleteOrder.add(res.getId().toHexString());
		String orderCode = Security.createOrderCode(res.getId(), res.getCardToken()) + "fail";

		orderBusiness.executeOrder(userRes.getId().toHexString(), res.getId().toHexString(), true, false, true, orderCode);
	}
	
	@Test
	public void testExecuteOrder5() throws EpickurException, AuthenticationException, InvalidRequestException, APIConnectionException,
			CardException, APIException {
		User user = TestUtils.generateRandomUser();
		User userRes = userBusiness.create(user, true, false);
		toDeleteUser.add(userRes.getId());

		Order order = TestUtils.generateRandomOrder();
		order.setAmount(150);

		Order res = orderBusiness.create(userRes.getId().toHexString(), order, false);
		assertNotNull(res);
		toDeleteOrder.add(res.getId().toHexString());
		String orderCode = Security.createOrderCode(res.getId(), res.getCardToken());

		Order orderAfterCharge = orderBusiness.executeOrder(userRes.getId().toHexString(), res.getId().toHexString(), true, false, false, orderCode);
		assertEquals(OrderStatus.PENDING, orderAfterCharge.getStatus());
	}
	
	@Test
	public void testExecuteOrder6() throws EpickurException, AuthenticationException, InvalidRequestException, APIConnectionException,
			CardException, APIException {
		User user = TestUtils.generateRandomUser();
		User userRes = userBusiness.create(user, true, false);
		toDeleteUser.add(userRes.getId());

		Order order = TestUtils.generateRandomOrder();
		order.setAmount(150);

		Order res = orderBusiness.create(userRes.getId().toHexString(), order, false);
		assertNotNull(res);
		toDeleteOrder.add(res.getId().toHexString());
		String orderCode = Security.createOrderCode(res.getId(), res.getCardToken());

		Order orderAfterCharge = orderBusiness.executeOrder(userRes.getId().toHexString(), res.getId().toHexString(), false, false, false, orderCode);
		assertEquals(OrderStatus.DECLINED, orderAfterCharge.getStatus());
	}
	
	@Test
	public void testExecuteOrder7() throws EpickurException, AuthenticationException, InvalidRequestException, APIConnectionException,
			CardException, APIException {
		Set<Voucher> vouchers = voucherBusiness.generate(1, DiscountType.AMOUNT, 8, ExpirationType.ONETIME, null);
		assertNotNull(vouchers);
		assertEquals(1, vouchers.size());
		Voucher voucher = vouchers.iterator().next();
		assertNotNull(voucher);
		User user = TestUtils.generateRandomUser();
		User userRes = userBusiness.create(user, true, false);
		toDeleteUser.add(userRes.getId());

		Order order = TestUtils.generateRandomOrder();
		order.getDish().setId(new ObjectId());
		order.setAmount(150);
		order.setVoucher(voucher);

		Order res = orderBusiness.create(userRes.getId().toHexString(), order, false);
		assertNotNull(res);
		toDeleteOrder.add(res.getId().toHexString());
		String orderCode = Security.createOrderCode(res.getId(), res.getCardToken());

		Order orderAfterCharge = orderBusiness.executeOrder(userRes.getId().toHexString(), res.getId().toHexString(), false, false, false, orderCode);
		assertEquals(OrderStatus.DECLINED, orderAfterCharge.getStatus());
		assertEquals(Status.VALID, orderAfterCharge.getVoucher().getStatus());
	}
	
	@Test
	public void testOrderFailed() throws EpickurException, AuthenticationException, InvalidRequestException, APIConnectionException,
			CardException, APIException {
		User user = TestUtils.generateRandomUser();
		User userRes = userBusiness.create(user, true, false);
		toDeleteUser.add(userRes.getId());

		Order order = TestUtils.generateRandomOrder();
		order.setAmount(150);

		Order res = orderBusiness.create(userRes.getId().toHexString(), order, false);
		assertNotNull(res);
		toDeleteOrder.add(res.getId().toHexString());

		orderBusiness.orderFailed(res, user, true);
		assertFalse(res.getPaid());
		assertEquals(OrderStatus.FAILED, res.getStatus());
	}
	
	@Test
	public void testOrderFailed2() throws EpickurException, AuthenticationException, InvalidRequestException, APIConnectionException,
			CardException, APIException {
		User user = TestUtils.generateRandomUser();
		User userRes = userBusiness.create(user, true, true);
		toDeleteUser.add(userRes.getId());

		Order order = TestUtils.generateRandomOrder();
		order.setAmount(150);

		Order res = orderBusiness.create(userRes.getId().toHexString(), order, false);
		assertNotNull(res);
		toDeleteOrder.add(res.getId().toHexString());

		orderBusiness.orderFailed(res, user, true);
		assertFalse(res.getPaid());
		assertEquals(OrderStatus.FAILED, res.getStatus());
	}
	
	@Test
	public void testOrderFailed3() throws EpickurException, AuthenticationException, InvalidRequestException, APIConnectionException,
			CardException, APIException {
		Set<Voucher> vouchers = voucherBusiness.generate(1, DiscountType.AMOUNT, 8, ExpirationType.ONETIME, null);
		assertNotNull(vouchers);
		assertEquals(1, vouchers.size());
		Voucher voucher = vouchers.iterator().next();
		assertNotNull(voucher);
		User user = TestUtils.generateRandomUser();
		User userRes = userBusiness.create(user, true, true);
		toDeleteUser.add(userRes.getId());

		Order order = TestUtils.generateRandomOrder();
		order.setAmount(150);
		order.setVoucher(voucher);

		Order res = orderBusiness.create(userRes.getId().toHexString(), order, false);
		assertNotNull(res);
		toDeleteOrder.add(res.getId().toHexString());

		orderBusiness.orderFailed(res, user, true);
		assertFalse(res.getPaid());
		assertEquals(OrderStatus.FAILED, res.getStatus());
		assertEquals(Status.VALID, res.getVoucher().getStatus());
	}
}
