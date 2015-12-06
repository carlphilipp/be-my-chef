package com.epickur.api.rest;

import com.epickur.api.entity.Caterer;
import com.epickur.api.entity.Dish;
import com.epickur.api.entity.Key;
import com.epickur.api.entity.message.DeletedMessage;
import com.epickur.api.entity.message.ErrorMessage;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.helper.EntityGenerator;
import com.epickur.api.service.CatererService;
import com.epickur.api.service.DishService;
import com.epickur.api.validator.DishValidator;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

public class DishControllerTest {

	@Mock
	private DishService dishBusiness;
	@Mock
	private CatererService catererBusiness;
	@Mock
	private HttpServletRequest context;
	@Mock
	private DishValidator validator;
	@InjectMocks
	private DishController controller;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		Key key = EntityGenerator.generateRandomAdminKey();
		when(context.getAttribute("key")).thenReturn(key);
	}

	@Test
	public void testCreate() throws EpickurException {
		Dish dish = EntityGenerator.generateRandomDish();
		dish.getCaterer().setId(null);
		Caterer caterer = EntityGenerator.generateRandomCatererWithId();
		dish.setCaterer(caterer);
		Dish dishAfterCreate = EntityGenerator.mockDishAfterCreate(dish);

		when(catererBusiness.read(anyString())).thenReturn(caterer);
		when(dishBusiness.create(anyObject())).thenReturn(dishAfterCreate);

		ResponseEntity<?> actual = controller.create(dish);
		assertNotNull(actual);
		assertEquals(200, actual.getStatusCode().value());
		Dish actualDish = (Dish) actual.getBody();
		assertNotNull(actualDish.getId());
	}

	@Test
	public void testRead() throws EpickurException {
		Dish dish = EntityGenerator.generateRandomDish();
		dish.getCaterer().setId(null);
		Caterer caterer = EntityGenerator.generateRandomCatererWithId();
		dish.setCaterer(caterer);
		Dish dishAfterCreate = EntityGenerator.mockDishAfterCreate(dish);

		when(dishBusiness.read(anyString())).thenReturn(dishAfterCreate);

		ResponseEntity actual = controller.read(new ObjectId().toHexString());
		assertNotNull(actual);
		assertEquals(200, actual.getStatusCode().value());
		Dish actualDish = (Dish) actual.getBody();
		assertNotNull(actualDish.getId());
	}

	@Test
	public void testReadDishNotFound() throws EpickurException {
		Dish dish = EntityGenerator.generateRandomDish();
		dish.getCaterer().setId(null);
		Caterer caterer = EntityGenerator.generateRandomCatererWithId();
		dish.setCaterer(caterer);

		when(dishBusiness.read(anyString())).thenReturn(null);

		ResponseEntity actual = controller.read(new ObjectId().toHexString());
		assertNotNull(actual);
		assertEquals(404, actual.getStatusCode().value());
		ErrorMessage error = (ErrorMessage) actual.getBody();
		assertEquals(HttpStatus.NOT_FOUND.value(), error.getError().intValue());
		assertEquals(HttpStatus.NOT_FOUND.getReasonPhrase(), error.getMessage());
	}

	@Test
	public void testUpdate() throws EpickurException {
		Dish dish = EntityGenerator.generateRandomDishWithId();
		dish.getCaterer().setId(null);
		Caterer caterer = EntityGenerator.generateRandomCatererWithId();
		dish.setCaterer(caterer);
		Dish dishAfterCreate = EntityGenerator.mockDishAfterCreate(dish);
		dishAfterCreate.setDescription("desc");

		when(dishBusiness.update(anyObject())).thenReturn(dishAfterCreate);

		ResponseEntity<?> actual = controller.update(dish.getId().toHexString(), dish);
		assertNotNull(actual);
		assertEquals(200, actual.getStatusCode().value());
		Dish actualDish = (Dish) actual.getBody();
		assertNotNull(actualDish.getId());
		assertEquals("desc", actualDish.getDescription());
	}

	@Test
	public void testDelete() throws EpickurException {
		Dish dish = EntityGenerator.generateRandomDishWithId();
		dish.getCaterer().setId(null);
		Caterer caterer = EntityGenerator.generateRandomCatererWithId();
		dish.setCaterer(caterer);

		when(dishBusiness.delete(anyString())).thenReturn(true);

		ResponseEntity<?> actual = controller.delete(dish.getId().toHexString());
		assertNotNull(actual);
		assertEquals(200, actual.getStatusCode().value());
		DeletedMessage actualDeletedMessage = (DeletedMessage) actual.getBody();
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

		when(dishBusiness.delete(anyString())).thenReturn(false);

		ResponseEntity<?> actual = controller.delete(dish.getId().toHexString());
		assertNotNull(actual);
		assertEquals(404, actual.getStatusCode().value());
		ErrorMessage error = (ErrorMessage) actual.getBody();
		assertEquals(HttpStatus.NOT_FOUND.value(), error.getError().intValue());
		assertEquals(HttpStatus.NOT_FOUND.getReasonPhrase(), error.getMessage());
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

		when(dishBusiness.search(anyString(), anyInt(), anyObject(), anyInt(), anyObject(), anyString(), anyInt()))
				.thenReturn(dishes);

		ResponseEntity<?> actual = controller
				.search(EntityGenerator.generateRandomPickupDate(), EntityGenerator.generateRandomDishType().toString(), 1,
						"1,1",
						EntityGenerator.generateRandomString(), 5);
		assertNotNull(actual);
		assertEquals(200, actual.getStatusCode().value());
		assertNotNull(actual.getBody());
		List<Dish> actualDishes = (List<Dish>) actual.getBody();
		assertNotNull(actualDishes);
		assertEquals(1, actualDishes.size());
	}
}
