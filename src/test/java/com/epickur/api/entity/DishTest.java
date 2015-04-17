package com.epickur.api.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import com.epickur.api.TestUtils;

public class DishTest {

	@Test
	public void testDish() {
		Dish dish = TestUtils.generateRandomDish();
		Dish dish2 = dish.clone();

		assertEquals(dish.hashCode(), dish2.hashCode());
		assertEquals(dish, dish2);

		Dish dish3 = dish2;
		assertEquals(dish, dish3);
		assertFalse(dish.equals(null));
		assertFalse(dish.equals(new User()));
	}
}
