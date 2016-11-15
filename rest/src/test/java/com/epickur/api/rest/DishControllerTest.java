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
import com.epickur.api.utils.Utils;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class DishControllerTest {

	@Mock
	private DishService dishService;
	@Mock
	private CatererService catererService;
	@Mock
	private HttpServletRequest context;
	@Mock
	private Utils utils;
	@InjectMocks
	private DishController controller;

	@Before
	public void setUp() {
		Key key = EntityGenerator.generateRandomAdminKey();
		given(context.getAttribute("key")).willReturn(key);
	}

	@Test
	public void testCreate() throws EpickurException {
		// Given
		Dish dish = EntityGenerator.generateRandomDish();
		dish.getCaterer().setId(null);
		Caterer caterer = EntityGenerator.generateRandomCatererWithId();
		dish.setCaterer(caterer);
		Dish dishAfterCreate = EntityGenerator.mockDishAfterCreate(dish);
		given(catererService.read(any())).willReturn(Optional.of(caterer));
		given(dishService.create(any(Dish.class))).willReturn(dishAfterCreate);

		// When
		ResponseEntity<?> actual = controller.create(dish);

		// Then
		assertNotNull(actual);
		assertEquals(200, actual.getStatusCode().value());
		Dish actualDish = (Dish) actual.getBody();
		assertNotNull(actualDish.getId());
	}

	@Test
	public void testRead() throws EpickurException {
		// Given
		Dish dish = EntityGenerator.generateRandomDish();
		dish.getCaterer().setId(null);
		Caterer caterer = EntityGenerator.generateRandomCatererWithId();
		dish.setCaterer(caterer);
		Dish dishAfterCreate = EntityGenerator.mockDishAfterCreate(dish);
		given(dishService.read(any())).willReturn(Optional.of(dishAfterCreate));

		// When
		ResponseEntity actual = controller.read(new ObjectId().toHexString());

		// Then
		assertNotNull(actual);
		assertEquals(200, actual.getStatusCode().value());
		Dish actualDish = (Dish) actual.getBody();
		assertNotNull(actualDish.getId());
	}

	@Test
	public void testReadDishNotFound() throws EpickurException {
		// Given
		Dish dish = EntityGenerator.generateRandomDish();
		dish.getCaterer().setId(null);
		Caterer caterer = EntityGenerator.generateRandomCatererWithId();
		dish.setCaterer(caterer);
		given(dishService.read(any())).willReturn(Optional.empty());

		// When
		ResponseEntity actual = controller.read(new ObjectId().toHexString());

		// Then
		assertNotNull(actual);
		assertEquals(404, actual.getStatusCode().value());
		ErrorMessage error = (ErrorMessage) actual.getBody();
		assertEquals(HttpStatus.NOT_FOUND.value(), error.getError().intValue());
		assertEquals(HttpStatus.NOT_FOUND.getReasonPhrase(), error.getMessage());
	}

	@Test
	public void testUpdate() throws EpickurException {
		// Given
		Dish dish = EntityGenerator.generateRandomDishWithId();
		dish.getCaterer().setId(null);
		Caterer caterer = EntityGenerator.generateRandomCatererWithId();
		dish.setCaterer(caterer);
		Dish dishAfterCreate = EntityGenerator.mockDishAfterCreate(dish);
		dishAfterCreate.setDescription("desc");
		given(dishService.update(any(Dish.class))).willReturn(dishAfterCreate);

		// When
		ResponseEntity<?> actual = controller.update(dish.getId().toHexString(), dish);

		// Then
		assertNotNull(actual);
		assertEquals(200, actual.getStatusCode().value());
		Dish actualDish = (Dish) actual.getBody();
		assertNotNull(actualDish.getId());
		assertEquals("desc", actualDish.getDescription());
	}

	@Test
	public void testDelete() throws EpickurException {
		// Given
		Dish dish = EntityGenerator.generateRandomDishWithId();
		dish.getCaterer().setId(null);
		Caterer caterer = EntityGenerator.generateRandomCatererWithId();
		dish.setCaterer(caterer);
		given(dishService.delete(any())).willReturn(true);

		// When
		ResponseEntity<?> actual = controller.delete(dish.getId().toHexString());

		// Then
		assertNotNull(actual);
		assertEquals(200, actual.getStatusCode().value());
		DeletedMessage actualDeletedMessage = (DeletedMessage) actual.getBody();
		assertNotNull(actualDeletedMessage.getId());
		assertNotNull(actualDeletedMessage.getDeleted());
		assertTrue(actualDeletedMessage.getDeleted());
	}

	@Test
	public void testDeleteFail() throws EpickurException {
		// Given
		Dish dish = EntityGenerator.generateRandomDishWithId();
		dish.getCaterer().setId(null);
		Caterer caterer = EntityGenerator.generateRandomCatererWithId();
		dish.setCaterer(caterer);
		given(dishService.delete(any())).willReturn(false);

		// When
		ResponseEntity<?> actual = controller.delete(dish.getId().toHexString());

		// Then
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
		// Given
		Dish dish = EntityGenerator.generateRandomDish();
		dish.getCaterer().setId(null);
		Caterer caterer = EntityGenerator.generateRandomCatererWithId();
		dish.setCaterer(caterer);
		Dish dishAfterCreate = EntityGenerator.mockDishAfterCreate(dish);
		List<Dish> dishes = new ArrayList<>();
		dishes.add(dishAfterCreate);
		given(dishService.search(any(String.class), any(Integer.class), any(), any(Integer.class), isNull(), any(String.class), any(Integer.class))).willReturn(dishes);

		// When
		ResponseEntity<?> actual = controller
			.search(EntityGenerator.generateRandomPickupDate(),
				EntityGenerator.generateRandomDishType().toString(),
				1,
				"1,1",
				EntityGenerator.generateRandomString(), 5);

		// Then
		assertNotNull(actual);
		assertEquals(200, actual.getStatusCode().value());
		assertNotNull(actual.getBody());
		List<Dish> actualDishes = (List<Dish>) actual.getBody();
		assertNotNull(actualDishes);
		assertEquals(1, actualDishes.size());
	}
}
