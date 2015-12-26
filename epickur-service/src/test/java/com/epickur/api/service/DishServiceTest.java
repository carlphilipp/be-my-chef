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
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@PowerMockIgnore("javax.management.*")
@RunWith(org.powermock.modules.junit4.PowerMockRunner.class)
@PrepareForTest(DishService.class)
public class DishServiceTest {

	@Mock
	private DishDAO dishDAOMock;
	@Mock
	private GeocoderHereImpl geoCoder;
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
		Dish dish = EntityGenerator.generateRandomDish();
		Dish dishAfterCreate = EntityGenerator.mockDishAfterCreate(dish);

		when(dishDAOMock.create(anyObject())).thenReturn(dishAfterCreate);

		Dish actual = dishService.create(dish);
		assertNotNull("Dish is null", actual);
		assertNotNull("Id not generated", actual.getId());
		assertNotNull("CreatedAt is null", actual.getCreatedAt());
		assertNotNull("UpdatedAt is null", actual.getUpdatedAt());
		assertEquals(dish.getName(), actual.getName());
	}

	@Test
	public void testRead() throws EpickurException {
		Dish dish = EntityGenerator.generateRandomDishWithId();
		Dish dishAfterRead = EntityGenerator.mockDishAfterCreate(dish);

		when(dishDAOMock.read(anyString())).thenReturn(dishAfterRead);

		Dish actual = dishService.read(dish.getId().toHexString());
		assertNotNull("Dish is null", actual);
	}

	@Test
	public void testReadAll() throws EpickurException {
		Dish dish = EntityGenerator.generateRandomDishWithId();
		Dish dishAfterRead = EntityGenerator.mockDishAfterCreate(dish);
		List<Dish> listDishes = new ArrayList<>();
		listDishes.add(dishAfterRead);

		when(dishDAOMock.readAll()).thenReturn(listDishes);

		List<Dish> listActual = dishService.readAll();
		Dish actual = listActual.get(0);
		assertNotNull("Dish is null", actual);
	}

	@Test
	public void testUpdate() throws EpickurException {
		Dish dish = EntityGenerator.generateRandomDishWithId();
		Dish dishAfterRead = EntityGenerator.mockDishAfterCreate(dish);
		Dish dishAfterUpdate = EntityGenerator.mockDishAfterCreate(dish);
		dishAfterUpdate.setName("new name");

		when(dishDAOMock.read(anyString())).thenReturn(dishAfterRead);
		when(dishDAOMock.update(anyObject())).thenReturn(dishAfterUpdate);

		Dish actual = dishService.update(dish);
		assertNotNull("Dish is null", actual);
		assertEquals("new name", actual.getName());
	}

	@Test
	public void testDelete() throws EpickurException {
		Dish dish = EntityGenerator.generateRandomDishWithId();
		Dish dishAfterRead = EntityGenerator.mockDishAfterCreate(dish);

		when(dishDAOMock.read(anyString())).thenReturn(dishAfterRead);
		when(dishDAOMock.delete(dish.getId().toHexString())).thenReturn(true);

		boolean actual = dishService.delete(dish.getId().toHexString());
		assertTrue(actual);
	}

	@Test
	public void testSearchDishesForOneCaterer() throws EpickurException {
		Dish dish = EntityGenerator.generateRandomDishWithId();
		Dish dishAfterRead = EntityGenerator.mockDishAfterCreate(dish);
		List<Dish> listDishes = new ArrayList<>();
		listDishes.add(dishAfterRead);

		when(dishDAOMock.searchWithCatererId(anyString())).thenReturn(listDishes);

		List<Dish> listActual = dishService.searchDishesForOneCaterer(anyString());
		Dish actual = listActual.get(0);
		assertNotNull("Dish is null", actual);
	}

	@Test
	public void testSearch() throws Exception {
		Dish dish = EntityGenerator.generateRandomDishWithId();
		Dish dishAfterRead = EntityGenerator.mockDishAfterCreate(dish);
		List<Dish> listDishes = new ArrayList<>();
		listDishes.add(dishAfterRead);

		when(dishDAOMock.search(anyString(), anyInt(), anyObject(), anyInt(), anyObject(), anyInt()))
				.thenReturn(listDishes);
		whenNew(GeocoderHereImpl.class).withNoArguments().thenReturn(geoCoder);
		when(geoCoder.getPosition(anyString())).thenReturn(geo);

		List<Dish> listActual = dishService.search("", 0, new ArrayList<>(), 0, new Geo(), "", 0);
		Dish actual = listActual.get(0);
		assertNotNull("Dish is null", actual);
	}
}
