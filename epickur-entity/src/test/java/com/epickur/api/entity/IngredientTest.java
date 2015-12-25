package com.epickur.api.entity;

import com.epickur.api.helper.EntityGenerator;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class IngredientTest {

	@Test
	public void testIngredient() {
		Ingredient ingredient = EntityGenerator.generateRandomIngredient();
		Ingredient ingredient2 = ingredient.clone();

		assertEquals(ingredient.hashCode(), ingredient2.hashCode());
		assertEquals(ingredient, ingredient2);
	}
}
