package com.epickur.api.business;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.bson.types.ObjectId;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.epickur.api.TestUtils;
import com.epickur.api.entity.Key;
import com.epickur.api.entity.Order;
import com.epickur.api.entity.User;
import com.epickur.api.enumeration.Role;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.exception.EpickurNotFoundException;
import com.epickur.api.integration.UserIntegrationTest;
import com.epickur.api.utils.Info;
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
	private static List<String> toDeleteOrder;
	private static List<ObjectId> toDeleteUser;
	private static List<String> temp;
	private static String STRIPE_TEST_KEY;

	@BeforeClass
	public static void beforeClass() {
		orderBusiness = new OrderBusiness();
		userBusiness = new UserBusiness();
		InputStreamReader in = null;
		try {
			in = new InputStreamReader(UserIntegrationTest.class.getClass().getResourceAsStream("/test.properties"));
			Properties prop = new Properties();
			prop.load(in);
			in.close();
			STRIPE_TEST_KEY = prop.getProperty("stripe.key");
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

	@Test
	public void testUpdate() throws EpickurException {
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

		Stripe.apiKey = STRIPE_TEST_KEY;
		Map<String, Object> tokenParams = new HashMap<String, Object>();
		Map<String, Object> cardParams = new HashMap<String, Object>();
		cardParams.put("number", "4242424242424242");
		cardParams.put("exp_month", 2);
		cardParams.put("exp_year", 2016);
		cardParams.put("cvc", "314");
		tokenParams.put("card", cardParams);
		Token token = Token.create(tokenParams);
		Order res = orderBusiness.create(userRes.getId().toHexString(), order, token.getId(), false);
		assertNotNull(res);
		toDeleteOrder.add(res.getId().toHexString());
		assertEquals(token.getId(), res.getCardToken());
	}

	@Test
	public void testCreate2() throws EpickurException, AuthenticationException, InvalidRequestException, APIConnectionException, CardException,
			APIException {
		User user = TestUtils.generateRandomUser();
		User userRes = userBusiness.create(user, true, false);
		toDeleteUser.add(userRes.getId());

		Order order = TestUtils.generateRandomOrder();
		order.setAmount(-15);

		Stripe.apiKey = STRIPE_TEST_KEY;
		Map<String, Object> tokenParams = new HashMap<String, Object>();
		Map<String, Object> cardParams = new HashMap<String, Object>();
		cardParams.put("number", "4242424242424242");
		cardParams.put("exp_month", 2);
		cardParams.put("exp_year", 2016);
		cardParams.put("cvc", "314");
		tokenParams.put("card", cardParams);
		Token token = Token.create(tokenParams);
		Order res = orderBusiness.create(userRes.getId().toHexString(), order, token.getId(), false);
		assertNotNull(res);
		toDeleteOrder.add(res.getId().toHexString());
	}

	@Test(expected = EpickurNotFoundException.class)
	public void testCreate3() throws EpickurException, AuthenticationException, InvalidRequestException, APIConnectionException, CardException,
			APIException {
		Order order = TestUtils.generateRandomOrder();
		orderBusiness.create(new ObjectId().toHexString(), order, "token", false);
	}
	
	@Test
	public void testChargeOneUser() throws EpickurException, AuthenticationException, InvalidRequestException, APIConnectionException, CardException, APIException{
		User user = TestUtils.generateRandomUser();
		User userRes = userBusiness.create(user, true, false);
		toDeleteUser.add(userRes.getId());
		
		Order order = TestUtils.generateRandomOrder();
		order.setAmount(150);

		Stripe.apiKey = STRIPE_TEST_KEY;
		Map<String, Object> tokenParams = new HashMap<String, Object>();
		Map<String, Object> cardParams = new HashMap<String, Object>();
		cardParams.put("number", "4242424242424242");
		cardParams.put("exp_month", 2);
		cardParams.put("exp_year", 2016);
		cardParams.put("cvc", "314");
		tokenParams.put("card", cardParams);
		Token token = Token.create(tokenParams);
		Order res = orderBusiness.create(userRes.getId().toHexString(), order, token.getId(), false);
		assertNotNull(res);
		toDeleteOrder.add(res.getId().toHexString());
		Key key = new Key();
		key.setRole(Role.ADMIN);
		key.setUserId(userRes.getId());
		
		Order orderAfterCharge = orderBusiness.chargeOneUser(userRes.getId().toHexString(), res.getId().toHexString(), true, false, true, key);
		assertTrue(orderAfterCharge.getPaid());
	}
	
	@Test
	public void testChargeOneUserFail() throws EpickurException, AuthenticationException, InvalidRequestException, APIConnectionException, CardException, APIException{
		User user = TestUtils.generateRandomUser();
		User userRes = userBusiness.create(user, true, false);
		toDeleteUser.add(userRes.getId());
		
		Order order = TestUtils.generateRandomOrder();
		order.setAmount(-15);

		Stripe.apiKey = STRIPE_TEST_KEY;
		Map<String, Object> tokenParams = new HashMap<String, Object>();
		Map<String, Object> cardParams = new HashMap<String, Object>();
		cardParams.put("number", "4242424242424242");
		cardParams.put("exp_month", 2);
		cardParams.put("exp_year", 2016);
		cardParams.put("cvc", "314");
		tokenParams.put("card", cardParams);
		Token token = Token.create(tokenParams);
		Order res = orderBusiness.create(userRes.getId().toHexString(), order, token.getId(), false);
		assertNotNull(res);
		toDeleteOrder.add(res.getId().toHexString());
		Key key = new Key();
		key.setRole(Role.ADMIN);
		key.setUserId(userRes.getId());
		
		Order orderAfterCharge = orderBusiness.chargeOneUser(userRes.getId().toHexString(), res.getId().toHexString(), true, false, true, key);
		assertFalse(orderAfterCharge.getPaid());
	}
	
	@Test(expected=EpickurNotFoundException.class)
	public void testChargeOneUserFail2() throws EpickurException, AuthenticationException, InvalidRequestException, APIConnectionException, CardException, APIException{
		User user = TestUtils.generateRandomUser();
		User userRes = userBusiness.create(user, true, false);
		toDeleteUser.add(userRes.getId());
		
		Order order = TestUtils.generateRandomOrder();
		order.setAmount(150);

		Stripe.apiKey = STRIPE_TEST_KEY;
		Map<String, Object> tokenParams = new HashMap<String, Object>();
		Map<String, Object> cardParams = new HashMap<String, Object>();
		cardParams.put("number", "4242424242424242");
		cardParams.put("exp_month", 2);
		cardParams.put("exp_year", 2016);
		cardParams.put("cvc", "314");
		tokenParams.put("card", cardParams);
		Token token = Token.create(tokenParams);
		Order res = orderBusiness.create(userRes.getId().toHexString(), order, token.getId(), false);
		assertNotNull(res);
		toDeleteOrder.add(res.getId().toHexString());
		Key key = new Key();
		key.setRole(Role.ADMIN);
		key.setUserId(userRes.getId());
		
		orderBusiness.chargeOneUser(userRes.getId().toHexString(), new ObjectId().toHexString(), true, false, true, key);
	}
	
	@Test(expected=EpickurNotFoundException.class)
	public void testChargeOneUserFail3() throws EpickurException, AuthenticationException, InvalidRequestException, APIConnectionException, CardException, APIException{
		User user = TestUtils.generateRandomUser();
		User userRes = userBusiness.create(user, true, false);
		toDeleteUser.add(userRes.getId());
		
		Order order = TestUtils.generateRandomOrder();
		order.setAmount(150);

		Stripe.apiKey = STRIPE_TEST_KEY;
		Map<String, Object> tokenParams = new HashMap<String, Object>();
		Map<String, Object> cardParams = new HashMap<String, Object>();
		cardParams.put("number", "4242424242424242");
		cardParams.put("exp_month", 2);
		cardParams.put("exp_year", 2016);
		cardParams.put("cvc", "314");
		tokenParams.put("card", cardParams);
		Token token = Token.create(tokenParams);
		Order res = orderBusiness.create(userRes.getId().toHexString(), order, token.getId(), false);
		assertNotNull(res);
		toDeleteOrder.add(res.getId().toHexString());
		Key key = new Key();
		key.setRole(Role.ADMIN);
		key.setUserId(userRes.getId());
		
		orderBusiness.chargeOneUser(new ObjectId().toHexString(), res.getId().toHexString(), true, false, true, key);
	}
	
	@Test
	public void testChargeOneUser2() throws EpickurException, AuthenticationException, InvalidRequestException, APIConnectionException, CardException, APIException{
		User user = TestUtils.generateRandomUser();
		User userRes = userBusiness.create(user, true, false);
		toDeleteUser.add(userRes.getId());
		
		Order order = TestUtils.generateRandomOrder();
		order.setAmount(150);

		Stripe.apiKey = STRIPE_TEST_KEY;
		Map<String, Object> tokenParams = new HashMap<String, Object>();
		Map<String, Object> cardParams = new HashMap<String, Object>();
		cardParams.put("number", "4242424242424242");
		cardParams.put("exp_month", 2);
		cardParams.put("exp_year", 2016);
		cardParams.put("cvc", "314");
		tokenParams.put("card", cardParams);
		Token token = Token.create(tokenParams);
		Order res = orderBusiness.create(userRes.getId().toHexString(), order, token.getId(), false);
		assertNotNull(res);
		toDeleteOrder.add(res.getId().toHexString());
		Key key = new Key();
		key.setRole(Role.ADMIN);
		key.setUserId(userRes.getId());
		
		Order orderAfterCharge = orderBusiness.chargeOneUser(userRes.getId().toHexString(), res.getId().toHexString(), false, true, true, key);
		assertNull(orderAfterCharge.getPaid());
	}
	
	@Test
	public void testChargeOneUser3() throws EpickurException, AuthenticationException, InvalidRequestException, APIConnectionException, CardException, APIException{
		User user = TestUtils.generateRandomUser();
		User userRes = userBusiness.create(user, true, false);
		toDeleteUser.add(userRes.getId());
		
		Order order = TestUtils.generateRandomOrder();
		order.setAmount(150);

		Stripe.apiKey = STRIPE_TEST_KEY;
		Map<String, Object> tokenParams = new HashMap<String, Object>();
		Map<String, Object> cardParams = new HashMap<String, Object>();
		cardParams.put("number", "4242424242424242");
		cardParams.put("exp_month", 2);
		cardParams.put("exp_year", 2016);
		cardParams.put("cvc", "314");
		tokenParams.put("card", cardParams);
		Token token = Token.create(tokenParams);
		Order res = orderBusiness.create(userRes.getId().toHexString(), order, token.getId(), false);
		assertNotNull(res);
		toDeleteOrder.add(res.getId().toHexString());
		Key key = new Key();
		key.setRole(Role.ADMIN);
		key.setUserId(userRes.getId());
		
		Order orderAfterCharge = orderBusiness.chargeOneUser(userRes.getId().toHexString(), res.getId().toHexString(), true, true, true, key);
		assertTrue(orderAfterCharge.getPaid());
	}
}
