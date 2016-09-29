package com.epickur.api.validation;

import com.epickur.api.entity.Dish;
import com.epickur.api.entity.Ingredient;
import com.epickur.api.enumeration.Operation;
import com.epickur.api.enumeration.Role;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.exception.EpickurForbiddenException;
import com.epickur.api.exception.EpickurIllegalArgument;
import com.epickur.api.helper.EntityGenerator;
import org.bson.types.ObjectId;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class DishValidationTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private CatererValidation catererValidator;
	@InjectMocks
	private DishValidation dishValidation;

	@Test
	public void testCheckCreateDish() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field dish.name is not allowed to be null or empty");

		Dish dish = EntityGenerator.generateRandomDish();
		dish.setName(null);
		dishValidation.checkCreateData(dish);
	}

	@Test
	public void testCheckCreateDish2() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field dish.description is not allowed to be null or empty");

		Dish dish = EntityGenerator.generateRandomDish();
		dish.setDescription(null);
		dishValidation.checkCreateData(dish);
	}

	@Test
	public void testCheckUpdateDish() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field dish.id is not allowed to be null or empty");

		Dish dish = EntityGenerator.generateRandomDish();
		dish.setId(null);
		dishValidation.checkUpdateData(new ObjectId().toHexString(), dish);
	}

	@Test
	public void testCheckUpdateDish2() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage(Validation.NO_DISH_PROVIDED);

		dishValidation.checkUpdateData(new ObjectId().toHexString(), null);
	}

	@Test
	public void testData() {
		Dish dish = EntityGenerator.generateRandomDish();
		dishValidation.checkData(dish);
	}

	@Test
	public void testData2() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field dish.type is not allowed to be null or empty");

		Dish dish = EntityGenerator.generateRandomDish();
		dish.setType(null);
		dishValidation.checkData(dish);
	}

	@Test
	public void testData3() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field dish.price is not allowed to be null or empty");

		Dish dish = EntityGenerator.generateRandomDish();
		dish.setPrice(null);
		dishValidation.checkData(dish);
	}

	@Test
	public void testData4() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field dish.cookingTime is not allowed to be null or empty");

		Dish dish = EntityGenerator.generateRandomDish();
		dish.setCookingTime(null);
		dishValidation.checkData(dish);
	}

	@Test
	public void testData5() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field dish.difficultyLevel is not allowed to be null or empty");

		Dish dish = EntityGenerator.generateRandomDish();
		dish.setDifficultyLevel(null);
		dishValidation.checkData(dish);
	}

	@Test
	public void testData8() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field dish.ingredients is not allowed to be null or empty");

		Dish dish = EntityGenerator.generateRandomDish();
		dish.setIngredients(null);
		dishValidation.checkData(dish);
	}

	@Test
	public void testData9() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field dish.steps is not allowed to be null or empty");

		Dish dish = EntityGenerator.generateRandomDish();
		dish.setSteps(null);
		dishValidation.checkData(dish);
	}

	@Test
	public void testData10() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field dish.caterer is not allowed to be null or empty");

		Dish dish = EntityGenerator.generateRandomDish();
		dish.setCaterer(null);
		dishValidation.checkData(dish);
	}

	@Test
	public void testData11() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field dish.caterer.id is not allowed to be null or empty");

		Dish dish = EntityGenerator.generateRandomDish();
		dish.getCaterer().setId(null);
		dishValidation.checkData(dish);
	}

	@Test
	public void testData12() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field dish.steps is not allowed to be null or empty");

		Dish dish = EntityGenerator.generateRandomDish();
		List<String> step = new ArrayList<>();
		dish.setSteps(step);
		dishValidation.checkData(dish);
	}

	@Test
	public void testData13() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field dish.ingredients is not allowed to be null or empty");

		Dish dish = EntityGenerator.generateRandomDish();
		List<Ingredient> ing = new ArrayList<>();
		dish.setIngredients(ing);
		dishValidation.checkData(dish);
	}

	@Test
	public void testCheckRightsAfter() throws EpickurException {
		thrown.expect(EpickurForbiddenException.class);

		Dish dish = EntityGenerator.generateRandomDish();
		dishValidation.checkRightsAfter(Role.SUPER_USER, new ObjectId(), dish, Operation.UPDATE);
	}

	@Test
	public void testCheckSearch() {
		dishValidation.checkSearch("mon-18:10", "main", "-141.0,5.55", null);
	}

	@Test
	public void testCheckSearchFail() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The parameter at should contain 2 coordinates");

		dishValidation.checkSearch("mon-18:10", "main", "-141.0,5.55,0.0", null);
	}

	@Test
	public void testCheckSearchFail2() {
		thrown.expect(EpickurIllegalArgument.class);

		dishValidation.checkSearch("mon-18:10", "main", "-141.0,ppp", null);
	}
}
