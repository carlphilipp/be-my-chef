package com.epickur.api.validator;

import java.util.List;

import javax.ws.rs.ForbiddenException;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;

import com.epickur.api.entity.Caterer;
import com.epickur.api.entity.Dish;
import com.epickur.api.entity.Ingredient;
import com.epickur.api.entity.Key;
import com.epickur.api.entity.NutritionFact;
import com.epickur.api.enumeration.Crud;
import com.epickur.api.enumeration.Role;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.exception.EpickurIllegalArgument;

public class DishValidator extends Validator {

	protected DishValidator() {
		super("dish");
	}

	/**
	 * @param dish
	 *            The Dish
	 */
	public final void checkCreateData(final Dish dish) {
		checkData(dish);
	}

	/**
	 * @param id
	 *            The Dish id
	 */
	public final void checkId(final String id) {
		if (StringUtils.isBlank(id)) {
			throw new EpickurIllegalArgument(PARAM_ID_NULL);
		}
	}

	/**
	 * @param dish
	 */
	public final void checkUpdateData(final Dish dish) {
		if (dish == null) {
			throw new EpickurIllegalArgument(NO_DISH_PROVIDED);
		}
		if (dish.getId() == null) {
			throw new EpickurIllegalArgument(fieldNull(getEntity(), "id"));
		}
	}

	/**
	 * @param id
	 * @param dish
	 */
	public final void checkUpdateData2(final String id, final Dish dish) {
		if (StringUtils.isBlank(id)) {
			throw new EpickurIllegalArgument(PARAM_ID_NULL);
		}
		// checkData(dish);
		if (dish.getId() == null) {
			throw new EpickurIllegalArgument(fieldNull(getEntity(), "id"));
		}
		if (!dish.getId().toHexString().equals(id)) {
			throw new EpickurIllegalArgument("The parameter id and the field dish.id should match");
		}
	}

	/**
	 * @param dish
	 *            The dish
	 */
	private void checkData(final Dish dish) {
		if (dish == null) {
			throw new EpickurIllegalArgument(NO_DISH_PROVIDED);
		}
		if (StringUtils.isBlank(dish.getName())) {
			throw new EpickurIllegalArgument(fieldNull(getEntity(), "name"));
		}
		if (StringUtils.isBlank(dish.getDescription())) {
			throw new EpickurIllegalArgument(fieldNull(getEntity(), "description"));
		}
		if (StringUtils.isBlank(dish.getType())) {
			throw new EpickurIllegalArgument(fieldNull(getEntity(), "type"));
		}
		if (dish.getPrice() == null) {
			throw new EpickurIllegalArgument(fieldNull(getEntity(), "price"));
		}
		if (dish.getCookingTime() == null) {
			throw new EpickurIllegalArgument(fieldNull(getEntity(), "cookingTime"));
		}
		if (dish.getDifficultyLevel() == null) {
			throw new EpickurIllegalArgument(fieldNull(getEntity(), "difficultyLevel"));
		}
		if (StringUtils.isBlank(dish.getVideoUrl())) {
			throw new EpickurIllegalArgument(fieldNull(getEntity(), "videoUrl"));
		}
		if (dish.getNutritionFacts() != null) {
			checkNutritionFactsData(dish.getNutritionFacts());
		} else {
			throw new EpickurIllegalArgument(fieldNull(getEntity(), "nutritionFacts"));
		}
		if (dish.getIngredients() != null) {
			checkIngredientsData(dish.getIngredients());
		} else {
			throw new EpickurIllegalArgument(fieldNull(getEntity(), "ingredients"));
		}
		if (dish.getSteps() != null) {
			checkStepsData(dish.getSteps());
		} else {
			throw new EpickurIllegalArgument(fieldNull(getEntity(), "steps"));
		}
		if (dish.getCaterer() != null) {
			checkCatererData(dish.getCaterer());
		} else {
			throw new EpickurIllegalArgument(fieldNull(getEntity(), "caterer"));
		}
	}

	/**
	 * @param caterer
	 */
	private void checkCatererData(final Caterer caterer) {
		CatererValidator validator = (CatererValidator) FactoryValidator.getValidator("caterer");
		validator.checkCaterer(caterer, "dish");
		if (caterer.getId() == null) {
			throw new EpickurIllegalArgument(fieldNull(getEntity(), "caterer.id"));
		}
	}

	/**
	 * @param steps
	 */
	private void checkStepsData(final List<String> steps) {
		if (steps.size() == 0) {
			throw new EpickurIllegalArgument(fieldNull(getEntity(), "steps"));
		}
	}

	/**
	 * @param ingredients
	 */
	private void checkIngredientsData(final List<Ingredient> ingredients) {
		if (ingredients.size() == 0) {
			throw new EpickurIllegalArgument(fieldNull(getEntity(), "ingredients"));
		}
	}

	/**
	 * @param nutritionFacts
	 */
	private void checkNutritionFactsData(final List<NutritionFact> nutritionFacts) {
		if (nutritionFacts.size() == 0) {
			throw new EpickurIllegalArgument(fieldNull(getEntity(), "nutritionFacts"));
		} else {
			for (int i = 0; i < nutritionFacts.size(); i++) {
				if (StringUtils.isBlank(nutritionFacts.get(i).getName())) {
					throw new EpickurIllegalArgument(fieldNull(getEntity(), "nutritionFacts[" + i + "].name"));
				}
				if (nutritionFacts.get(i).getValue() == null) {
					throw new EpickurIllegalArgument(fieldNull(getEntity(), "nutritionFacts[" + i + "].value"));
				}
				if (nutritionFacts.get(i).getUnit() == null) {
					throw new EpickurIllegalArgument(fieldNull(getEntity(), "nutritionFacts[" + i + "].unit"));
				}
			}
		}
	}

	/**
	 * @param role
	 * @param action
	 * @param dish
	 * @param catererDB
	 * @param key
	 * @throws EpickurException
	 */
	public final void checkRightsBefore(final Role role, final Crud action, final Dish dish, final Caterer catererDB, final Key key)
			throws EpickurException {
		if (role == Role.SUPER_USER && action == Crud.CREATE) {
			if (!key.getUserId().equals(catererDB.getCreatedBy())) {
				throw new ForbiddenException();
			}
		}
		super.checkRightsBefore(role, action);
	}

	/**
	 * @param role
	 * @param userId
	 * @param dish
	 * @param action
	 */
	public final void checkRightsAfter(final Role role, final ObjectId userId, final Dish dish, final Crud action) {
		if (role != Role.ADMIN) {
			if (action == Crud.UPDATE || action == Crud.DELETE) {
				if (!dish.getCreatedBy().equals(userId)) {
					throw new ForbiddenException();
				}
			}
		}
	}
}
