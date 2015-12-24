package com.epickur.api.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import com.epickur.api.helper.EntityGenerator;

public class DishTest {

	@Test
	public void testDish() {
		Dish dish = EntityGenerator.generateRandomDish();
		Dish dish2 = dish.clone();

		assertEquals(dish.hashCode(), dish2.hashCode());
		assertEquals(dish, dish2);
	}
}
