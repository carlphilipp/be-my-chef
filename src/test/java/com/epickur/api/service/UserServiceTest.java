package com.epickur.api.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Response;

import org.bson.types.ObjectId;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import com.epickur.api.TestUtils;
import com.epickur.api.entity.Key;
import com.epickur.api.entity.Order;
import com.epickur.api.entity.User;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.exception.EpickurIllegalArgument;
import com.epickur.api.integration.UserIntegrationTest;
import com.mongodb.DBObject;
import com.stripe.Stripe;
import com.stripe.exception.APIConnectionException;
import com.stripe.exception.APIException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;

public class UserServiceTest {

	private static UserService service;
	private static List<ObjectId> idsToDeleteUser;
	private static Map<String, ObjectId> idsToDeleteOrder;
	private static ContainerRequestContext context;

	@BeforeClass
	public static void beforeClass() throws Exception {
		context = mock(ContainerRequestContext.class);
		Key key = TestUtils.generateRandomKey();
		Mockito.when(context.getProperty("key")).thenReturn(key);
		service = new UserService();
		idsToDeleteUser = new ArrayList<ObjectId>();
		idsToDeleteOrder = new HashMap<String, ObjectId>();
		InputStreamReader in = null;
		try {
			in = new InputStreamReader(UserIntegrationTest.class.getClass().getResourceAsStream("/test.properties"));
			Properties prop = new Properties();
			prop.load(in);
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
	}

	@AfterClass
	public static void afterClass() throws EpickurException {
		for (ObjectId id : idsToDeleteUser) {
			service.delete(id.toHexString(), context);
		}
		for (Entry<String, ObjectId> entry : idsToDeleteOrder.entrySet()) {
			service.deleteOneOrder(entry.getKey(), entry.getValue().toHexString(), context);
		}
	}

	@Test
	public void testCreate() throws EpickurException {
		User user = TestUtils.generateRandomUser();
		Response result = service.create(false, false, user, context);
		if (result.getEntity() != null) {
			User userResult = (User) result.getEntity();
			assertNotNull(userResult.getId());
			idsToDeleteUser.add(userResult.getId());
		} else {
			fail("User returned is null");
		}
	}

	@Test
	public void testCreate2() throws EpickurException {
		User user = TestUtils.generateRandomUser();
		Response result = service.create(false, true, user, context);
		if (result.getEntity() != null) {
			User userResult = (User) result.getEntity();
			assertNotNull(userResult.getId());
			idsToDeleteUser.add(userResult.getId());
		} else {
			fail("User returned is null");
		}
	}

	@Test
	public void testCreate3() throws EpickurException {
		User user = TestUtils.generateRandomUser();
		Response result = service.create(false, false, user, context);
		if (result.getEntity() != null) {
			User userResult = (User) result.getEntity();
			assertNotNull(userResult.getId());
			idsToDeleteUser.add(userResult.getId());
		} else {
			fail("User returned is null");
		}
	}

	@Test(expected = EpickurIllegalArgument.class)
	public void testCreateFail() throws EpickurException {
		Response result = service.create(false, false, null, context);
		if (result.getEntity() != null) {
			DBObject dbObject = (DBObject) result.getEntity();
			assertEquals(500, dbObject.get("error"));
		} else {
			fail("User returned is null");
		}
	}

	@Test
	public void testRead() throws EpickurException {
		User user = TestUtils.generateRandomUser();
		Response result = service.create(false, false, user, context);
		if (result.getEntity() != null) {
			User userResult = (User) result.getEntity();
			assertNotNull(userResult.getId());
			idsToDeleteUser.add(userResult.getId());
			// Remove code to be able to compare
			userResult.setCode(null);

			Response result2 = service.read(userResult.getId().toHexString(), context);
			if (result2.getEntity() != null) {
				User userResult2 = (User) result2.getEntity();
				assertNotNull(userResult2.getId());
				assertEquals(userResult, userResult2);
			} else {
				fail("User returned is null");
			}
		} else {
			fail("User returned is null");
		}
	}

	@Test(expected = EpickurIllegalArgument.class)
	public void testReadFail() throws EpickurException {
		service.read(null, context);
	}

	@Test
	public void testReadFail2() throws EpickurException {
		Response result = service.read(new ObjectId().toHexString(), context);
		if (result.getEntity() != null) {
			DBObject dbObject = (DBObject) result.getEntity();
			assertEquals(404, dbObject.get("error"));
		} else {
			fail("User returned is null");
		}
	}

	@Test
	public void testUpdate() throws EpickurException {
		User user = TestUtils.generateRandomUser();
		Response result = service.create(false, false, user, context);
		if (result.getEntity() != null) {
			User userResult = (User) result.getEntity();
			assertNotNull(userResult.getId());
			idsToDeleteUser.add(userResult.getId());

			User dishResultModified = userResult.clone();
			String newEmail = TestUtils.generateRandomString();
			dishResultModified.setEmail(newEmail);
			User dishResultModifiedCopy = dishResultModified.clone();

			Response result2 = service.update(dishResultModified.getId().toHexString(), dishResultModified, context);
			if (result2.getEntity() != null) {
				User userResult2 = (User) result2.getEntity();
				assertNotNull(userResult2.getId());
				assertEquals(dishResultModifiedCopy.getCreatedAt(), userResult2.getCreatedAt());
				assertEquals(newEmail, userResult2.getEmail());
			} else {
				fail(" returned is null");
			}
		} else {
			fail("User returned is null");
		}
	}

	@Test
	public void testUpdate2() throws EpickurException {
		User user = TestUtils.generateRandomUser();
		String password = user.getPassword();
		Response result = service.create(false, false, user, context);
		if (result.getEntity() != null) {
			User userResult = (User) result.getEntity();
			assertNotNull(userResult.getId());
			idsToDeleteUser.add(userResult.getId());

			User dishResultModified = userResult.clone();
			dishResultModified.setNewPassword("new password");
			dishResultModified.setPassword(password);
			User dishResultModifiedCopy = dishResultModified.clone();

			Response result2 = service.update(dishResultModified.getId().toHexString(), dishResultModified, context);
			if (result2.getEntity() != null) {
				User userResult2 = (User) result2.getEntity();
				assertNotNull(userResult2.getId());
				assertEquals(dishResultModifiedCopy.getCreatedAt(), userResult2.getCreatedAt());
			} else {
				fail(" returned is null");
			}
		} else {
			fail("User returned is null");
		}
	}

	@Test
	public void testUpdate3() throws EpickurException {
		User user = TestUtils.generateRandomUser();
		Response result = service.create(false, false, user, context);
		if (result.getEntity() != null) {
			User userResult = (User) result.getEntity();
			assertNotNull(userResult.getId());
			idsToDeleteUser.add(userResult.getId());

			User dishResultModified = userResult.clone();
			String newEmail = TestUtils.generateRandomString();
			dishResultModified.setEmail(newEmail);
			User dishResultModifiedCopy = dishResultModified.clone();

			Response result2 = service.update(dishResultModified.getId().toHexString(), dishResultModified, context);
			if (result2.getEntity() != null) {
				User userResult2 = (User) result2.getEntity();
				assertNotNull(userResult2.getId());
				assertEquals(dishResultModifiedCopy.getCreatedAt(), userResult2.getCreatedAt());
				assertEquals(newEmail, userResult2.getEmail());
			} else {
				fail(" returned is null");
			}
		} else {
			fail("User returned is null");
		}
	}

	@Test
	public void testUpdateFail() throws EpickurException {
		User user = TestUtils.generateRandomUser();
		user.setId(new ObjectId());
		user.setAllow(null);
		Response result = service.update(user.getId().toHexString(), user, context);
		if (result.getEntity() != null) {
			DBObject dbObject = (DBObject) result.getEntity();
			assertEquals(404, dbObject.get("error"));
		} else {
			fail("User returned is null");
		}
	}

	@Test(expected = EpickurIllegalArgument.class)
	public void testUpdateFail2() throws EpickurException {
		Response result = service.update(null, null, context);
		if (result.getEntity() != null) {
			DBObject dbObject = (DBObject) result.getEntity();
			assertEquals(500, dbObject.get("error"));
		} else {
			fail("User returned is null");
		}
	}

	@Test(expected = EpickurIllegalArgument.class)
	public void testUpdateFail3() throws EpickurException {
		User user = TestUtils.generateRandomUser();
		Response result = service.create(false, false, user, context);
		if (result.getEntity() != null) {
			User userResult = (User) result.getEntity();
			assertNotNull(userResult.getId());
			idsToDeleteUser.add(userResult.getId());

			User dishResultModified = userResult.clone();
			dishResultModified.setNewPassword("new password");

			Response result2 = service.update(dishResultModified.getId().toHexString(), dishResultModified, context);
			if (result2.getEntity() != null) {
				DBObject dbObject = (DBObject) result2.getEntity();
				assertEquals(500, dbObject.get("error"));
			} else {
				fail(" returned is null");
			}
		} else {
			fail("User returned is null");
		}
	}

	@Test(expected = EpickurIllegalArgument.class)
	public void testUpdateFail4() throws EpickurException {
		User user = TestUtils.generateRandomUser();
		user.setId(new ObjectId());
		Response result = service.update(null, user, context);
		if (result.getEntity() != null) {
			DBObject dbObject = (DBObject) result.getEntity();
			assertEquals(500, dbObject.get("error"));
		} else {
			fail("User returned is null");
		}
	}

	@Test(expected = EpickurIllegalArgument.class)
	public void testUpdateFail5() throws EpickurException {
		User user = TestUtils.generateRandomUser();
		user.setId(new ObjectId());
		Response result = service.update(user.getId().toHexString(), null, context);
		if (result.getEntity() != null) {
			DBObject dbObject = (DBObject) result.getEntity();
			assertEquals(500, dbObject.get("error"));
		} else {
			fail("User returned is null");
		}
	}

	@Test(expected = EpickurIllegalArgument.class)
	public void testUpdateFail6() throws EpickurException {
		User user = TestUtils.generateRandomUser();
		user.setId(new ObjectId());
		service.update("", user, context);
	}

	@Test
	public void testDelete() throws EpickurException {
		User user = TestUtils.generateRandomUser();
		Response result = service.create(false, false, user, context);
		if (result.getEntity() != null) {
			User userResult = (User) result.getEntity();
			assertNotNull(userResult.getId());
			idsToDeleteUser.add(userResult.getId());

			Response result2 = service.delete(userResult.getId().toHexString(), context);
			if (result2.getEntity() != null) {
				DBObject userResult2 = (DBObject) result2.getEntity();
				assertTrue((Boolean) userResult2.get("deleted"));
			} else {
				fail("Answer is null");
			}
		} else {
			fail("User returned is null");
		}
	}

	@Test(expected = EpickurIllegalArgument.class)
	public void testDeleteFail() throws EpickurException {
		Response result = service.delete(null, context);
		if (result.getEntity() != null) {
			DBObject dbObject = (DBObject) result.getEntity();
			assertEquals(500, dbObject.get("error"));
		} else {
			fail("User returned is null");
		}
	}

	@Test
	public void testDeleteFail2() throws EpickurException {
		Response result = service.delete(new ObjectId().toHexString(), context);
		if (result.getEntity() != null) {
			DBObject dbObject = (DBObject) result.getEntity();
			assertEquals(404, dbObject.get("error"));
		} else {
			fail("User returned is null");
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testReadAll() throws EpickurException {
		User user = TestUtils.generateRandomUser();
		Response result = service.create(false, false, user, context);
		if (result.getEntity() != null) {
			User userResult = (User) result.getEntity();
			assertNotNull(userResult.getId());
			idsToDeleteUser.add(userResult.getId());
			// To pass the test
			userResult.setCode(null);

			Response result2 = service.readAll(context);
			if (result2.getEntity() != null) {
				List<User> userResult2 = (List<User>) result2.getEntity();
				for (User us : userResult2) {
					if (us.getId().equals(userResult.getId())) {
						assertEquals(userResult, us);
					}
				}
			} else {
				fail("User list returned is null");
			}
		} else {
			fail("User returned is null");
		}
	}

	@Test
	public void testAddOneOrder() throws EpickurException, AuthenticationException, InvalidRequestException, APIConnectionException, CardException, APIException {
		User user = TestUtils.generateRandomUser();
		Response result = service.create(false, false, user, context);
		if (result.getEntity() != null) {
			User userResult = (User) result.getEntity();
			assertNotNull(userResult.getId());
			idsToDeleteUser.add(userResult.getId());

			Order order = TestUtils.generateRandomOrder();
			Response result2 = service.createOneOrder(userResult.getId().toHexString(), false, order);
			if (result.getEntity() != null) {
				Order userResult2 = (Order) result2.getEntity();
				assertNotNull(userResult2.getId());
				idsToDeleteOrder.put(userResult.getId().toHexString(), userResult2.getId());
			} else {
				fail("User returned is null");
			}
		} else {
			fail("User returned is null");
		}
	}

	@Test(expected = EpickurIllegalArgument.class)
	public void testAddOneOrderFail() throws EpickurException, AuthenticationException, InvalidRequestException, APIConnectionException, CardException, APIException {
		User user = TestUtils.generateRandomUser();
		Response result = service.create(false, false, user, context);
		if (result.getEntity() != null) {
			User userResult = (User) result.getEntity();
			assertNotNull(userResult.getId());
			idsToDeleteUser.add(userResult.getId());

			Order order = TestUtils.generateRandomOrder();
			Response result2 = service.createOneOrder(null, false, order);
			if (result2.getEntity() != null) {
				DBObject dbObject = (DBObject) result2.getEntity();
				assertEquals(500, dbObject.get("error"));
			} else {
				fail("Order returned is null");
			}
		} else {
			fail("User returned is null");
		}
	}

	@Test(expected = EpickurIllegalArgument.class)
	public void testAddOneOrderFail2() throws EpickurException {
		User user = TestUtils.generateRandomUser();
		Response result = service.create(false, false, user, context);
		if (result.getEntity() != null) {
			User userResult = (User) result.getEntity();
			assertNotNull(userResult.getId());
			idsToDeleteUser.add(userResult.getId());

			Response result2 = service.createOneOrder("", false, null);
			if (result2.getEntity() != null) {
				DBObject dbObject = (DBObject) result2.getEntity();
				assertEquals(500, dbObject.get("error"));
			} else {
				fail("Order returned is null");
			}
		} else {
			fail("User returned is null");
		}
	}

	@Test
	public void testReadOneOrder() throws EpickurException, AuthenticationException, InvalidRequestException, APIConnectionException, CardException, APIException {
		User user = TestUtils.generateRandomUser();
		Response result = service.create(false, false, user, context);
		if (result.getEntity() != null) {
			User userResult = (User) result.getEntity();
			assertNotNull(userResult.getId());
			idsToDeleteUser.add(userResult.getId());

			Order order = TestUtils.generateRandomOrder();
			Response result2 = service.createOneOrder(userResult.getId().toHexString(), false, order);
			if (result2.getEntity() != null) {
				Order orderResult = (Order) result2.getEntity();
				assertNotNull(orderResult.getId());
				idsToDeleteOrder.put(userResult.getId().toHexString(), orderResult.getId());

				Response result3 = service.readOneOrder(userResult.getId().toHexString(), orderResult.getId().toHexString(), context);
				if (result3.getEntity() != null) {
					Order userResult3 = (Order) result3.getEntity();
					assertNotNull(userResult3.getId());
					assertEquals(orderResult, userResult3);
				} else {
					fail("Order returned is null");
				}
			} else {
				fail("Order returned is null");
			}
		} else {
			fail("User returned is null");
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testReadAllOrder() throws EpickurException, AuthenticationException, InvalidRequestException, APIConnectionException, CardException, APIException {
		User user = TestUtils.generateRandomUser();
		Response result = service.create(false, false, user, context);
		if (result.getEntity() != null) {
			User userResult = (User) result.getEntity();
			assertNotNull(userResult.getId());
			idsToDeleteUser.add(userResult.getId());

			Order order = TestUtils.generateRandomOrder();
			Response result2 = service.createOneOrder(userResult.getId().toHexString(), false, order);
			if (result2.getEntity() != null) {
				Order userResult2 = (Order) result2.getEntity();
				assertNotNull(userResult2.getId());
				idsToDeleteOrder.put(userResult.getId().toHexString(), userResult2.getId());

				Response result3 = service.readAllOrders(userResult.getId().toHexString(), context);
				if (result3.getEntity() != null) {
					List<Order> userResult3 = (List<Order>) result3.getEntity();
					for (Order or : userResult3) {
						if (or.getId().toHexString().equals(userResult2.getId().toHexString())) {
							assertNotNull(or.getId());
							assertEquals(userResult2, or);
						}
					}
				} else {
					fail("List Order returned is null");
				}
			} else {
				fail("Order returned is null");
			}
		} else {
			fail("User returned is null");
		}
	}

	@Test(expected = EpickurIllegalArgument.class)
	public void testReadAllOrderFail() throws EpickurException, AuthenticationException, InvalidRequestException, APIConnectionException, CardException, APIException {
		User user = TestUtils.generateRandomUser();
		Response result = service.create(false, false, user, context);
		if (result.getEntity() != null) {
			User userResult = (User) result.getEntity();
			assertNotNull(userResult.getId());
			idsToDeleteUser.add(userResult.getId());

			Order order = TestUtils.generateRandomOrder();
			Response result2 = service.createOneOrder(userResult.getId().toHexString(), false, order);
			if (result2.getEntity() != null) {
				Order userResult2 = (Order) result2.getEntity();
				assertNotNull(userResult2.getId());
				idsToDeleteOrder.put(userResult.getId().toHexString(), userResult2.getId());

				Response result3 = service.readAllOrders(null, context);
				if (result3.getEntity() != null) {
					DBObject dbObject = (DBObject) result3.getEntity();
					assertEquals(500, dbObject.get("error"));
				} else {
					fail("List Order returned is null");
				}
			} else {
				fail("Order returned is null");
			}
		} else {
			fail("User returned is null");
		}
	}

	@Test(expected = EpickurIllegalArgument.class)
	public void testReadOneOrderFail() throws EpickurException, AuthenticationException, InvalidRequestException, APIConnectionException, CardException, APIException {
		User user = TestUtils.generateRandomUser();
		Response result = service.create(false, false, user, context);
		if (result.getEntity() != null) {
			User userResult = (User) result.getEntity();
			assertNotNull(userResult.getId());
			idsToDeleteUser.add(userResult.getId());

			Order order = TestUtils.generateRandomOrder();
			Response result2 = service.createOneOrder(userResult.getId().toHexString(), false, order);
			if (result2.getEntity() != null) {
				Order userResult2 = (Order) result2.getEntity();
				assertNotNull(userResult2.getId());
				idsToDeleteOrder.put(userResult.getId().toHexString(), userResult2.getId());

				Response result3 = service.readOneOrder(null, userResult2.getId().toHexString(), context);
				if (result3.getEntity() != null) {
					DBObject dbObject = (DBObject) result3.getEntity();
					assertEquals(500, dbObject.get("error"));
				} else {
					fail("Order returned is null");
				}
			} else {
				fail("Order returned is null");
			}
		} else {
			fail("User returned is null");
		}
	}

	@Test(expected = EpickurIllegalArgument.class)
	public void testReadOneOrderFail2() throws EpickurException, AuthenticationException, InvalidRequestException, APIConnectionException, CardException, APIException {
		User user = TestUtils.generateRandomUser();
		Response result = service.create(false, false, user, context);
		if (result.getEntity() != null) {
			User userResult = (User) result.getEntity();
			assertNotNull(userResult.getId());
			idsToDeleteUser.add(userResult.getId());

			Order order = TestUtils.generateRandomOrder();
			Response result2 = service.createOneOrder(userResult.getId().toHexString(), false, order);
			if (result2.getEntity() != null) {
				Order userResult2 = (Order) result2.getEntity();
				assertNotNull(userResult2.getId());
				idsToDeleteOrder.put(userResult.getId().toHexString(), userResult2.getId());

				Response result3 = service.readOneOrder(userResult.getId().toHexString(), null, context);
				if (result3.getEntity() != null) {
					DBObject dbObject = (DBObject) result3.getEntity();
					assertEquals(500, dbObject.get("error"));
				} else {
					fail("Order returned is null");
				}
			} else {
				fail("Order returned is null");
			}
		} else {
			fail("User returned is null");
		}
	}

	@Test
	public void testReadOneOrderFail3() throws EpickurException, AuthenticationException, InvalidRequestException, APIConnectionException, CardException, APIException {
		User user = TestUtils.generateRandomUser();
		Response result = service.create(false, false, user, context);
		if (result.getEntity() != null) {
			User userResult = (User) result.getEntity();
			assertNotNull(userResult.getId());
			idsToDeleteUser.add(userResult.getId());

			Order order = TestUtils.generateRandomOrder();
			Response result2 = service.createOneOrder(userResult.getId().toHexString(), false, order);
			if (result2.getEntity() != null) {
				Order userResult2 = (Order) result2.getEntity();
				assertNotNull(userResult2.getId());
				idsToDeleteOrder.put(userResult.getId().toHexString(), userResult2.getId());

				Response result3 = service.readOneOrder(userResult.getId().toHexString(), new ObjectId().toHexString(), context);
				if (result3.getEntity() != null) {
					DBObject dbObject = (DBObject) result3.getEntity();
					assertEquals(404, dbObject.get("error"));
				} else {
					fail("Order returned is null");
				}
			} else {
				fail("Order returned is null");
			}
		} else {
			fail("User returned is null");
		}
	}

	@Test
	public void testUpdateOneOrder() throws EpickurException, AuthenticationException, InvalidRequestException, APIConnectionException, CardException, APIException {
		User user = TestUtils.generateRandomUser();
		Response result = service.create(false, false, user, context);
		if (result.getEntity() != null) {
			User userResult = (User) result.getEntity();
			assertNotNull(userResult.getId());
			idsToDeleteUser.add(userResult.getId());

			Order order = TestUtils.generateRandomOrder();
			Response result2 = service.createOneOrder(userResult.getId().toHexString(), false, order);
			if (result2.getEntity() != null) {
				Order userResult2 = (Order) result2.getEntity();
				assertNotNull(userResult2.getId());
				idsToDeleteOrder.put(userResult.getId().toHexString(), userResult2.getId());

				userResult2.setDescription("new description");
				Response result3 = service.updateOneOrder(userResult.getId().toHexString(), userResult2.getId().toHexString(), userResult2, context);
				if (result3.getEntity() != null) {
					Order userResult3 = (Order) result3.getEntity();
					assertNotNull(userResult3.getId());
					assertEquals("new description", userResult3.getDescription());
				} else {
					fail("order returned is null");
				}
			} else {
				fail("order returned is null");
			}
		} else {
			fail("User returned is null");
		}
	}

	@Test(expected = EpickurIllegalArgument.class)
	public void testUpdateOneOrderFail() throws EpickurException, AuthenticationException, InvalidRequestException, APIConnectionException, CardException, APIException {
		User user = TestUtils.generateRandomUser();
		Response result = service.create(false, false, user, context);
		if (result.getEntity() != null) {
			User userResult = (User) result.getEntity();
			assertNotNull(userResult.getId());
			idsToDeleteUser.add(userResult.getId());

			Order order = TestUtils.generateRandomOrder();
			Response result2 = service.createOneOrder(userResult.getId().toHexString(), false, order);
			if (result2.getEntity() != null) {
				Order userResult2 = (Order) result2.getEntity();
				assertNotNull(userResult2.getId());
				idsToDeleteOrder.put(userResult.getId().toHexString(), userResult2.getId());

				userResult2.setDescription("new description");
				Response result3 = service.updateOneOrder(null, userResult2.getId().toHexString(), userResult2, context);
				if (result3.getEntity() != null) {
					DBObject dbObject = (DBObject) result3.getEntity();
					assertEquals(500, dbObject.get("error"));
				} else {
					fail("order returned is null");
				}
			} else {
				fail("order returned is null");
			}
		} else {
			fail("User returned is null");
		}
	}

	@Test(expected = EpickurIllegalArgument.class)
	public void testUpdateOneOrderFail2() throws EpickurException, AuthenticationException, InvalidRequestException, APIConnectionException, CardException, APIException {
		User user = TestUtils.generateRandomUser();
		Response result = service.create(false, false, user, context);
		if (result.getEntity() != null) {
			User userResult = (User) result.getEntity();
			assertNotNull(userResult.getId());
			idsToDeleteUser.add(userResult.getId());

			Order order = TestUtils.generateRandomOrder();
			Response result2 = service.createOneOrder(userResult.getId().toHexString(), false, order);
			if (result2.getEntity() != null) {
				Order userResult2 = (Order) result2.getEntity();
				assertNotNull(userResult2.getId());
				idsToDeleteOrder.put(userResult.getId().toHexString(), userResult2.getId());

				userResult2.setDescription("new description");
				Response result3 = service.updateOneOrder(userResult.getId().toHexString(), null, userResult2, context);
				if (result3.getEntity() != null) {
					DBObject dbObject = (DBObject) result3.getEntity();
					assertEquals(500, dbObject.get("error"));
				} else {
					fail("order returned is null");
				}
			} else {
				fail("order returned is null");
			}
		} else {
			fail("User returned is null");
		}
	}

	@Test(expected = EpickurIllegalArgument.class)
	public void testUpdateOneOrderFail3() throws EpickurException, AuthenticationException, InvalidRequestException, APIConnectionException, CardException, APIException {
		User user = TestUtils.generateRandomUser();
		Response result = service.create(false, false, user, context);
		if (result.getEntity() != null) {
			User userResult = (User) result.getEntity();
			assertNotNull(userResult.getId());
			idsToDeleteUser.add(userResult.getId());

			Order order = TestUtils.generateRandomOrder();
			Response result2 = service.createOneOrder(userResult.getId().toHexString(), false, order);
			if (result2.getEntity() != null) {
				Order userResult2 = (Order) result2.getEntity();
				assertNotNull(userResult2.getId());
				idsToDeleteOrder.put(userResult.getId().toHexString(), userResult2.getId());

				userResult2.setDescription("new description");
				Response result3 = service.updateOneOrder(userResult.getId().toHexString(), userResult2.getId().toHexString(), null, context);
				if (result3.getEntity() != null) {
					DBObject dbObject = (DBObject) result3.getEntity();
					assertEquals(500, dbObject.get("error"));
				} else {
					fail("order returned is null");
				}
			} else {
				fail("order returned is null");
			}
		} else {
			fail("User returned is null");
		}
	}

	@Test
	public void testUpdateOneOrderFail4() throws EpickurException, AuthenticationException, InvalidRequestException, APIConnectionException, CardException, APIException {
		User user = TestUtils.generateRandomUser();
		Response result = service.create(false, false, user, context);
		if (result.getEntity() != null) {
			User userResult = (User) result.getEntity();
			assertNotNull(userResult.getId());
			idsToDeleteUser.add(userResult.getId());

			Order order = TestUtils.generateRandomOrder();
			order.setId(new ObjectId());
			Response result3 = service.updateOneOrder(userResult.getId().toHexString(), order.getId().toHexString(), order, context);
			if (result3.getEntity() != null) {
				DBObject dbObject = (DBObject) result3.getEntity();
				assertEquals(404, dbObject.get("error"));
			} else {
				fail("Order returned is null");
			}
		} else {
			fail("User returned is null");
		}
	}

	@Test
	public void testdeleteOneOrder() throws EpickurException, AuthenticationException, InvalidRequestException, APIConnectionException, CardException, APIException {
		User user = TestUtils.generateRandomUser();
		Response result = service.create(false, false, user, context);
		if (result.getEntity() != null) {
			User userResult = (User) result.getEntity();
			assertNotNull(userResult.getId());
			idsToDeleteUser.add(userResult.getId());

			Order order = TestUtils.generateRandomOrder();
			order.setId(null);
			Response result2 = service.createOneOrder(userResult.getId().toHexString(), false, order);
			if (result2.getEntity() != null) {
				Order userResult2 = (Order) result2.getEntity();
				assertNotNull(userResult2.getId());
				idsToDeleteOrder.put(userResult.getId().toHexString(), userResult2.getId());
				Response result3 = service.deleteOneOrder(userResult.getId().toHexString(), userResult2.getId().toHexString(), context);
				if (result3.getEntity() != null) {
					DBObject res = (DBObject) result3.getEntity();
					assertNotNull(res.get("deleted"));
					assertTrue((Boolean) res.get("deleted"));
				} else {
					fail("Order returned is null");
				}
			} else {
				fail("Order returned is null");
			}
		} else {
			fail("User returned is null");
		}
	}

	@Test(expected = EpickurIllegalArgument.class)
	public void testdeleteOneOrderFail() throws EpickurException, AuthenticationException, InvalidRequestException, APIConnectionException, CardException, APIException {
		User user = TestUtils.generateRandomUser();
		Response result = service.create(false, false, user, context);
		if (result.getEntity() != null) {
			User userResult = (User) result.getEntity();
			assertNotNull(userResult.getId());
			idsToDeleteUser.add(userResult.getId());

			Order order = TestUtils.generateRandomOrder();
			Response result2 = service.createOneOrder(userResult.getId().toHexString(), false, order);
			if (result2.getEntity() != null) {
				Order userResult2 = (Order) result2.getEntity();
				assertNotNull(userResult2.getId());
				idsToDeleteOrder.put(userResult.getId().toHexString(), userResult2.getId());
				Response result3 = service.deleteOneOrder(null, userResult2.getId().toHexString(), context);
				if (result3.getEntity() != null) {
					DBObject dbObject = (DBObject) result3.getEntity();
					assertEquals(500, dbObject.get("error"));
				} else {
					fail("Order returned is null");
				}
			} else {
				fail("Order returned is null");
			}
		} else {
			fail("User returned is null");
		}
	}

	@Test(expected = EpickurIllegalArgument.class)
	public void testdeleteOneOrderFail2() throws EpickurException, AuthenticationException, InvalidRequestException, APIConnectionException, CardException, APIException {
		User user = TestUtils.generateRandomUser();
		Response result = service.create(false, false, user, context);
		if (result.getEntity() != null) {
			User userResult = (User) result.getEntity();
			assertNotNull(userResult.getId());
			idsToDeleteUser.add(userResult.getId());

			Order order = TestUtils.generateRandomOrder();
			Response result2 = service.createOneOrder(userResult.getId().toHexString(), false, order);
			if (result2.getEntity() != null) {
				Order userResult2 = (Order) result2.getEntity();
				assertNotNull(userResult2.getId());
				idsToDeleteOrder.put(userResult.getId().toHexString(), userResult2.getId());
				Response result3 = service.deleteOneOrder(userResult.getId().toHexString(), null, context);
				if (result3.getEntity() != null) {
					DBObject dbObject = (DBObject) result3.getEntity();
					assertEquals(500, dbObject.get("error"));
				} else {
					fail("Order returned is null");
				}
			} else {
				fail("Order returned is null");
			}
		} else {
			fail("User returned is null");
		}
	}
}
