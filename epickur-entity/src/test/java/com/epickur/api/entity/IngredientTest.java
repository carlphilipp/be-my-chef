package com.epickur.api.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import com.epickur.api.helper.EntityGenerator;

public class IngredientTest {

	@Test
	public void testIngredient() {
		Ingredient ingredient = EntityGenerator.generateRandomIngredient();
		Ingredient ingredient2 = ingredient.clone();

		assertEquals(ingredient.hashCode(), ingredient2.hashCode());
		assertEquals(ingredient, ingredient2);
	}
}
