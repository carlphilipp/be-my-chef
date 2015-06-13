package com.epickur.api.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.hamcrest.Matchers.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Response;

import org.bson.types.ObjectId;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import com.epickur.api.TestUtils;
import com.epickur.api.entity.Caterer;
import com.epickur.api.entity.Dish;
import com.epickur.api.entity.Key;
import com.epickur.api.enumeration.DishType;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.exception.EpickurIllegalArgument;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.DBObject;

public class DishServiceTest {

	private static DishService dishService;
	private static CatererService catererService;
	private static List<ObjectId> idsToDelete;
	private static List<ObjectId> idsToDeleteCaterer;
	private static ContainerRequestContext context;

	@BeforeClass
	public static void beforeClass() {
		dishService = new DishService();
		catererService = new CatererService();
		idsToDelete = new ArrayList<ObjectId>();
		idsToDeleteCaterer = new ArrayList<ObjectId>();
		context = mock(ContainerRequestContext.class);
		Key key = TestUtils.generateRandomKey();
		Mockito.when(context.getProperty("key")).thenReturn(key);
	}

	@AfterClass
	public static void afterClass() throws EpickurException {
		for (ObjectId id : idsToDelete) {
			dishService.delete(id.toHexString(), context);
		}
		for (ObjectId id : idsToDeleteCaterer) {
			catererService.delete(id.toHexString(), context);
		}
	}

	@Test
	public void testCreate() throws EpickurException {
		Dish dish = TestUtils.generateRandomDish();
		dish.getCaterer().setId(null);
		Caterer cat = TestUtils.createCaterer(dish.getCaterer(), null);
		dish.setCaterer(cat);
		idsToDeleteCaterer.add(cat.getId());
		Response result = dishService.create(dish, context);
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
		Response result = dishService.create(null, context);
		if (result.getEntity() != null) {
			DBObject dbObject = (DBObject) result.getEntity();
			assertEquals(500, dbObject.get("error"));
		} else {
			fail("Fail");
		}
	}
	
	@Test
	public void testCreateFail2() throws EpickurException {
		Dish dish = TestUtils.generateRandomDish();
		dish.getCaterer().setId(null);
		Caterer cat = TestUtils.createCaterer(dish.getCaterer(), null);
		dish.setCaterer(cat);
		idsToDeleteCaterer.add(cat.getId());
		cat.setId(new ObjectId());
		Response result = dishService.create(dish, context);
		if (result.getEntity() != null) {
			int statusCode = result.getStatus();
			assertEquals("Wrong status code: " + statusCode + " with " + result.getEntity(), 404, statusCode);
		} else {
			fail("Dish returned is null");
		}
	}

	@Test
	public void testRead() throws EpickurException {
		Dish dish = TestUtils.generateRandomDish();
		dish.getCaterer().setId(null);
		Caterer cat = TestUtils.createCaterer(dish.getCaterer(), null);
		dish.setCaterer(cat);
		idsToDeleteCaterer.add(cat.getId());
		Response result = dishService.create(dish, context);
		if (result.getEntity() != null) {
			int statusCode = result.getStatus();
			assertEquals("Wrong status code: " + statusCode + " with " + result.getEntity(), 200, statusCode);
			Dish dishResult = (Dish) result.getEntity();
			assertNotNull(dishResult.getId());
			idsToDelete.add(dishResult.getId());

			Response result2 = dishService.read(dishResult.getId().toHexString());
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
		Response result = dishService.read(new ObjectId().toHexString());
		if (result.getEntity() != null) {
			DBObject dbObject = (DBObject) result.getEntity();
			assertEquals(404, dbObject.get("error"));
		} else {
			fail("Fail");
		}
	}

	@Test(expected = EpickurIllegalArgument.class)
	public void testReadFail2() throws EpickurException {
		Response result = dishService.read(null);
		if (result.getEntity() != null) {
			DBObject dbObject = (DBObject) result.getEntity();
			assertEquals(500, dbObject.get("error"));
		} else {
			fail("Fail");
		}
	}

	@Test
	public void testUpdate() throws EpickurException {
		Dish dish = TestUtils.generateRandomDish();
		dish.getCaterer().setId(null);
		Caterer cat = TestUtils.createCaterer(dish.getCaterer(), null);
		dish.setCaterer(cat);
		idsToDeleteCaterer.add(cat.getId());
		Response result = dishService.create(dish, context);
		if (result.getEntity() != null) {
			int statusCode = result.getStatus();
			assertEquals("Wrong status code: " + statusCode + " with " + result.getEntity(), 200, statusCode);
			Dish dishResult = (Dish) result.getEntity();
			assertNotNull(dishResult.getId());
			idsToDelete.add(dishResult.getId());

			Dish dishResultModified = dishResult.clone();
			dishResultModified.setDescription("new descr");
			Dish dishResultModifiedCopy = dishResultModified.clone();

			Response result2 = dishService.update(dishResultModified.getId().toHexString(), dishResultModified, context);
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
		idsToDeleteCaterer.add(cat.getId());

		Response result = dishService.create(dish, context);
		if (result.getEntity() != null) {
			int statusCode = result.getStatus();
			assertEquals("Wrong status code: " + statusCode + " with " + result.getEntity(), 200, statusCode);
			Dish dishResult = (Dish) result.getEntity();
			assertNotNull(dishResult.getId());
			idsToDelete.add(dishResult.getId());

			Dish dishResultModified = dishResult.clone();
			dishResultModified.setDescription("new descr");
			Dish dishResultModifiedCopy = dishResultModified.clone();

			Response result2 = dishService.update(dishResultModified.getId().toHexString(), dishResultModified, context);
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
		Response result = dishService.update(dish.getId().toHexString(), dish, context);
		if (result.getEntity() != null) {
			DBObject dbObject = (DBObject) result.getEntity();
			assertEquals(404, dbObject.get("error"));
		} else {
			fail("Dish returned is null");
		}
	}

	@Test(expected = EpickurIllegalArgument.class)
	public void testUpdateFail2() throws EpickurException {
		Response result = dishService.update(null, null, context);
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
		Response result = dishService.update(null, dish, context);
		if (result.getEntity() != null) {
			DBObject dbObject = (DBObject) result.getEntity();
			assertEquals(500, dbObject.get("error"));
		} else {
			fail("Dish returned is null");
		}
	}

	@Test(expected = EpickurIllegalArgument.class)
	public void testUpdateFail4() throws EpickurException {
		Response result = dishService.update("", null, context);
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
		dishService.update("fake id", dish, context);
	}

	@Test
	public void testDelete() throws EpickurException {
		Dish dish = TestUtils.generateRandomDish();
		dish.getCaterer().setId(null);
		Caterer cat = TestUtils.createCaterer(dish.getCaterer(), null);
		dish.setCaterer(cat);
		idsToDeleteCaterer.add(cat.getId());
		Response result = dishService.create(dish, context);
		if (result.getEntity() != null) {
			int statusCode = result.getStatus();
			assertEquals("Wrong status code: " + statusCode + " with " + result.getEntity(), 200, statusCode);
			Dish dishResult = (Dish) result.getEntity();
			assertNotNull(dishResult.getId());
			idsToDelete.add(dishResult.getId());

			Response result2 = dishService.delete(dishResult.getId().toHexString(), context);
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
		Response result = dishService.delete(null, context);
		if (result.getEntity() != null) {
			DBObject dbObject = (DBObject) result.getEntity();
			assertEquals(500, dbObject.get("error"));
		} else {
			fail("Dish returned is null");
		}
	}
	
	// Search dish
	
	@Test
	public void testSearch() throws EpickurException {
		Dish dish = TestUtils.generateRandomDish();
		Caterer caterer = dish.getCaterer().clone();
		caterer.setId(null);
		Caterer res = TestUtils.createCaterer(caterer, null);
		dish.getCaterer().setId(res.getId());
		Response result = dishService.create(dish, context);
		if (result.getEntity() != null) {
			int statusCode = result.getStatus();
			assertEquals("Wrong status code: " + statusCode + " with " + result.getEntity(), 200, statusCode);
			Dish dishResult = (Dish) result.getEntity();
			assertNotNull(dishResult.getId());
			idsToDelete.add(dishResult.getId());
			idsToDeleteCaterer.add(dishResult.getCaterer().getId());
		} else {
			fail("Dish returned is null");
		}
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testSearch2() throws EpickurException {
		Dish dish = TestUtils.generateRandomDish();
		Caterer caterer = dish.getCaterer().clone();
		caterer.setId(null);
		Caterer res = TestUtils.createCaterer(caterer, null);
		dish.getCaterer().setId(res.getId());
		Response result = dishService.create(dish, context);
		if (result.getEntity() != null) {
			int statusCode = result.getStatus();
			assertEquals("Wrong status code: " + statusCode + " with " + result.getEntity(), 200, statusCode);
			Dish dishResult = (Dish) result.getEntity();
			assertNotNull(dishResult.getId());
			idsToDelete.add(dishResult.getId());
			idsToDeleteCaterer.add(dishResult.getCaterer().getId());
			String pickupdate = TestUtils.generateRandomCorrectPickupDate(dishResult.getCaterer().getWorkingTimes());
			Response result2 = dishService.search(pickupdate, dish.getType().getType(), 100, null, "832 W. Wrightwood, Chicago", 3000);
			if (result2.getEntity() != null) {
				List<Dish> dishes = (List<Dish>) result2.getEntity();
				for(Dish temp : dishes){
					System.out.println(temp);
				}
				assertNotNull(dishes);
				Assert.assertThat("Failed with pickupdate: " + pickupdate + ", and workingTimes: " + dishResult.getCaterer().getWorkingTimes(), dishes.size(),
						greaterThanOrEqualTo(1));
				Dish dish1 = dishes.get(0);
				assertEquals(dishResult.getName(), dish1.getName());
				assertEquals(dishResult.getPrice(), dish1.getPrice());
			} else {
				fail("List of dish returned is null");
			}
		} else {
			fail("Dish returned is null");
		}
	}
	
	@Test
	public void testSearch3() throws EpickurException, IOException {
		TestUtils.cleanDB();
		Dish dish = TestUtils.generateRandomDish();
		String pickupdate = TestUtils.generateRandomPickupDate();
		Response result = dishService.search(pickupdate, dish.getType().getType(), 100, null, "832 W. Wrightwood, Chicago", 3000);
		if (result.getEntity() != null) {
			try {
				ObjectMapper mapper = new ObjectMapper();
				List<?> jsonResult = mapper.readValue(result.getEntity().toString(), List.class);
				assertNotNull(jsonResult);
				assertEquals(0, jsonResult.size());
			} catch (Exception e) {
				fail(result.getEntity() + " has been returned. We should have had a no content error");
			}
		} else {
			fail("List of dish returned is null");
		}
	}
	
	@Test(expected = EpickurIllegalArgument.class)
	public void testSearchFail() throws EpickurException {
		dishService.search(null, null, null, null, null, null);
	}

	@Test(expected = EpickurIllegalArgument.class)
	public void testSearchFail2() throws EpickurException {
		String pickupdate = TestUtils.generateRandomPickupDate();
		dishService.search(pickupdate, null, 8, null, "", null);
	}

	@Test(expected = EpickurIllegalArgument.class)
	public void testSearchFail3() throws EpickurException {
		String pickupdate = TestUtils.generateRandomPickupDate();
		dishService.search(pickupdate, DishType.FISH.getType(), null, null, null, null);
	}

	@Test(expected = EpickurIllegalArgument.class)
	public void testSearchFail4() throws EpickurException {
		String pickupdate = TestUtils.generateRandomPickupDate();
		dishService.search(pickupdate, DishType.FISH.getType(), 8, null, "", null);
	}

	@Test(expected = EpickurIllegalArgument.class)
	public void testSearchFail5() throws EpickurException {
		String pickupdate = TestUtils.generateRandomPickupDate();
		dishService.search(pickupdate, DishType.FISH.getType(), 0, null, null, null);
	}

	@Test(expected = EpickurIllegalArgument.class)
	public void testSearchFail6() throws EpickurException {
		String pickupdate = TestUtils.generateRandomPickupDate();
		dishService.search(pickupdate, DishType.FISH.getType(), 0, null, "", null);
	}
}
