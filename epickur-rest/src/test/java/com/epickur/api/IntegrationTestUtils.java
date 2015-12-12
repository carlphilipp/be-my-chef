package com.epickur.api;

import com.epickur.api.commons.CommonsUtil;
import com.epickur.api.entity.Caterer;
import com.epickur.api.entity.Dish;
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
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IntegrationTestUtils {

	@Autowired
	private CatererService catererService;

	@Autowired
	private UserService userService;

	@Autowired
	private DishService dishService;

	@Autowired
	private OrderService orderService;

	public User createAdminAndLogin() throws EpickurException {
		User user = EntityGenerator.generateRandomUser();
		String password = user.getPassword();
		User newUser = userService.create(user, true);
		newUser.setRole(Role.ADMIN);
		userService.update(newUser);
		return userService.login(newUser.getEmail(), password);
	}

	public Caterer createCaterer() throws EpickurException {
		return createCatererWithUserId(new ObjectId());
	}

	public Caterer createCatererWithUserId(final ObjectId userId) throws EpickurException {
		Caterer caterer = EntityGenerator.generateRandomCatererWithoutId();
		return createCaterer(caterer, userId);
	}

	public Caterer createCaterer(final Caterer caterer, final ObjectId userId) throws EpickurException {
		caterer.setCreatedBy(userId);
		return catererService.create(caterer);
	}

	public User createSuperUserAndLogin() throws EpickurException {
		User user = EntityGenerator.generateRandomUser();
		String password = user.getPassword();
		User newUser = userService.create(user, true);
		newUser.setRole(Role.SUPER_USER);
		userService.update(newUser);
		return userService.login(newUser.getEmail(), password);
	}

	public User createUserAndLogin() throws EpickurException {
		User user = EntityGenerator.generateRandomUser();
		String password = user.getPassword();
		User newUser = userService.create(user, true);
		newUser.setRole(Role.USER);
		userService.update(newUser);
		return userService.login(newUser.getEmail(), password);
	}

	public Dish createDish() throws EpickurException {
		return createDishWithUserId(new ObjectId());
	}

	public Dish createDishWithUserId(final ObjectId userId) throws EpickurException {
		Dish dish = EntityGenerator.generateRandomDish();
		dish.setCreatedBy(userId);
		return dishService.create(dish);
	}

	public Order createOrder(final ObjectId userId) throws EpickurException {
		Order order = EntityGenerator.generateRandomOrder();
		return orderService.create(userId.toHexString(), order);
	}

	public User createUser() throws EpickurException {
		User user = EntityGenerator.generateRandomUser();
		return userService.create(user, true);
	}

	public Order createOrder(final ObjectId userId, final ObjectId catererId) throws EpickurException {
		Order order = EntityGenerator.generateRandomOrder();
		order.getDish().getCaterer().setId(catererId);
		return orderService.create(userId.toHexString(), order);
	}

	public String generateRandomCorrectPickupDate(final WorkingTimes workingTimes) {
		String pickupdate = EntityGenerator.generateRandomPickupDate();
		Object[] parsedPickupdate = CommonsUtil.parsePickupdate(pickupdate);
		while (!workingTimes.canBePickup((String) parsedPickupdate[0], (Integer) parsedPickupdate[1])) {
			pickupdate = EntityGenerator.generateRandomPickupDate();
			parsedPickupdate = CommonsUtil.parsePickupdate(pickupdate);
		}
		return pickupdate;
	}
}
