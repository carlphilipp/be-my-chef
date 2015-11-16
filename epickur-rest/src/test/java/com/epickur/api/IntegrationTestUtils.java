package com.epickur.api;

import org.bson.types.ObjectId;

import com.epickur.api.business.CatererBusiness;
import com.epickur.api.business.DishBusiness;
import com.epickur.api.business.OrderBusiness;
import com.epickur.api.business.UserBusiness;
import com.epickur.api.entity.Caterer;
import com.epickur.api.entity.Dish;
import com.epickur.api.entity.Key;
import com.epickur.api.entity.Order;
import com.epickur.api.entity.User;
import com.epickur.api.entity.times.WorkingTimes;
import com.epickur.api.enumeration.Role;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.helper.EntityGenerator;
import com.epickur.api.utils.Utils;

public class IntegrationTestUtils {

	public static User createAdminAndLogin() throws EpickurException {
		User user = EntityGenerator.generateRandomUser();
		String password = new String(user.getPassword());
		UserBusiness business = new UserBusiness();
		User newUser = business.create(user, true);
		newUser.setRole(Role.ADMIN);
		Key key = EntityGenerator.generateRandomAdminKey();
		key.setRole(Role.ADMIN);
		business.update(newUser, key);
		User login = business.login(newUser.getEmail(), password);
		return login;
	}

	public static Caterer createCaterer() throws EpickurException {
		return createCatererWithUserId(new ObjectId());
	}

	public static Caterer createCatererWithUserId(final ObjectId userId) throws EpickurException {
		Caterer caterer = EntityGenerator.generateRandomCatererWithoutId();
		return createCaterer(caterer, userId);
	}

	public static Caterer createCaterer(final Caterer caterer, final ObjectId userId) throws EpickurException {
		CatererBusiness business = new CatererBusiness();
		caterer.setCreatedBy(userId);
		return business.create(caterer);
	}

	public static User createSuperUserAndLogin() throws EpickurException {
		User user = EntityGenerator.generateRandomUser();
		String password = new String(user.getPassword());
		UserBusiness business = new UserBusiness();
		User newUser = business.create(user, true);
		newUser.setRole(Role.SUPER_USER);
		Key key = EntityGenerator.generateRandomAdminKey();
		key.setRole(Role.ADMIN);
		business.update(newUser, key);
		User login = business.login(newUser.getEmail(), password);
		return login;
	}

	public static User createUserAndLogin() throws EpickurException {
		User user = EntityGenerator.generateRandomUser();
		String password = new String(user.getPassword());
		UserBusiness business = new UserBusiness();
		User newUser = business.create(user, true);
		newUser.setRole(Role.USER);
		Key key = EntityGenerator.generateRandomAdminKey();
		key.setRole(Role.ADMIN);
		business.update(newUser, key);
		User login = business.login(newUser.getEmail(), password);
		return login;
	}

	public static Dish createDish() throws EpickurException {
		return createDishWithUserId(new ObjectId());
	}

	public static Dish createDishWithUserId(final ObjectId userId) throws EpickurException {
		Dish dish = EntityGenerator.generateRandomDish();
		dish.setCreatedBy(userId);
		DishBusiness business = new DishBusiness();
		Dish newDish = business.create(dish);
		return newDish;
	}

	public static Order createOrder(final ObjectId userId) throws EpickurException {
		Order order = EntityGenerator.generateRandomOrder();
		OrderBusiness business = new OrderBusiness();
		Order orderRes = business.create(userId.toHexString(), order);
		return orderRes;
	}

	public static User createUser() throws EpickurException {
		User user = EntityGenerator.generateRandomUser();
		UserBusiness business = new UserBusiness();
		return business.create(user, true);
	}

	public static Order createOrder(final ObjectId userId, final ObjectId catererId) throws EpickurException  {
		Order order = EntityGenerator.generateRandomOrder();
		order.getDish().getCaterer().setId(catererId);
		OrderBusiness business = new OrderBusiness();
		Order orderRes = business.create(userId.toHexString(), order);
		return orderRes;
	}
	
	public static String generateRandomCorrectPickupDate(final WorkingTimes workingTimes) {
		String pickupdate = EntityGenerator.generateRandomPickupDate();
		Object[] parsedPickupdate = Utils.parsePickupdate(pickupdate);
		while (!workingTimes.canBePickup((String) parsedPickupdate[0], (Integer) parsedPickupdate[1])) {
			pickupdate = EntityGenerator.generateRandomPickupDate();
			parsedPickupdate = Utils.parsePickupdate(pickupdate);
		}
		return pickupdate;
	}
}
