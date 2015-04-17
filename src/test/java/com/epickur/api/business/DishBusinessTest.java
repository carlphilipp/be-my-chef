package com.epickur.api.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.epickur.api.entity.Dish;
import com.epickur.api.entity.Key;
import com.epickur.api.enumeration.Role;
import com.epickur.api.exception.EpickurException;

public class DishBusinessTest {

	private static DishBusiness business;
	private static List<ObjectId> idsToDelete;
	private static final String name = "Dish Name";
	private static final String description = "description";
	private static Key key;

	@BeforeClass
	public static void beforeClass() {
		business = new DishBusiness();
		idsToDelete = new ArrayList<ObjectId>();
		key = new Key();
		key.setRole(Role.ADMIN);
	}

	@AfterClass
	public static void afterClass() throws EpickurException {
		for (ObjectId id : idsToDelete) {
			business.delete(id.toHexString(), key);
		}
	}

	@Test
	public void testCreate() throws EpickurException {
		// Create a new Dish
		Dish dish = new Dish();
		dish.setName(name);

		Dish result = business.create(dish);
		assertNotNull("Caterer is null", result);
		assertNotNull("Id not generated", result.getId());
		idsToDelete.add(result.getId());
		assertNotNull("CreatedAt is null", result.getCreatedAt());
		assertNotNull("UpdatedAt is null", result.getUpdatedAt());
		assertEquals(name, result.getName());
	}

	@Test
	public void testRead() throws EpickurException {
		// Create a new Dish
		Dish dish = new Dish();
		dish.setName(name + "2");

		Dish result = business.create(dish);
		assertNotNull("Dish is null", result);
		assertNotNull("Id not generated", result.getId());
		idsToDelete.add(result.getId());
		assertNotNull("CreatedAt is null", result.getCreatedAt());
		assertNotNull("UpdatedAt is null", result.getUpdatedAt());
		assertEquals(name + "2", result.getName());

		Dish result2 = business.read(result.getId().toHexString());
		assertNotNull("Dish is null", result2);
		assertEquals(result, result2);
	}

	@Test
	public void testReadAll() throws EpickurException {
		// Create a new Dish
		Dish dish = new Dish();
		dish.setName(name + "5");

		Dish result = business.create(dish);
		assertNotNull("Dish is null", result);
		assertNotNull("Id not generated", result.getId());
		idsToDelete.add(result.getId());
		assertNotNull("CreatedAt is null", result.getCreatedAt());
		assertNotNull("UpdatedAt is null", result.getUpdatedAt());
		assertEquals(name + "5", result.getName());

		List<Dish> result2 = business.readAll();
		assertNotNull("Caterer is null", result2);
		for (Dish di : result2) {
			if (di.getId().equals(result.getId())) {
				assertEquals(result, di);
			}
		}
	}

	@Test
	public void testUpdate() throws EpickurException {
		// Create a new Dish
		Dish dish = new Dish();
		dish.setName(name + "3");
		dish.setDescription(description);

		Dish result = business.create(dish);
		assertNotNull("Dish is null", result);
		assertNotNull("Id not generated", result.getId());
		idsToDelete.add(result.getId());
		assertNotNull("CreatedAt is null", result.getCreatedAt());
		assertNotNull("UpdatedAt is null", result.getUpdatedAt());
		assertEquals(name + "3", result.getName());

		result.setDescription(description + "3");
		Dish result2 = business.update(result, key);
		assertNotNull("Dish is null", result2);
		result.setCreatedAt(result2.getCreatedAt());
		assertEquals(result, result2);
	}

	@Test
	public void testDelete() throws EpickurException {
		// Create a new Dish
		Dish dish = new Dish();
		dish.setName(name + "4");

		Dish result = business.create(dish);
		assertNotNull("Dish is null", result);
		assertNotNull("Id not generated", result.getId());
		idsToDelete.add(result.getId());

		boolean deleted = business.delete(result.getId().toHexString(), key);
		assertTrue(deleted);

		Dish result2 = business.read(result.getId().toHexString());
		assertNull(result2);
	}
}
