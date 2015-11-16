package com.epickur.api.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import com.epickur.api.helper.EntityGenerator;

public class NutritionFactTest {

	@Test
	public void testNutritionFact() {
		NutritionFact nutritionFact = EntityGenerator.generateRandomNutritionFact();
		NutritionFact nutritionFact2 = nutritionFact.clone();

		assertEquals(nutritionFact.hashCode(), nutritionFact2.hashCode());
		assertEquals(nutritionFact, nutritionFact2);

		NutritionFact key3 = nutritionFact2;
		assertEquals(nutritionFact, key3);
		assertFalse(nutritionFact.equals(null));
		assertFalse(nutritionFact.equals(new User()));
	}
}
