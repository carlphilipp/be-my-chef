package com.epickur.api.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Response;

import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.epickur.api.entity.Caterer;
import com.epickur.api.entity.Dish;
import com.epickur.api.entity.Geo;
import com.epickur.api.entity.Key;
import com.epickur.api.entity.message.DeletedMessage;
import com.epickur.api.entity.message.ErrorMessage;
import com.epickur.api.enumeration.DishType;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.helper.EntityGenerator;
import com.epickur.api.service.CatererService;
import com.epickur.api.service.DishService;

public class DishControllerTest {

	@Mock
	private DishService dishBusiness;
	@Mock
	private CatererService catererBusiness;
	@Mock
	private ContainerRequestContext context;
	@InjectMocks
	private DishController controller;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		Key key = EntityGenerator.generateRandomAdminKey();
		when(context.getProperty("key")).thenReturn(key);
	}

	@Test
	public void testCreate() throws EpickurException {
		Dish dish = EntityGenerator.generateRandomDish();
		dish.getCaterer().setId(null);
		Caterer caterer = EntityGenerator.generateRandomCatererWithId();
		dish.setCaterer(caterer);
		Dish dishAfterCreate = EntityGenerator.mockDishAfterCreate(dish);

		when(catererBusiness.read(anyString())).thenReturn(caterer);
		when(dishBusiness.create((Dish) anyObject())).thenReturn(dishAfterCreate);

		Response actual = controller.create(dish);
		assertNotNull(actual);
		assertEquals(200, actual.getStatus());
		Dish actualDish = (Dish) actual.getEntity();
		assertNotNull(actualDish.getId());
	}

	@Test
	public void testCreateCatererNotFound() throws EpickurException {
		Dish dish = EntityGenerator.generateRandomDish();
		dish.getCaterer().setId(null);
		Caterer caterer = EntityGenerator.generateRandomCatererWithId();
		dish.setCaterer(caterer);

		when(catererBusiness.read(anyString())).thenReturn(null);

		Response actual = controller.create(dish);
		assertNotNull(actual);
		assertEquals(404, actual.getStatus());
		ErrorMessage error = (ErrorMessage) actual.getEntity();
		assertEquals(Response.Status.NOT_FOUND.getStatusCode(), error.getError().intValue());
		assertEquals(Response.Status.NOT_FOUND.getReasonPhrase(), error.getMessage());
	}

	@Test
	public void testRead() throws EpickurException {
		Dish dish = EntityGenerator.generateRandomDish();
		dish.getCaterer().setId(null);
		Caterer caterer = EntityGenerator.generateRandomCatererWithId();
		dish.setCaterer(caterer);
		Dish dishAfterCreate = EntityGenerator.mockDishAfterCreate(dish);

		when(dishBusiness.read(anyString())).thenReturn(dishAfterCreate);

		Response actual = controller.read(new ObjectId().toHexString());
		assertNotNull(actual);
		assertEquals(200, actual.getStatus());
		Dish actualDish = (Dish) actual.getEntity();
		assertNotNull(actualDish.getId());
	}

	@Test
	public void testReadDishNotFound() throws EpickurException {
		Dish dish = EntityGenerator.generateRandomDish();
		dish.getCaterer().setId(null);
		Caterer caterer = EntityGenerator.generateRandomCatererWithId();
		dish.setCaterer(caterer);

		when(dishBusiness.read(anyString())).thenReturn(null);

		Response actual = controller.read(new ObjectId().toHexString());
		assertNotNull(actual);
		assertEquals(404, actual.getStatus());
		ErrorMessage error = (ErrorMessage) actual.getEntity();
		assertEquals(Response.Status.NOT_FOUND.getStatusCode(), error.getError().intValue());
		assertEquals(Response.Status.NOT_FOUND.getReasonPhrase(), error.getMessage());
	}

	@Test
	public void testUpdate() throws EpickurException {
		Dish dish = EntityGenerator.generateRandomDishWithId();
		dish.getCaterer().setId(null);
		Caterer caterer = EntityGenerator.generateRandomCatererWithId();
		dish.setCaterer(caterer);
		Dish dishAfterCreate = EntityGenerator.mockDishAfterCreate(dish);
		dishAfterCreate.setDescription("desc");

		when(dishBusiness.update((Dish) anyObject(), (Key) anyObject())).thenReturn(dishAfterCreate);

		Response actual = controller.update(dish.getId().toHexString(), dish);
		assertNotNull(actual);
		assertEquals(200, actual.getStatus());
		Dish actualDish = (Dish) actual.getEntity();
		assertNotNull(actualDish.getId());
		assertEquals("desc", actualDish.getDescription());
	}

	@Test
	public void testUpdateDishNotFound() throws EpickurException {
		Dish dish = EntityGenerator.generateRandomDishWithId();
		dish.getCaterer().setId(null);
		Caterer caterer = EntityGenerator.generateRandomCatererWithId();
		dish.setCaterer(caterer);
		Dish dishAfterCreate = EntityGenerator.mockDishAfterCreate(dish);
		dishAfterCreate.setDescription("desc");

		when(dishBusiness.update((Dish) anyObject(), (Key) anyObject())).thenReturn(null);

		Response actual = controller.update(dish.getId().toHexString(), dish);
		assertNotNull(actual);
		assertEquals(404, actual.getStatus());
		ErrorMessage error = (ErrorMessage) actual.getEntity();
		assertEquals(Response.Status.NOT_FOUND.getStatusCode(), error.getError().intValue());
		assertEquals(Response.Status.NOT_FOUND.getReasonPhrase(), error.getMessage());
	}

	@Test
	public void testDelete() throws EpickurException {
		Dish dish = EntityGenerator.generateRandomDishWithId();
		dish.getCaterer().setId(null);
		Caterer caterer = EntityGenerator.generateRandomCatererWithId();
		dish.setCaterer(caterer);

		when(dishBusiness.delete(anyString(), (Key) anyObject())).thenReturn(true);

		Response actual = controller.delete(dish.getId().toHexString());
		assertNotNull(actual);
		assertEquals(200, actual.getStatus());
		DeletedMessage actualDeletedMessage = (DeletedMessage) actual.getEntity();
		assertNotNull(actualDeletedMessage.getId());
		assertNotNull(actualDeletedMessage.getDeleted());
		assertTrue(actualDeletedMessage.getDeleted());
	}

	@Test
	public void testDeleteFail() throws EpickurException {
		Dish dish = EntityGenerator.generateRandomDishWithId();
		dish.getCaterer().setId(null);
		Caterer caterer = EntityGenerator.generateRandomCatererWithId();
		dish.setCaterer(caterer);

		when(dishBusiness.delete(anyString(), (Key) anyObject())).thenReturn(false);

		Response actual = controller.delete(dish.getId().toHexString());
		assertNotNull(actual);
		assertEquals(404, actual.getStatus());
		ErrorMessage error = (ErrorMessage) actual.getEntity();
		assertEquals(Response.Status.NOT_FOUND.getStatusCode(), error.getError().intValue());
		assertEquals(Response.Status.NOT_FOUND.getReasonPhrase(), error.getMessage());
	}

	// Search dish
	@SuppressWarnings("unchecked")
	@Test
	public void testSearch() throws EpickurException {
		Dish dish = EntityGenerator.generateRandomDish();
		dish.getCaterer().setId(null);
		Caterer caterer = EntityGenerator.generateRandomCatererWithId();
		dish.setCaterer(caterer);
		Dish dishAfterCreate = EntityGenerator.mockDishAfterCreate(dish);
		List<Dish> dishes = new ArrayList<>();
		dishes.add(dishAfterCreate);

		when(dishBusiness.search(anyString(), anyInt(), (List<DishType>) anyObject(), anyInt(), (Geo) anyObject(), anyString(), anyInt()))
				.thenReturn(dishes);

		Response actual = controller.search(EntityGenerator.generateRandomPickupDate(), EntityGenerator.generateRandomDishType().toString(), 1,
				"1,1",
				EntityGenerator.generateRandomString(), 5);
		assertNotNull(actual);
		assertEquals(200, actual.getStatus());
		assertNotNull(actual.getEntity());
		List<Dish> actualDishes = (List<Dish>) actual.getEntity();
		assertNotNull(actualDishes);
		assertEquals(1, actualDishes.size());
	}
}
