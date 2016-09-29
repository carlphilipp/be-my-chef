package com.epickur.api.entity;

import com.epickur.api.helper.EntityGenerator;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NutritionFactTest {

	@Test
	public void testNutritionFact() throws CloneNotSupportedException {
		NutritionFact nutritionFact = EntityGenerator.generateRandomNutritionFact();
		NutritionFact nutritionFact2 = nutritionFact.clone();

		assertEquals(nutritionFact.hashCode(), nutritionFact2.hashCode());
		assertEquals(nutritionFact, nutritionFact2);
	}
}
