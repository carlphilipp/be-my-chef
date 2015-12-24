package com.epickur.api.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import com.epickur.api.helper.EntityGenerator;

public class NutritionFactTest {

	@Test
	public void testNutritionFact() throws CloneNotSupportedException {
		NutritionFact nutritionFact = EntityGenerator.generateRandomNutritionFact();
		NutritionFact nutritionFact2 = nutritionFact.clone();

		assertEquals(nutritionFact.hashCode(), nutritionFact2.hashCode());
		assertEquals(nutritionFact, nutritionFact2);
	}
}
