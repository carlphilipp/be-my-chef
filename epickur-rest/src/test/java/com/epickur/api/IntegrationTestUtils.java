package com.epickur.api;

import com.epickur.api.commons.CommonsUtil;
import org.bson.types.ObjectId;

import com.epickur.api.entity.Caterer;
import com.epickur.api.entity.Dish;
import com.epickur.api.entity.Key;
import com.epickur.api.entity.Order;
import com.epickur.api.entity.User;
import com.epickur.api.entity.times.WorkingTimes;
import com.epickur.api.enumeration.Role;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.helper.EntityGenerator;
import com.epickur.api.service.CatererService;
import com.epickur.api.service.DishService;
import com.epickur.api.service.OrderService;
import com.epickur.api.service.UserService;
import com.epickur.api.utils.Utils;

public class IntegrationTestUtils {

	public static User createAdminAndLogin() throws EpickurException {
		User user = EntityGenerator.generateRandomUser();
		String password = new String(user.getPassword());
		UserService service = new UserService();
		User newUser = service.create(user, true);
		newUser.setRole(Role.ADMIN);
		Key key = EntityGenerator.generateRandomAdminKey();
		key.setRole(Role.ADMIN);
		service.update(newUser, key);
		User login = service.login(newUser.getEmail(), password);
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
		CatererService service = new CatererService();
		caterer.setCreatedBy(userId);
		return service.create(caterer);
	}

	public static User createSuperUserAndLogin() throws EpickurException {
		User user = EntityGenerator.generateRandomUser();
		String password = new String(user.getPassword());
		UserService service = new UserService();
		User newUser = service.create(user, true);
		newUser.setRole(Role.SUPER_USER);
		Key key = EntityGenerator.generateRandomAdminKey();
		key.setRole(Role.ADMIN);
		service.update(newUser, key);
		User login = service.login(newUser.getEmail(), password);
		return login;
	}

	public static User createUserAndLogin() throws EpickurException {
		User user = EntityGenerator.generateRandomUser();
		String password = new String(user.getPassword());
		UserService service = new UserService();
		User newUser = service.create(user, true);
		newUser.setRole(Role.USER);
		Key key = EntityGenerator.generateRandomAdminKey();
		key.setRole(Role.ADMIN);
		service.update(newUser, key);
		User login = service.login(newUser.getEmail(), password);
		return login;
	}

	public static Dish createDish() throws EpickurException {
		return createDishWithUserId(new ObjectId());
	}

	public static Dish createDishWithUserId(final ObjectId userId) throws EpickurException {
		Dish dish = EntityGenerator.generateRandomDish();
		dish.setCreatedBy(userId);
		DishService service = new DishService();
		Dish newDish = service.create(dish);
		return newDish;
	}

	public static Order createOrder(final ObjectId userId) throws EpickurException {
		Order order = EntityGenerator.generateRandomOrder();
		OrderService service = new OrderService();
		Order orderRes = service.create(userId.toHexString(), order);
		return orderRes;
	}

	public static User createUser() throws EpickurException {
		User user = EntityGenerator.generateRandomUser();
		UserService service = new UserService();
		return service.create(user, true);
	}

	public static Order createOrder(final ObjectId userId, final ObjectId catererId) throws EpickurException {
		Order order = EntityGenerator.generateRandomOrder();
		order.getDish().getCaterer().setId(catererId);
		OrderService service = new OrderService();
		Order orderRes = service.create(userId.toHexString(), order);
		return orderRes;
	}

	public static String generateRandomCorrectPickupDate(final WorkingTimes workingTimes) {
		String pickupdate = EntityGenerator.generateRandomPickupDate();
		Object[] parsedPickupdate = CommonsUtil.parsePickupdate(pickupdate);
		while (!workingTimes.canBePickup((String) parsedPickupdate[0], (Integer) parsedPickupdate[1])) {
			pickupdate = EntityGenerator.generateRandomPickupDate();
			parsedPickupdate = CommonsUtil.parsePickupdate(pickupdate);
		}
		return pickupdate;
	}
}
