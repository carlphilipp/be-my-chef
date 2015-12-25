package com.epickur.api.validator;

import com.epickur.api.entity.Key;
import com.epickur.api.entity.Order;
import com.epickur.api.entity.User;
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

public class UserValidatorTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testCheckCheckUser() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The parameter check is not allowed to be null or empty");

		UserValidator validator = new UserValidator();
		validator.checkCheckUser("id", null);
	}

	@Test
	public void testCheckRightsAfter() throws EpickurException {
		UserValidator validator = new UserValidator();
		Key key = EntityGenerator.generateRandomAdminKey();
		key.setRole(Role.USER);
		ObjectId userId = new ObjectId();
		key.setUserId(userId);
		User user = EntityGenerator.generateRandomUser();
		user.setId(userId);
		validator.checkUserRightsAfter(key.getRole(), key.getUserId(), user, Operation.READ);
	}

	@Test
	public void testCheckRightsAfter2() throws EpickurException {
		UserValidator validator = new UserValidator();
		Key key = EntityGenerator.generateRandomAdminKey();
		key.setRole(Role.USER);
		ObjectId userId = new ObjectId();
		key.setUserId(userId);
		User user = EntityGenerator.generateRandomUser();
		user.setId(userId);
		validator.checkUserRightsAfter(key.getRole(), key.getUserId(), user, Operation.UPDATE);
	}

	@Test
	public void testCheckRightsAfter3() throws EpickurException {
		UserValidator validator = new UserValidator();
		Key key = EntityGenerator.generateRandomAdminKey();
		key.setRole(Role.SUPER_USER);
		ObjectId userId = new ObjectId();
		key.setUserId(userId);
		User user = EntityGenerator.generateRandomUser();
		user.setId(userId);
		validator.checkUserRightsAfter(key.getRole(), key.getUserId(), user, Operation.READ);
	}

	@Test
	public void testCheckRightsAfter4() throws EpickurException {
		UserValidator validator = new UserValidator();
		Key key = EntityGenerator.generateRandomAdminKey();
		key.setRole(Role.SUPER_USER);
		ObjectId userId = new ObjectId();
		key.setUserId(userId);
		User user = EntityGenerator.generateRandomUser();
		user.setId(userId);
		validator.checkUserRightsAfter(key.getRole(), key.getUserId(), user, Operation.UPDATE);
	}

	@Test(expected = EpickurForbiddenException.class)
	public void testCheckRightsAfter5() throws EpickurException {
		UserValidator validator = new UserValidator();
		Key key = EntityGenerator.generateRandomAdminKey();
		key.setRole(Role.SUPER_USER);
		ObjectId userId = new ObjectId();
		key.setUserId(userId);
		User user = EntityGenerator.generateRandomUser();
		user.setId(new ObjectId());
		validator.checkUserRightsAfter(key.getRole(), key.getUserId(), user, Operation.UPDATE);
	}

	@Test(expected = EpickurException.class)
	public void testCheckRightsAfter6() throws EpickurException {
		UserValidator validator = new UserValidator();
		Key key = EntityGenerator.generateRandomAdminKey();
		key.setRole(Role.USER);
		User user = EntityGenerator.generateRandomUser();
		validator.checkUserRightsAfter(key.getRole(), key.getUserId(), user, Operation.CREATE);
	}

	@Test
	public void testCheckOrderRightsAfter() {
		thrown.expect(EpickurForbiddenException.class);

		UserValidator validator = new UserValidator();
		Order order = EntityGenerator.generateRandomOrder();
		validator.checkOrderRightsAfter(Role.SUPER_USER, new ObjectId(), order, Operation.READ);
	}

	@Test
	public void testCheckOrderRightsAfter2() {
		thrown.expect(EpickurForbiddenException.class);

		UserValidator validator = new UserValidator();
		Order order = EntityGenerator.generateRandomOrder();
		validator.checkOrderRightsAfter(Role.SUPER_USER, new ObjectId(), order, Operation.DELETE);
	}
}
