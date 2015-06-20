package com.epickur.api.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Response;

import org.bson.types.ObjectId;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import com.epickur.api.TestUtils;
import com.epickur.api.entity.Caterer;
import com.epickur.api.entity.Key;
import com.epickur.api.entity.Order;
import com.epickur.api.entity.User;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.exception.EpickurIllegalArgument;
import com.epickur.api.integration.UserIntegrationTest;
import com.epickur.api.validator.Validator;
import com.mongodb.DBObject;
import com.stripe.Stripe;
import com.stripe.exception.APIConnectionException;
import com.stripe.exception.APIException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;

public class CatererServiceTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private static CatererService catererService;
	private static UserService userService;
	private static List<ObjectId> idsCaterers;
	private static Map<String, List<ObjectId>> idsOrders;
	private static ContainerRequestContext context;

	@BeforeClass
	public static void beforeClass() {
		context = mock(ContainerRequestContext.class);
		Key key = TestUtils.generateRandomKey();
		Mockito.when(context.getProperty("key")).thenReturn(key);
		catererService = new CatererService();
		userService = new UserService();
		idsCaterers = new ArrayList<ObjectId>();
		idsOrders = new HashMap<String, List<ObjectId>>();
		try {
			InputStreamReader in = new InputStreamReader(UserIntegrationTest.class.getClass().getResourceAsStream("/test.properties"));
			Properties prop = new Properties();
			prop.load(in);
			in.close();
			String STRIPE_TEST_KEY = prop.getProperty("stripe.key");
			Stripe.apiKey = STRIPE_TEST_KEY;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@AfterClass
	public static void afterClass() throws EpickurException {
		for (ObjectId id : idsCaterers) {
			catererService.delete(id.toHexString(), context);
		}
		for (Entry<String, List<ObjectId>> entry : idsOrders.entrySet()) {
			for (ObjectId id : entry.getValue()) {
				userService.deleteOneOrder(entry.getKey(), id.toHexString(), context);
			}
			userService.delete(entry.getKey(), context);
		}
	}

	@Test
	public void testCreate() throws EpickurException {
		Caterer caterer = TestUtils.generateRandomCatererWithoutId();
		Response result = catererService.create(caterer, context);
		if (result.getEntity() != null) {
			Caterer catererResult = (Caterer) result.getEntity();
			assertNotNull(catererResult.getId());
			idsCaterers.add(catererResult.getId());
		} else {
			fail("Caterer returned is null");
		}
	}

	@Test
	public void testCreateFail() throws EpickurException {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("No caterer has been provided");

		Caterer caterer = null;
		Response result = catererService.create(caterer, context);
		if (result.getEntity() != null) {
			DBObject obj = (DBObject) result.getEntity();
			assertEquals(500, obj.get("error"));
		} else {
			fail("Caterer returned is null");
		}
	}

	@Test
	public void testRead() throws EpickurException {
		Caterer caterer = TestUtils.generateRandomCatererWithoutId();
		Response result = catererService.create(caterer, context);
		if (result.getEntity() != null) {
			Caterer catererResult = (Caterer) result.getEntity();
			assertNotNull(catererResult.getId());
			idsCaterers.add(catererResult.getId());
			String id = catererResult.getId().toHexString();
			Response result2 = catererService.read(id);
			if (result2.getEntity() != null) {
				Caterer catererResult2 = (Caterer) result.getEntity();
				assertEquals(catererResult, catererResult2);
			} else {
				fail("Caterer returned is null");
			}
		} else {
			fail("Caterer returned is null");
		}
	}

	@Test
	public void testReadFail() throws EpickurException {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage(Validator.PARAM_ID_NULL);

		Response result = catererService.read(null);
		if (result.getEntity() != null) {
			DBObject obj = (DBObject) result.getEntity();
			assertEquals(500, obj.get("error"));
		} else {
			fail("Caterer returned is null");
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testReadAll() throws EpickurException {
		Caterer caterer = TestUtils.generateRandomCatererWithoutId();
		Response result = catererService.create(caterer, context);
		if (result.getEntity() != null) {
			Caterer catererResult = (Caterer) result.getEntity();
			assertNotNull(catererResult.getId());
			idsCaterers.add(catererResult.getId());
			String id = catererResult.getId().toHexString();
			Response result2 = catererService.readAll(context);
			if (result2.getEntity() != null) {
				List<Caterer> cateres = (List<Caterer>) result2.getEntity();
				for (Caterer cat : cateres) {
					if (cat.getId().equals(id)) {
						assertEquals(catererResult, cat);
					}
				}
			} else {
				fail("List of catereres returned is null");
			}
		} else {
			fail("Caterer returned is null");
		}
	}

	@Test
	public void testUpdate() throws EpickurException {
		Caterer caterer = TestUtils.generateRandomCatererWithoutId();
		Response result = catererService.create(caterer, context);
		if (result.getEntity() != null) {
			Caterer catererResult = (Caterer) result.getEntity();
			assertNotNull(catererResult.getId());
			idsCaterers.add(catererResult.getId());

			Caterer catererUpdate = catererResult.clone();
			catererUpdate.setDescription("modified");
			Response result2 = catererService.update(catererUpdate.getId().toHexString(), catererUpdate, context);
			if (result2.getEntity() != null) {
				int statusCode = result2.getStatus();
				assertEquals("Wrong status code: " + statusCode + " with " + result2.getEntity(), 200, statusCode);
				Caterer catererUpdate2 = (Caterer) result2.getEntity();
				assertNotNull("CreatedAt is null", catererUpdate2.getCreatedAt());
				assertNotNull("UpdatedAt is null", catererUpdate2.getUpdatedAt());
				assertEquals("modified", catererUpdate2.getDescription());
				assertEquals(catererResult.getEmail(), catererUpdate2.getEmail());
				assertEquals(catererResult.getManager(), catererUpdate2.getManager());
				assertEquals(catererResult.getName(), catererUpdate2.getName());
				assertEquals(catererResult.getPhone(), catererUpdate2.getPhone());
				assertEquals(catererResult.getLocation(), catererUpdate2.getLocation());
			} else {
				fail("Caterer returned is null");
			}
		} else {
			fail("Caterer returned is null");
		}
	}

	@Test
	public void testUpdate2() throws EpickurException {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The parameter id and the field caterer.id should match");

		Caterer caterer = TestUtils.generateRandomCatererWithoutId();
		Response result = catererService.create(caterer, context);
		if (result.getEntity() != null) {
			Caterer catererResult = (Caterer) result.getEntity();
			assertNotNull(catererResult.getId());
			idsCaterers.add(catererResult.getId());

			Caterer catererUpdate = catererResult.clone();
			catererUpdate.setDescription("modified");
			catererService.update(new ObjectId().toHexString(), catererUpdate, context);
		} else {
			fail("Caterer returned is null");
		}
	}

	@Test
	public void testUpdate3() throws EpickurException {
		Caterer caterer = TestUtils.generateRandomCatererWithoutId();
		Response result = catererService.create(caterer, context);
		if (result.getEntity() != null) {
			Caterer catererResult = (Caterer) result.getEntity();
			assertNotNull(catererResult.getId());
			idsCaterers.add(catererResult.getId());

			Caterer catererUpdate = catererResult.clone();
			catererUpdate.setDescription("modified");
			Response result2 = catererService.update(catererUpdate.getId().toHexString(), catererUpdate, context);
			if (result2.getEntity() != null) {
				int statusCode = result.getStatus();
				assertEquals("Wrong status code: " + statusCode + " with " + result.getEntity(), 200, statusCode);
				Caterer catererUpdate2 = (Caterer) result2.getEntity();
				assertNotNull("CreatedAt is null", catererUpdate2.getCreatedAt());
				assertNotNull("UpdatedAt is null", catererUpdate2.getUpdatedAt());
				assertEquals("modified", catererUpdate2.getDescription());
				assertEquals(catererResult.getEmail(), catererUpdate2.getEmail());
				assertEquals(catererResult.getManager(), catererUpdate2.getManager());
				assertEquals(catererResult.getName(), catererUpdate2.getName());
				assertEquals(catererResult.getPhone(), catererUpdate2.getPhone());
				assertEquals(catererResult.getLocation(), catererUpdate2.getLocation());
			} else {
				fail("Caterer returned is null");
			}
		} else {
			fail("Caterer returned is null");
		}
	}

	@Test
	public void testFail() throws EpickurException {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage(Validator.PARAM_ID_NULL);

		Response result = catererService.update(null, null, context);
		if (result.getEntity() != null) {
			DBObject dbObject = (DBObject) result.getEntity();
			assertEquals(500, dbObject.get("error"));
		} else {
			fail("Caterer returned is null");
		}
	}

	@Test
	public void testFail2() throws EpickurException {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage(Validator.NO_CATERER_PROVIDED);

		Response result = catererService.update(new ObjectId().toHexString(), null, context);
		if (result.getEntity() != null) {
			DBObject dbObject = (DBObject) result.getEntity();
			assertEquals(500, dbObject.get("error"));
		} else {
			fail("Caterer returned is null");
		}
	}

	@Test
	public void testFail3() throws EpickurException {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage(Validator.NO_CATERER_PROVIDED);

		Response result = catererService.update(new ObjectId().toHexString(), null, context);
		if (result.getEntity() != null) {
			DBObject dbObject = (DBObject) result.getEntity();
			assertEquals(500, dbObject.get("error"));
		} else {
			fail("Caterer returned is null");
		}
	}

	@Test
	public void testFail4() throws EpickurException {
		Caterer caterer = TestUtils.generateRandomCatererWithId();
		caterer.setId(new ObjectId());
		Response result = catererService.update(caterer.getId().toHexString(), caterer, context);
		if (result.getEntity() != null) {
			DBObject dbObject = (DBObject) result.getEntity();
			assertEquals(404, dbObject.get("error"));
		} else {
			fail("Caterer should not be found");
		}
	}

	@Test
	public void testDelete() throws EpickurException {
		Caterer caterer = TestUtils.generateRandomCatererWithoutId();
		Response result = catererService.create(caterer, context);
		if (result.getEntity() != null) {
			Caterer catererResult = (Caterer) result.getEntity();
			assertNotNull(catererResult.getId());
			idsCaterers.add(catererResult.getId());

			Response result2 = catererService.delete(catererResult.getId().toHexString(), context);
			if (result2.getEntity() != null) {
				int statusCode = result2.getStatus();
				String entityResult = ((DBObject) result2.getEntity()).toString();
				assertEquals("Wrong status code: " + statusCode + " with " + entityResult, 200, statusCode);
				DBObject deleted = (DBObject) result2.getEntity();
				assertTrue((Boolean) deleted.get("deleted"));

				Response result3 = catererService.read(catererResult.getId().toHexString());
				DBObject dbObject = (DBObject) result3.getEntity();
				assertEquals(404, dbObject.get("error"));
			}
		} else {
			fail("Caterer returned is null");
		}
	}

	@Test
	public void testDeleteFail() throws EpickurException {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage(Validator.PARAM_ID_NULL);

		Response result = catererService.delete(null, context);
		if (result.getEntity() != null) {
			DBObject dbObject = (DBObject) result.getEntity();
			assertEquals(500, dbObject.get("error"));
		} else {
			fail("Fail");
		}
	}

	@Test
	public void testPaymentInfo() throws EpickurException, AuthenticationException, InvalidRequestException, APIConnectionException, CardException,
			APIException {
		User user = TestUtils.createUserAndLogin();
		Caterer caterer = TestUtils.createCatererWithUserId(user.getId());
		idsCaterers.add(caterer.getId());
		Order order1 = TestUtils.createOrder(user.getId(), caterer.getId());
		addOrder(user, order1);
		Order order2 = TestUtils.createOrder(user.getId(), caterer.getId());
		addOrder(user, order2);
		Order order3 = TestUtils.createOrder(user.getId(), caterer.getId());
		addOrder(user, order3);

		Response result = catererService.paymentInfo(caterer.getId().toHexString(), null, null, null);
		if (result.getEntity() != null) {
			int statusCode = result.getStatus();
			DBObject entityResult = (DBObject) result.getEntity();
			assertEquals("Wrong status code: " + statusCode + " with " + entityResult, 200, statusCode);
			assertTrue((int) entityResult.get("amount") > 0);
		}
	}

	@Test
	public void testPaymentInfo2() throws EpickurException, AuthenticationException, InvalidRequestException, APIConnectionException, CardException,
			APIException {
		User user = TestUtils.createUserAndLogin();
		Caterer caterer = TestUtils.createCatererWithUserId(user.getId());
		idsCaterers.add(caterer.getId());
		Order order1 = TestUtils.createOrder(user.getId(), caterer.getId());
		addOrder(user, order1);
		Order order2 = TestUtils.createOrder(user.getId(), caterer.getId());
		addOrder(user, order2);
		Order order3 = TestUtils.createOrder(user.getId(), caterer.getId());
		addOrder(user, order3);

		String start = "01/01/2015";
		String defaultFormat = "MM/dd/yyyy";

		Response result = catererService.paymentInfo(caterer.getId().toHexString(), start, null, defaultFormat);
		if (result.getEntity() != null) {
			int statusCode = result.getStatus();
			DBObject entityResult = (DBObject) result.getEntity();
			assertEquals("Wrong status code: " + statusCode + " with " + entityResult, 200, statusCode);
			assertTrue((int) entityResult.get("amount") > 0);
		}
	}

	@Test
	public void testPaymentInfo3() throws EpickurException, AuthenticationException, InvalidRequestException, APIConnectionException, CardException,
			APIException {
		User user = TestUtils.createUserAndLogin();
		Caterer caterer = TestUtils.createCatererWithUserId(user.getId());
		idsCaterers.add(caterer.getId());
		Order order1 = TestUtils.createOrder(user.getId(), caterer.getId());
		addOrder(user, order1);
		Order order2 = TestUtils.createOrder(user.getId(), caterer.getId());
		addOrder(user, order2);
		Order order3 = TestUtils.createOrder(user.getId(), caterer.getId());
		addOrder(user, order3);

		String start = "01/01/2015";
		String end = "01/02/2015";
		String defaultFormat = "MM/dd/yyyy";

		Response result = catererService.paymentInfo(caterer.getId().toHexString(), start, end, defaultFormat);
		if (result.getEntity() != null) {
			int statusCode = result.getStatus();
			DBObject entityResult = (DBObject) result.getEntity();
			assertEquals("Wrong status code: " + statusCode + " with " + entityResult, 200, statusCode);
		}
	}

	@Test
	public void testPaymentInfo4() throws EpickurException, AuthenticationException, InvalidRequestException, APIConnectionException, CardException,
			APIException {
		String start = "01/01/2015";
		String end = "01/02/2015";
		String defaultFormat = "MM/dd/yyyy";

		Response result = catererService.paymentInfo(new ObjectId().toHexString(), start, end, defaultFormat);
		if (result.getEntity() != null) {
			int statusCode = result.getStatus();
			DBObject entityResult = (DBObject) result.getEntity();
			assertEquals("Wrong status code: " + statusCode + " with " + entityResult, 404, statusCode);
		}
	}

	private void addOrder(User user, Order order) {
		List<ObjectId> list = null;
		if (!idsOrders.containsKey(user.getId().toHexString())) {
			list = new ArrayList<ObjectId>();
			idsOrders.put(user.getId().toHexString(), list);
		} else {
			list = idsOrders.get(user.getId().toHexString());
		}
		list.add(order.getId());
	}
}
