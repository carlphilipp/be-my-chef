package com.epickur.api.validator;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.epickur.api.entity.Dish;
import com.epickur.api.entity.Ingredient;
import com.epickur.api.enumeration.Operation;
import com.epickur.api.enumeration.Role;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.exception.EpickurForbiddenException;
import com.epickur.api.exception.EpickurIllegalArgument;
import com.epickur.api.helper.EntityGenerator;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DishValidatorTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private CatererValidator catererValidator;
	@InjectMocks
	private DishValidator validator;

	@Test
	public void testCheckCreateDish() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field dish.name is not allowed to be null or empty");

		Dish dish = EntityGenerator.generateRandomDish();
		dish.setName(null);
		validator.checkCreateData(dish);
	}

	@Test
	public void testCheckCreateDish2() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field dish.description is not allowed to be null or empty");

		Dish dish = EntityGenerator.generateRandomDish();
		dish.setDescription(null);
		validator.checkCreateData(dish);
	}

	@Test
	public void testCheckUpdateDish() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field dish.id is not allowed to be null or empty");

		Dish dish = EntityGenerator.generateRandomDish();
		dish.setId(null);
		validator.checkUpdateData(new ObjectId().toHexString(), dish);
	}

	@Test
	public void testCheckUpdateDish2() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage(Validator.NO_DISH_PROVIDED);

		Dish dish = null;
		validator.checkUpdateData(new ObjectId().toHexString(), dish);
	}

	@Test
	public void testData() {
		Dish dish = EntityGenerator.generateRandomDish();
		validator.checkData(dish);
	}

	@Test
	public void testData2() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field dish.type is not allowed to be null or empty");

		Dish dish = EntityGenerator.generateRandomDish();
		dish.setType(null);
		validator.checkData(dish);
	}

	@Test
	public void testData3() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field dish.price is not allowed to be null or empty");

		Dish dish = EntityGenerator.generateRandomDish();
		dish.setPrice(null);
		validator.checkData(dish);
	}

	@Test
	public void testData4() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field dish.cookingTime is not allowed to be null or empty");

		Dish dish = EntityGenerator.generateRandomDish();
		dish.setCookingTime(null);
		validator.checkData(dish);
	}

	@Test
	public void testData5() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field dish.difficultyLevel is not allowed to be null or empty");

		Dish dish = EntityGenerator.generateRandomDish();
		dish.setDifficultyLevel(null);
		validator.checkData(dish);
	}

	@Test
	public void testData8() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field dish.ingredients is not allowed to be null or empty");

		Dish dish = EntityGenerator.generateRandomDish();
		dish.setIngredients(null);
		validator.checkData(dish);
	}

	@Test
	public void testData9() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field dish.steps is not allowed to be null or empty");

		Dish dish = EntityGenerator.generateRandomDish();
		dish.setSteps(null);
		validator.checkData(dish);
	}

	@Test
	public void testData10() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field dish.caterer is not allowed to be null or empty");

		Dish dish = EntityGenerator.generateRandomDish();
		dish.setCaterer(null);
		validator.checkData(dish);
	}

	@Test
	public void testData11() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field dish.caterer.id is not allowed to be null or empty");

		Dish dish = EntityGenerator.generateRandomDish();
		dish.getCaterer().setId(null);
		validator.checkData(dish);
	}

	@Test
	public void testData12() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field dish.steps is not allowed to be null or empty");

		Dish dish = EntityGenerator.generateRandomDish();
		List<String> step = new ArrayList<>();
		dish.setSteps(step);
		validator.checkData(dish);
	}

	@Test
	public void testData13() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field dish.ingredients is not allowed to be null or empty");

		Dish dish = EntityGenerator.generateRandomDish();
		List<Ingredient> ing = new ArrayList<>();
		dish.setIngredients(ing);
		validator.checkData(dish);
	}

	@Test
	public void testCheckRightsAfter() throws EpickurException {
		thrown.expect(EpickurForbiddenException.class);

		Dish dish = EntityGenerator.generateRandomDish();
		validator.checkRightsAfter(Role.SUPER_USER, new ObjectId(), dish, Operation.UPDATE);
	}
}
