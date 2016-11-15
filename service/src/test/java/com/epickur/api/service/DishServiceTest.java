package com.epickur.api.service;

import com.epickur.api.dao.mongo.DishDAO;
import com.epickur.api.entity.Dish;
import com.epickur.api.entity.Geo;
import com.epickur.api.entity.Key;
import com.epickur.api.enumeration.Role;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.helper.EntityGenerator;
import com.epickur.api.here.GeocoderHereImpl;
import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.isA;

@RunWith(MockitoJUnitRunner.class)
public class DishServiceTest {

	@Mock
	private DishDAO dishDAO;
	@Mock
	private GeocoderHereImpl geocoderHere;
	@Mock
	private Geo geo;
	@InjectMocks
	private DishService dishService;
	private Key key;

	@Before
	public void setUp() {
		key = new Key();
		key.setRole(Role.ADMIN);
		key.setUserId(new ObjectId());
	}

	@After
	public void tearDown() throws Exception {
		key = null;
	}

	@Test
	public void testCreate() throws EpickurException {
		// Given
		Dish dish = EntityGenerator.generateRandomDish();
		Dish dishAfterCreate = EntityGenerator.mockDishAfterCreate(dish);
		given(dishDAO.create(isA(Dish.class))).willReturn(dishAfterCreate);

		// When
		Dish actual = dishService.create(dish);

		// Then
		assertNotNull("Dish is null", actual);
		assertNotNull("Id not generated", actual.getId());
		assertNotNull("CreatedAt is null", actual.getCreatedAt());
		assertNotNull("UpdatedAt is null", actual.getUpdatedAt());
		assertEquals(dish.getName(), actual.getName());
	}

	@Test
	public void testRead() throws EpickurException {
		// Given
		Dish dish = EntityGenerator.generateRandomDishWithId();
		Dish dishAfterRead = EntityGenerator.mockDishAfterCreate(dish);
		given(dishDAO.read(anyString())).willReturn(Optional.of(dishAfterRead));

		// When
		Optional<Dish> actual = dishService.read(dish.getId().toHexString());

		// Then
		assertTrue(actual.isPresent());
	}

	@Test
	public void testReadAll() throws EpickurException {
		// Given
		Dish dish = EntityGenerator.generateRandomDishWithId();
		Dish dishAfterRead = EntityGenerator.mockDishAfterCreate(dish);
		List<Dish> listDishes = new ArrayList<>();
		listDishes.add(dishAfterRead);
		given(dishDAO.readAll()).willReturn(listDishes);

		// When
		List<Dish> listActual = dishService.readAll();

		// Then
		Dish actual = listActual.get(0);
		assertNotNull("Dish is null", actual);
	}

	@Test
	public void testUpdate() throws EpickurException {
		// Given
		Dish dish = EntityGenerator.generateRandomDishWithId();
		Dish dishAfterRead = EntityGenerator.mockDishAfterCreate(dish);
		Dish dishAfterUpdate = EntityGenerator.mockDishAfterCreate(dish);
		dishAfterUpdate.setName("new name");
		given(dishDAO.read(anyString())).willReturn(Optional.of(dishAfterRead));
		given(dishDAO.update(isA(Dish.class))).willReturn(dishAfterUpdate);

		// When
		Dish actual = dishService.update(dish);

		// Then
		assertNotNull("Dish is null", actual);
		assertEquals("new name", actual.getName());
	}

	@Test
	public void testDelete() throws EpickurException {
		// Given
		Dish dish = EntityGenerator.generateRandomDishWithId();
		Dish dishAfterRead = EntityGenerator.mockDishAfterCreate(dish);
		given(dishDAO.read(anyString())).willReturn(Optional.of(dishAfterRead));
		given(dishDAO.delete(dish.getId().toHexString())).willReturn(true);

		// When
		boolean actual = dishService.delete(dish.getId().toHexString());

		// Then
		assertTrue(actual);
	}

	@Test
	public void testSearchDishesForOneCaterer() throws EpickurException {
		// Given
		Dish dish = EntityGenerator.generateRandomDishWithId();
		Dish dishAfterRead = EntityGenerator.mockDishAfterCreate(dish);
		List<Dish> listDishes = new ArrayList<>();
		listDishes.add(dishAfterRead);
		given(dishDAO.searchWithCatererId(anyString())).willReturn(listDishes);

		// When
		List<Dish> listActual = dishService.searchDishesForOneCaterer(UUID.randomUUID().toString());

		// Then
		Dish actual = listActual.get(0);
		assertNotNull("Dish is null", actual);
	}

	@Test
	public void testSearch() throws Exception {
		// Given
		Dish dish = EntityGenerator.generateRandomDishWithId();
		Dish dishAfterRead = EntityGenerator.mockDishAfterCreate(dish);
		List<Dish> listDishes = new ArrayList<>();
		listDishes.add(dishAfterRead);
		given(dishDAO.search(anyString(), anyInt(), isA(List.class), anyInt(), isA(Geo.class), anyInt())).willReturn(listDishes);
		//givenNew(GeocoderHereImpl.class).withNoArguments().willReturn(geocoderHere);
		given(geocoderHere.getPosition(anyString())).willReturn(geo);

		// When
		List<Dish> listActual = dishService.search("", 0, new ArrayList<>(), 0, new Geo(), "", 0);

		// Then
		Dish actual = listActual.get(0);
		assertNotNull("Dish is null", actual);
	}
}
