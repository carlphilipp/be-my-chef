package com.epickur.api.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;

import com.epickur.api.dao.mongo.DishDAO;
import com.epickur.api.entity.Dish;
import com.epickur.api.entity.Geo;
import com.epickur.api.entity.Key;
import com.epickur.api.enumeration.DishType;
import com.epickur.api.enumeration.Role;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.geocoder.here.GeocoderHereImpl;
import com.epickur.api.helper.EntityGenerator;

@PowerMockIgnore("javax.management.*")
@RunWith(org.powermock.modules.junit4.PowerMockRunner.class)
@PrepareForTest(DishBusiness.class)
public class DishBusinessTest {

	@Mock
	private DishDAO dishDAOMock;
	@Mock
	private GeocoderHereImpl geoCoder;
	@Mock
	private Geo geo;
	@InjectMocks
	private DishBusiness dishBusiness;

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

		when(dishDAOMock.create((Dish) anyObject())).thenReturn(dishAfterCreate);

		Dish actual = dishBusiness.create(dish);
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

		Dish actual = dishBusiness.read(dish.getId().toHexString());
		assertNotNull("Dish is null", actual);
	}

	@Test
	public void testReadAll() throws EpickurException {
		Dish dish = EntityGenerator.generateRandomDishWithId();
		Dish dishAfterRead = EntityGenerator.mockDishAfterCreate(dish);
		List<Dish> listDishes = new ArrayList<Dish>();
		listDishes.add(dishAfterRead);

		when(dishDAOMock.readAll()).thenReturn(listDishes);

		List<Dish> listActual = dishBusiness.readAll();
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
		when(dishDAOMock.update((Dish) anyObject())).thenReturn(dishAfterUpdate);

		Dish actual = dishBusiness.update(dish, key);
		assertNotNull("Dish is null", actual);
		assertEquals("new name", actual.getName());
	}

	@Test
	public void testDelete() throws EpickurException {
		Dish dish = EntityGenerator.generateRandomDishWithId();
		Dish dishAfterRead = EntityGenerator.mockDishAfterCreate(dish);

		when(dishDAOMock.read(anyString())).thenReturn(dishAfterRead);
		when(dishDAOMock.delete(dish.getId().toHexString())).thenReturn(true);

		boolean actual = dishBusiness.delete(dish.getId().toHexString(), key);
		assertTrue(actual);
	}

	@Test
	public void testSearchDishesForOneCaterer() throws EpickurException {
		Dish dish = EntityGenerator.generateRandomDishWithId();
		Dish dishAfterRead = EntityGenerator.mockDishAfterCreate(dish);
		List<Dish> listDishes = new ArrayList<Dish>();
		listDishes.add(dishAfterRead);

		when(dishDAOMock.searchWithCatererId(anyString())).thenReturn(listDishes);

		List<Dish> listActual = dishBusiness.searchDishesForOneCaterer(anyString());
		Dish actual = listActual.get(0);
		assertNotNull("Dish is null", actual);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSearch() throws Exception {
		Dish dish = EntityGenerator.generateRandomDishWithId();
		Dish dishAfterRead = EntityGenerator.mockDishAfterCreate(dish);
		List<Dish> listDishes = new ArrayList<Dish>();
		listDishes.add(dishAfterRead);

		when(dishDAOMock.search(anyString(), anyInt(), (List<DishType>) anyObject(), anyInt(), (Geo) anyObject(), anyInt()))
				.thenReturn(listDishes);
		whenNew(GeocoderHereImpl.class).withNoArguments().thenReturn(geoCoder);
		when(geoCoder.getPosition(anyString())).thenReturn(geo);

		List<Dish> listActual = dishBusiness.search("", 0, (List<DishType>)new ArrayList<DishType>(), 0, new Geo(),"", 0);
		Dish actual = listActual.get(0);
		assertNotNull("Dish is null", actual);
	}
}
