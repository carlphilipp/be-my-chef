package com.epickur.api.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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

public class SearchServiceTest {
	
	// TODO add more tests

	private static SearchService searchService;
	private static DishService dishService;
	private static List<ObjectId> idsToDelete;
	private static ContainerRequestContext context;

	@BeforeClass
	public static void beforeClass() {
		searchService = new SearchService();
		dishService = new DishService();
		idsToDelete = new ArrayList<ObjectId>();
		context = mock(ContainerRequestContext.class);
		Key key = TestUtils.generateRandomKey();
		Mockito.when(context.getProperty("key")).thenReturn(key);
	}

	@AfterClass
	public static void afterClass() throws EpickurException {
		for (ObjectId id : idsToDelete) {
			dishService.delete(id.toHexString(), context);
		}
	}

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

			Response result2 = searchService.search(dish.getType(), 100, "832 W. Wrightwood, Chicago", 3000);
			if (result2.getEntity() != null) {
				List<Dish> dishes = (List<Dish>) result2.getEntity();
				assertNotNull(dishes);
				assertEquals(1, dishes.size());
				Dish dish1 = dishes.get(0);
				assertEquals(dishResult.getName(), dish1.getName());
				assertEquals(dishResult.getPrice(), dish1.getPrice());
			}else{
				fail("List of dish returned is null");
			}
		} else {
			fail("Dish returned is null");
		}

	}

	@Test(expected = EpickurIllegalArgument.class)
	public void testSearchFail() throws EpickurException {
		searchService.search(null, null, null, null);
	}
	
	@Test(expected = EpickurIllegalArgument.class)
	public void testSearchFail2() throws EpickurException {
		searchService.search("", null, null, null);
	}

	@Test(expected = EpickurIllegalArgument.class)
	public void testSearchFail3() throws EpickurException {
		searchService.search("type", null, null, null);
	}
	
	@Test(expected = EpickurIllegalArgument.class)
	public void testSearchFail4() throws EpickurException {
		searchService.search("type", 8, "", null);
	}

	@Test(expected = EpickurIllegalArgument.class)
	public void testSearchFail5() throws EpickurException {
		searchService.search("type", 0, null, null);
	}
	
	@Test(expected = EpickurIllegalArgument.class)
	public void testSearchFail6() throws EpickurException {
		searchService.search("type", 0, "", null);
	}
}
