package com.epickur.api.validator;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;

import com.epickur.api.entity.Caterer;
import com.epickur.api.entity.Dish;
import com.epickur.api.entity.Ingredient;
import com.epickur.api.entity.Key;
import com.epickur.api.enumeration.Operation;
import com.epickur.api.enumeration.Role;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.exception.EpickurForbiddenException;
import com.epickur.api.exception.EpickurIllegalArgument;

/**
 * The Dish Validator class
 * 
 * @author cph
 * @version 1.0
 *
 */
public final class DishValidator extends Validator {

	/**
	 * Constructor
	 */
	protected DishValidator() {
		super("dish");
	}

	/**
	 * @param dish
	 *            The Dish
	 */
	public void checkCreateData(final Dish dish) {
		checkData(dish);
	}

	/**
	 * @param id
	 *            The Dish Id
	 * @param dish
	 *            The Dish
	 */
	public void checkUpdateData(final String id, final Dish dish) {
		checkId(id);
		if (dish == null) {
			throw new EpickurIllegalArgument(NO_DISH_PROVIDED);
		}
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
	protected void checkData(final Dish dish) {
		if (dish == null) {
			throw new EpickurIllegalArgument(NO_DISH_PROVIDED);
		}
		if (StringUtils.isBlank(dish.getName())) {
			throw new EpickurIllegalArgument(fieldNull(getEntity(), "name"));
		}
		if (StringUtils.isBlank(dish.getDescription())) {
			throw new EpickurIllegalArgument(fieldNull(getEntity(), "description"));
		}
		if (dish.getType() == null) {
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
		if (StringUtils.isBlank(dish.getImageAfterUrl())) {
			throw new EpickurIllegalArgument(fieldNull(getEntity(), "imageAfterUrl"));
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
	 *            The Caterer
	 */
	private void checkCatererData(final Caterer caterer) {
		CatererValidator validator = (CatererValidator) FactoryValidator.getValidator("caterer");
		validator.checkCaterer(caterer, "dish");
		if (caterer.getId() == null) {
			throw new EpickurIllegalArgument(fieldNull(getEntity(), "caterer.id"));
		}
	}
	
	/**
	 * @param role
	 *            The Role
	 * @param action
	 *            The Crud Action
	 * @param dish
	 *            The Dish
	 * @param catererDB
	 *            The CatererDB
	 * @param key
	 *            The Key
	 * @throws EpickurException
	 *             If an EpickurExeption occured
	 */
	public void checkRightsBefore(final Role role, final Operation action, final Dish dish, final Caterer catererDB, final Key key)
			throws EpickurException {
		if (role == Role.SUPER_USER && action == Operation.CREATE && !key.getUserId().equals(catererDB.getCreatedBy())) {
			throw new EpickurForbiddenException();
		}
	}

	/**
	 * @param steps
	 *            A list of step
	 */
	private void checkStepsData(final List<String> steps) {
		if (steps.size() == 0) {
			throw new EpickurIllegalArgument(fieldNull(getEntity(), "steps"));
		}
	}

	/**
	 * @param ingredients
	 *            The list of ingredients
	 */
	private void checkIngredientsData(final List<Ingredient> ingredients) {
		if (ingredients.size() == 0) {
			throw new EpickurIllegalArgument(fieldNull(getEntity(), "ingredients"));
		}
	}

	/**
	 * @param role
	 *            The Role
	 * @param userId
	 *            The User Id
	 * @param dish
	 *            The Dish
	 * @param action
	 *            The action
	 */
	public void checkRightsAfter(final Role role, final ObjectId userId, final Dish dish, final Operation action) {
		if (role != Role.ADMIN) {
			if ((action == Operation.UPDATE || action == Operation.DELETE) && !dish.getCreatedBy().equals(userId)) {
				throw new EpickurForbiddenException();
			}
		}
	}
}
