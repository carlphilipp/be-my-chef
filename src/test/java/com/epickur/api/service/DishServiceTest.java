package com.epickur.api.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Response;

import org.bson.types.ObjectId;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import com.epickur.api.TestUtils;
import com.epickur.api.entity.Caterer;
import com.epickur.api.entity.Dish;
import com.epickur.api.entity.Key;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.exception.EpickurIllegalArgument;
import com.mongodb.DBObject;

public class DishServiceTest {

	private static DishService service;
	private static CatererService catererService;
	private static List<ObjectId> idsToDelete;
	private static List<ObjectId> idsCatererToDelete;
	private static ContainerRequestContext context;

	@BeforeClass
	public static void beforeClass() {
		service = new DishService();
		catererService = new CatererService();
		idsToDelete = new ArrayList<ObjectId>();
		idsCatererToDelete = new ArrayList<ObjectId>();
		context = mock(ContainerRequestContext.class);
		Key key = TestUtils.generateRandomKey();
		Mockito.when(context.getProperty("key")).thenReturn(key);
	}

	@AfterClass
	public static void afterClass() throws EpickurException {
		for (ObjectId id : idsToDelete) {
			service.delete(id.toHexString(), context);
		}
		for (ObjectId id : idsCatererToDelete) {
			catererService.delete(id.toHexString(), context);
		}
	}

	@Test
	public void testCreate() throws EpickurException {
		Dish dish = TestUtils.generateRandomDish();
		dish.getCaterer().setId(null);
		Caterer cat = TestUtils.createCaterer(dish.getCaterer(), null);
		dish.setCaterer(cat);
		idsCatererToDelete.add(cat.getId());
		Response result = service.create(dish, context);
		if (result.getEntity() != null) {
			int statusCode = result.getStatus();
			assertEquals("Wrong status code: " + statusCode + " with " + result.getEntity(), 200, statusCode);
			Dish dishResult = (Dish) result.getEntity();
			assertNotNull(dishResult.getId());
			idsToDelete.add(dishResult.getId());
		} else {
			fail("Dish returned is null");
		}
	}

	@Test(expected = EpickurIllegalArgument.class)
	public void testCreateFail() throws EpickurException {
		Response result = service.create(null, context);
		if (result.getEntity() != null) {
			DBObject dbObject = (DBObject) result.getEntity();
			assertEquals(500, dbObject.get("error"));
		} else {
			fail("Fail");
		}
	}

	@Test
	public void testRead() throws EpickurException {
		Dish dish = TestUtils.generateRandomDish();
		dish.getCaterer().setId(null);
		Caterer cat = TestUtils.createCaterer(dish.getCaterer(), null);
		dish.setCaterer(cat);
		idsCatererToDelete.add(cat.getId());
		Response result = service.create(dish, context);
		if (result.getEntity() != null) {
			int statusCode = result.getStatus();
			assertEquals("Wrong status code: " + statusCode + " with " + result.getEntity(), 200, statusCode);
			Dish dishResult = (Dish) result.getEntity();
			assertNotNull(dishResult.getId());
			idsToDelete.add(dishResult.getId());

			Response result2 = service.read(dishResult.getId().toHexString());
			if (result2.getEntity() != null) {
				statusCode = result2.getStatus();
				assertEquals("Wrong status code: " + statusCode + " with " + result2.getEntity(), 200, statusCode);
				Dish dishResult2 = (Dish) result2.getEntity();
				assertNotNull(dishResult2.getId());
				assertEquals(dishResult, dishResult2);
			} else {
				fail("Dish returned is null");
			}
		} else {
			fail("Dish returned is null");
		}
	}

	@Test
	public void testReadFail() throws EpickurException {
		Response result = service.read(new ObjectId().toHexString());
		if (result.getEntity() != null) {
			DBObject dbObject = (DBObject) result.getEntity();
			assertEquals(404, dbObject.get("error"));
		} else {
			fail("Fail");
		}
	}

	@Test(expected = EpickurIllegalArgument.class)
	public void testReadFail2() throws EpickurException {
		Response result = service.read(null);
		if (result.getEntity() != null) {
			DBObject dbObject = (DBObject) result.getEntity();
			assertEquals(500, dbObject.get("error"));
		} else {
			fail("Fail");
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testReadAll() throws EpickurException {
		Dish dish = TestUtils.generateRandomDish();
		dish.getCaterer().setId(null);
		Caterer cat = TestUtils.createCaterer(dish.getCaterer(), null);
		dish.setCaterer(cat);
		idsCatererToDelete.add(cat.getId());
		Response result = service.create(dish, context);
		if (result.getEntity() != null) {
			int statusCode = result.getStatus();
			assertEquals("Wrong status code: " + statusCode + " with " + result.getEntity(), 200, statusCode);
			Dish dishResult = (Dish) result.getEntity();
			assertNotNull(dishResult.getId());
			idsToDelete.add(dishResult.getId());

			Response result2 = service.readAll();
			if (result2.getEntity() != null) {
				List<Dish> dishResult2 = (List<Dish>) result2.getEntity();
				for (Dish di : dishResult2) {
					if (di.getId().equals(dishResult.getId())) {
						assertEquals(dishResult, di);
					}
				}
			} else {
				fail("Dish list returned is null");
			}
		} else {
			fail("Dish returned is null");
		}
	}

	@Test
	public void testUpdate() throws EpickurException {
		Dish dish = TestUtils.generateRandomDish();
		dish.getCaterer().setId(null);
		Caterer cat = TestUtils.createCaterer(dish.getCaterer(), null);
		dish.setCaterer(cat);
		idsCatererToDelete.add(cat.getId());
		Response result = service.create(dish, context);
		if (result.getEntity() != null) {
			int statusCode = result.getStatus();
			assertEquals("Wrong status code: " + statusCode + " with " + result.getEntity(), 200, statusCode);
			Dish dishResult = (Dish) result.getEntity();
			assertNotNull(dishResult.getId());
			idsToDelete.add(dishResult.getId());

			Dish dishResultModified = dishResult.clone();
			dishResultModified.setDescription("new descr");
			Dish dishResultModifiedCopy = dishResultModified.clone();

			Response result2 = service.update(dishResultModified.getId().toHexString(), dishResultModified, context);
			if (result2.getEntity() != null) {
				Dish dishResult2 = (Dish) result2.getEntity();
				assertNotNull(dishResult2.getId());
				assertEquals(dishResultModifiedCopy.getCreatedAt(), dishResult2.getCreatedAt());
				assertEquals("new descr", dishResult2.getDescription());
			} else {
				fail("Dish returned is null");
			}
		} else {
			fail("Dish returned is null");
		}
	}

	@Test
	public void testUpdate2() throws EpickurException {
		Dish dish = TestUtils.generateRandomDish();
		dish.getCaterer().setId(null);
		Caterer cat = TestUtils.createCaterer(dish.getCaterer(), null);
		dish.setCaterer(cat);
		idsCatererToDelete.add(cat.getId());

		Response result = service.create(dish, context);
		if (result.getEntity() != null) {
			int statusCode = result.getStatus();
			assertEquals("Wrong status code: " + statusCode + " with " + result.getEntity(), 200, statusCode);
			Dish dishResult = (Dish) result.getEntity();
			assertNotNull(dishResult.getId());
			idsToDelete.add(dishResult.getId());

			Dish dishResultModified = dishResult.clone();
			dishResultModified.setDescription("new descr");
			Dish dishResultModifiedCopy = dishResultModified.clone();

			Response result2 = service.update(dishResultModified.getId().toHexString(), dishResultModified, context);
			if (result2.getEntity() != null) {
				Dish dishResult2 = (Dish) result2.getEntity();
				assertNotNull(dishResult2.getId());
				assertEquals(dishResultModifiedCopy.getCreatedAt(), dishResult2.getCreatedAt());
				assertEquals("new descr", dishResult2.getDescription());
			} else {
				fail("Dish returned is null");
			}
		} else {
			fail("Dish returned is null");
		}
	}

	@Test
	public void testUpdateFail() throws EpickurException {
		Dish dish = TestUtils.generateRandomDish();
		dish.setId(new ObjectId());
		Response result = service.update(dish.getId().toHexString(), dish, context);
		if (result.getEntity() != null) {
			DBObject dbObject = (DBObject) result.getEntity();
			assertEquals(404, dbObject.get("error"));
		} else {
			fail("Dish returned is null");
		}
	}

	@Test(expected = EpickurIllegalArgument.class)
	public void testUpdateFail2() throws EpickurException {
		Response result = service.update(null, null, context);
		if (result.getEntity() != null) {
			DBObject dbObject = (DBObject) result.getEntity();
			assertEquals(500, dbObject.get("error"));
		} else {
			fail("Dish returned is null");
		}
	}

	@Test(expected = EpickurIllegalArgument.class)
	public void testUpdateFail3() throws EpickurException {
		Dish dish = TestUtils.generateRandomDish();
		dish.setId(new ObjectId());
		Response result = service.update(null, dish, context);
		if (result.getEntity() != null) {
			DBObject dbObject = (DBObject) result.getEntity();
			assertEquals(500, dbObject.get("error"));
		} else {
			fail("Dish returned is null");
		}
	}

	@Test(expected = EpickurIllegalArgument.class)
	public void testUpdateFail4() throws EpickurException {
		Response result = service.update("", null, context);
		if (result.getEntity() != null) {
			DBObject dbObject = (DBObject) result.getEntity();
			assertEquals(500, dbObject.get("error"));
		} else {
			fail("Dish returned is null");
		}
	}

	@Test(expected = EpickurIllegalArgument.class)
	public void testUpdateFail5() throws EpickurException {
		Dish dish = TestUtils.generateRandomDish();
		dish.setId(new ObjectId());
		service.update("fake id", dish, context);
	}

	@Test
	public void testDelete() throws EpickurException {
		Dish dish = TestUtils.generateRandomDish();
		dish.getCaterer().setId(null);
		Caterer cat = TestUtils.createCaterer(dish.getCaterer(), null);
		dish.setCaterer(cat);
		idsCatererToDelete.add(cat.getId());
		Response result = service.create(dish, context);
		if (result.getEntity() != null) {
			int statusCode = result.getStatus();
			assertEquals("Wrong status code: " + statusCode + " with " + result.getEntity(), 200, statusCode);
			Dish dishResult = (Dish) result.getEntity();
			assertNotNull(dishResult.getId());
			idsToDelete.add(dishResult.getId());

			Response result2 = service.delete(dishResult.getId().toHexString(), context);
			if (result2.getEntity() != null) {
				DBObject dishResult2 = (DBObject) result2.getEntity();
				assertTrue((Boolean) dishResult2.get("deleted"));
			} else {
				fail("Answer is null");
			}
		} else {
			fail("Dish returned is null");
		}
	}

	@Test(expected = EpickurIllegalArgument.class)
	public void testDeleteFail() throws EpickurException {
		Response result = service.delete(null, context);
		if (result.getEntity() != null) {
			DBObject dbObject = (DBObject) result.getEntity();
			assertEquals(500, dbObject.get("error"));
		} else {
			fail("Dish returned is null");
		}
	}
}
