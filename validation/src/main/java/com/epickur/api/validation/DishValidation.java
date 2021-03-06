package com.epickur.api.validation;

import com.epickur.api.commons.CommonsUtil;
import com.epickur.api.entity.Caterer;
import com.epickur.api.entity.Dish;
import com.epickur.api.entity.Ingredient;
import com.epickur.api.entity.Key;
import com.epickur.api.enumeration.DishType;
import com.epickur.api.enumeration.Operation;
import com.epickur.api.enumeration.Role;
import com.epickur.api.exception.EpickurForbiddenException;
import com.epickur.api.exception.EpickurIllegalArgument;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

/**
 * The Dish Validator class
 *
 * @author cph
 * @version 1.0
 */
public class DishValidation extends Validation {

	@Autowired
	private CatererValidation catererValidator;

	/**
	 * Constructor
	 */
	protected DishValidation() {
		super("dish");
	}

	/**
	 * @param dish The Dish
	 */
	public void checkCreateData(final Dish dish) {
		checkData(dish);
	}

	/**
	 * @param id   The Dish Id
	 * @param dish The Dish
	 */
	public void checkUpdateData(final String id, final Dish dish) {
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
	 * @param dish The dish
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
		if (dish.getIngredients() == null) {
			throw new EpickurIllegalArgument(fieldNull(getEntity(), "ingredients"));
		} else {
			checkIngredientsData(dish.getIngredients());
		}
		if (dish.getSteps() == null) {
			throw new EpickurIllegalArgument(fieldNull(getEntity(), "steps"));
		} else {
			checkStepsData(dish.getSteps());
		}
		if (dish.getCaterer() == null) {
			throw new EpickurIllegalArgument(fieldNull(getEntity(), "caterer"));
		} else {
			checkCatererData(dish.getCaterer());
		}
	}

	/**
	 * @param caterer The Caterer
	 */
	private void checkCatererData(final Caterer caterer) {
		catererValidator.checkCaterer(caterer, "dish");
		if (caterer.getId() == null) {
			throw new EpickurIllegalArgument(fieldNull(getEntity(), "caterer.id"));
		}
	}

	/**
	 * @param role      The Role
	 * @param action    The Crud Action
	 * @param catererDB The CatererDB
	 * @param key       The Key
	 */
	public void checkRightsBefore(final Role role, final Operation action, final Caterer catererDB, final Key key) {
		if (role == Role.SUPER_USER && action == Operation.CREATE && !key.getUserId().equals(catererDB.getCreatedBy())) {
			throw new EpickurForbiddenException();
		}
	}

	/**
	 * @param steps A list of step
	 */
	private void checkStepsData(final List<String> steps) {
		if (steps.size() == 0) {
			throw new EpickurIllegalArgument(fieldNull(getEntity(), "steps"));
		}
	}

	/**
	 * @param ingredients The list of ingredients
	 */
	private void checkIngredientsData(final List<Ingredient> ingredients) {
		if (ingredients.size() == 0) {
			throw new EpickurIllegalArgument(fieldNull(getEntity(), "ingredients"));
		}
	}

	/**
	 * @param role   The Role
	 * @param userId The User Id
	 * @param dish   The Dish
	 * @param action The action
	 */
	public void checkRightsAfter(final Role role, final ObjectId userId, final Dish dish, final Operation action) {
		if (role != Role.ADMIN) {
			if ((action == Operation.UPDATE || action == Operation.DELETE) && !dish.getCreatedBy().equals(userId)) { // NOPMD
				throw new EpickurForbiddenException();
			}
		}
	}

	/**
	 * @param pickupdate The pickupdate
	 * @param types      The list of dish type
	 * @param at         The geo coordinate
	 * @param searchtext The address to search
	 */
	public void checkSearch(final String pickupdate, final String types, final String at, final String searchtext) {
		final Optional<Object[]> resultOptional = CommonsUtil.parsePickupdate(pickupdate);
		if (!resultOptional.isPresent()) {
			throw new EpickurIllegalArgument("The parameter pickupdate has a wrong format. Should be: ddd-hh:mm, with ddd: mon|tue|wed|thu|fri|sat|sun. Found: " + pickupdate);
		}
		final String[] typesArray = types.split(",");
		for (final String temp : typesArray) {
			try {
				DishType.fromString(temp);
			} catch (IllegalArgumentException e) {
				throw new EpickurIllegalArgument(temp + " is not recongnize as a dish type");
			}
		}
		if (StringUtils.isBlank(searchtext) && StringUtils.isBlank(at)) {
			throw new EpickurIllegalArgument("The parameter at or searchtext are not allowed to be null or empty at the same time");
		} else {
			if (!StringUtils.isBlank(at)) {
				final String[] coordinates = at.split(",");
				if (coordinates.length != 2) {
					throw new EpickurIllegalArgument("The parameter at should contain 2 coordinates");
				} else {
					for (final String temp : coordinates) {
						if(!NumberUtils.isNumber(temp)) {
							throw new EpickurIllegalArgument(at + " is not a valid coordinate");
						}
					}
				}
			}
		}
	}
}
