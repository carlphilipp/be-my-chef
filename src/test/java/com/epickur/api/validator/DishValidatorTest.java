package com.epickur.api.validator;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.epickur.api.TestUtils;
import com.epickur.api.entity.Dish;
import com.epickur.api.entity.Ingredient;
import com.epickur.api.entity.Key;
import com.epickur.api.enumeration.Crud;
import com.epickur.api.enumeration.Role;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.exception.EpickurForbiddenException;
import com.epickur.api.exception.EpickurIllegalArgument;

public class DishValidatorTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testCheckCreateDish() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field dish.name is not allowed to be null or empty");

		DishValidator validator = new DishValidator();
		Dish dish = TestUtils.generateRandomDish();
		dish.setName(null);
		validator.checkCreateData(dish);
	}

	@Test
	public void testCheckCreateDish2() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field dish.description is not allowed to be null or empty");

		DishValidator validator = new DishValidator();
		Dish dish = TestUtils.generateRandomDish();
		dish.setDescription(null);
		validator.checkCreateData(dish);
	}

	@Test
	public void testCheckUpdateDish() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field dish.id is not allowed to be null or empty");

		DishValidator validator = new DishValidator();
		Dish dish = TestUtils.generateRandomDish();
		dish.setId(null);
		validator.checkUpdateData("id", dish);
	}

	@Test
	public void testCheckUpdateDish2() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage(Validator.NO_DISH_PROVIDED);

		DishValidator validator = new DishValidator();
		Dish dish = null;
		validator.checkUpdateData("id", dish);
	}

	@Test
	public void testData() {
		DishValidator validator = new DishValidator();
		Dish dish = TestUtils.generateRandomDish();
		validator.checkData(dish);
	}

	@Test
	public void testData2() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field dish.type is not allowed to be null or empty");

		DishValidator validator = new DishValidator();
		Dish dish = TestUtils.generateRandomDish();
		dish.setType(null);
		validator.checkData(dish);
	}

	@Test
	public void testData3() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field dish.price is not allowed to be null or empty");

		DishValidator validator = new DishValidator();
		Dish dish = TestUtils.generateRandomDish();
		dish.setPrice(null);
		validator.checkData(dish);
	}

	@Test
	public void testData4() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field dish.cookingTime is not allowed to be null or empty");

		DishValidator validator = new DishValidator();
		Dish dish = TestUtils.generateRandomDish();
		dish.setCookingTime(null);
		validator.checkData(dish);
	}

	@Test
	public void testData5() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field dish.difficultyLevel is not allowed to be null or empty");

		DishValidator validator = new DishValidator();
		Dish dish = TestUtils.generateRandomDish();
		dish.setDifficultyLevel(null);
		validator.checkData(dish);
	}

	/*
	 * @Test public void testData6() { thrown.expect(EpickurIllegalArgument.class);
	 * thrown.expectMessage("The field dish.videoUrl is not allowed to be null or empty");
	 * 
	 * DishValidator validator = new DishValidator(); Dish dish = TestUtils.generateRandomDish(); dish.setVideoUrl(null); validator.checkData(dish); }
	 */

	/*
	 * @Test public void testData7() { thrown.expect(EpickurIllegalArgument.class);
	 * thrown.expectMessage("The field dish.nutritionFacts is not allowed to be null or empty");
	 * 
	 * DishValidator validator = new DishValidator(); Dish dish = TestUtils.generateRandomDish(); dish.setNutritionFacts(null);
	 * validator.checkData(dish); }
	 */

	/*
	 * @Test public void testData71() { thrown.expect(EpickurIllegalArgument.class);
	 * thrown.expectMessage("The field dish.nutritionFacts is not allowed to be null or empty");
	 * 
	 * DishValidator validator = new DishValidator(); Dish dish = TestUtils.generateRandomDish(); List<NutritionFact> list = new
	 * ArrayList<NutritionFact>(); dish.setNutritionFacts(list); validator.checkData(dish); }
	 */

	@Test
	public void testData8() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field dish.ingredients is not allowed to be null or empty");

		DishValidator validator = new DishValidator();
		Dish dish = TestUtils.generateRandomDish();
		dish.setIngredients(null);
		validator.checkData(dish);
	}

	@Test
	public void testData9() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field dish.steps is not allowed to be null or empty");

		DishValidator validator = new DishValidator();
		Dish dish = TestUtils.generateRandomDish();
		dish.setSteps(null);
		validator.checkData(dish);
	}

	@Test
	public void testData10() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field dish.caterer is not allowed to be null or empty");

		DishValidator validator = new DishValidator();
		Dish dish = TestUtils.generateRandomDish();
		dish.setCaterer(null);
		validator.checkData(dish);
	}

	@Test
	public void testData11() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field dish.caterer.id is not allowed to be null or empty");

		DishValidator validator = new DishValidator();
		Dish dish = TestUtils.generateRandomDish();
		dish.getCaterer().setId(null);
		validator.checkData(dish);
	}

	@Test
	public void testData12() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field dish.steps is not allowed to be null or empty");

		DishValidator validator = new DishValidator();
		Dish dish = TestUtils.generateRandomDish();
		List<String> step = new ArrayList<String>();
		dish.setSteps(step);
		validator.checkData(dish);
	}

	@Test
	public void testData13() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field dish.ingredients is not allowed to be null or empty");

		DishValidator validator = new DishValidator();
		Dish dish = TestUtils.generateRandomDish();
		List<Ingredient> ing = new ArrayList<Ingredient>();
		dish.setIngredients(ing);
		validator.checkData(dish);
	}

	/*
	 * @Test public void testData14() { thrown.expect(EpickurIllegalArgument.class);
	 * thrown.expectMessage("The field dish.nutritionFacts[0].name is not allowed to be null or empty");
	 * 
	 * DishValidator validator = new DishValidator(); Dish dish = TestUtils.generateRandomDish(); List<NutritionFact> list = dish.getNutritionFacts();
	 * list.get(0).setName(null); validator.checkData(dish); }
	 */

	/*
	 * @Test public void testData15() { thrown.expect(EpickurIllegalArgument.class);
	 * thrown.expectMessage("The field dish.nutritionFacts[0].value is not allowed to be null or empty");
	 * 
	 * DishValidator validator = new DishValidator(); Dish dish = TestUtils.generateRandomDish(); List<NutritionFact> list = dish.getNutritionFacts();
	 * list.get(0).setValue(null); validator.checkData(dish); }
	 */

	/*
	 * @Test public void testData16() { thrown.expect(EpickurIllegalArgument.class);
	 * thrown.expectMessage("The field dish.nutritionFacts[0].unit is not allowed to be null or empty");
	 * 
	 * DishValidator validator = new DishValidator(); Dish dish = TestUtils.generateRandomDish(); List<NutritionFact> list = dish.getNutritionFacts();
	 * list.get(0).setUnit(null); validator.checkData(dish); }
	 */

	@Test
	public void testCheckRightsBefore() throws EpickurException {
		thrown.expect(EpickurForbiddenException.class);

		DishValidator validator = new DishValidator();
		Dish dish = TestUtils.generateRandomDish();
		Key key = TestUtils.generateRandomKey();
		validator.checkRightsBefore(Role.SUPER_USER, Crud.CREATE, dish, dish.getCaterer(), key);
	}

	@Test
	public void checkRightsBefore2() throws EpickurException {
		DishValidator validator = new DishValidator();
		validator.checkRightsBefore(Role.ADMIN, Crud.CREATE, null);
	}

	@Test
	public void checkRightsBefore3() throws EpickurException {
		DishValidator validator = new DishValidator();
		validator.checkRightsBefore(Role.SUPER_USER, Crud.CREATE, null);
	}

	@Test
	public void checkRightsBefore4() throws EpickurException {
		DishValidator validator = new DishValidator();
		validator.checkRightsBefore(Role.SUPER_USER, Crud.READ, null);
	}

	@Test
	public void checkRightsBefore5() throws EpickurException {
		DishValidator validator = new DishValidator();
		validator.checkRightsBefore(Role.SUPER_USER, Crud.UPDATE, null);
	}

	@Test
	public void checkRightsBefore6() throws EpickurException {
		DishValidator validator = new DishValidator();
		validator.checkRightsBefore(Role.SUPER_USER, Crud.DELETE, null);
	}

	@Test(expected = EpickurForbiddenException.class)
	public void checkRightsBefore7() throws EpickurException {
		DishValidator validator = new DishValidator();
		validator.checkRightsBefore(Role.USER, Crud.CREATE, null);
	}

	@Test
	public void checkRightsBefore8() throws EpickurException {
		DishValidator validator = new DishValidator();
		validator.checkRightsBefore(Role.USER, Crud.READ, null);
	}

	@Test(expected = EpickurForbiddenException.class)
	public void checkRightsBefore9() throws EpickurException {
		DishValidator validator = new DishValidator();
		validator.checkRightsBefore(Role.USER, Crud.UPDATE, null);
	}

	@Test(expected = EpickurForbiddenException.class)
	public void checkRightsBefore10() throws EpickurException {
		DishValidator validator = new DishValidator();
		validator.checkRightsBefore(Role.USER, Crud.DELETE, null);
	}

	@Test
	public void testCheckRightsAfter() throws EpickurException {
		thrown.expect(EpickurForbiddenException.class);

		DishValidator validator = new DishValidator();
		Dish dish = TestUtils.generateRandomDish();
		validator.checkRightsAfter(Role.SUPER_USER, new ObjectId(), dish, Crud.UPDATE);
	}
}
