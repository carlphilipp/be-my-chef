package com.epickur.api.entity;

import com.epickur.api.helper.EntityGenerator;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DishTest {

	@Test
	public void testDish() {
		Dish dish = EntityGenerator.generateRandomDish();
		Dish dish2 = dish.clone();

		assertEquals(dish.hashCode(), dish2.hashCode());
		assertEquals(dish, dish2);
	}
}
