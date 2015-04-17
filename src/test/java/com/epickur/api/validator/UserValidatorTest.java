package com.epickur.api.validator;

import javax.ws.rs.ForbiddenException;

import org.bson.types.ObjectId;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.epickur.api.TestUtils;
import com.epickur.api.entity.Key;
import com.epickur.api.entity.Order;
import com.epickur.api.entity.User;
import com.epickur.api.enumeration.Crud;
import com.epickur.api.enumeration.Role;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.exception.EpickurIllegalArgument;
import com.epickur.api.validator.UserValidator;

public class UserValidatorTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testCheckCreateUser() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field user.name is not allowed to be null or empty");

		UserValidator validator = new UserValidator();
		User user = TestUtils.generateRandomUser();
		user.setName(null);
		validator.checkCreateUser(user);
	}

	@Test
	public void testCheckCreateUser2() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field user.password is not allowed to be null or empty");

		UserValidator validator = new UserValidator();
		User user = TestUtils.generateRandomUser();
		user.setPassword(null);
		validator.checkCreateUser(user);
	}

	@Test
	public void testCheckCreateUser3() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field user.email is not allowed to be null or empty");

		UserValidator validator = new UserValidator();
		User user = TestUtils.generateRandomUser();
		user.setEmail(null);
		validator.checkCreateUser(user);
	}

	@Test
	public void testCheckUpdateUser() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field user.id is not allowed to be null or empty");

		UserValidator validator = new UserValidator();
		User user = TestUtils.generateRandomUser();
		user.setId(null);
		user.setRole(Role.ADMIN);
		validator.checkUpdateUser(user);
	}

	@Test
	public void testCheckUpdateUser2() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("No user has been provided");

		UserValidator validator = new UserValidator();
		;
		validator.checkUpdateUser2("id", null);
	}

	@Test
	public void testCheckUpdateUser3() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field user.password is mandatory when a new password is provided");

		UserValidator validator = new UserValidator();
		User user = TestUtils.generateRandomUser();
		user.setNewPassword("test");
		user.setPassword(null);
		validator.checkUpdateUser2("id", user);
	}

	@Test
	public void testCheckUpdateUser4() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field user.id is not allowed to be null or empty");

		UserValidator validator = new UserValidator();
		User user = TestUtils.generateRandomUser();
		user.setId(null);
		validator.checkUpdateUser2("id", user);
	}

	@Test
	public void testCheckUpdateUser5() {
		UserValidator validator = new UserValidator();
		User user = TestUtils.generateRandomUser();
		user.setId(new ObjectId());
		user.setRole(null);
		validator.checkUpdateUser(user);
	}

	@Test
	public void testCheckUpdateUser6() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field user.id is not allowed to be null or empty");

		UserValidator validator = new UserValidator();
		User user = TestUtils.generateRandomUser();
		user.setNewPassword("password");
		user.setPassword("password");
		validator.checkUpdateUser2("idid", user);
	}

	@Test
	public void testCheckUpdateUser7() {
		UserValidator validator = new UserValidator();
		User user = TestUtils.generateRandomUser();
		user.setId(new ObjectId());
		user.setNewPassword("password");
		user.setPassword("password");
		user.setAllow(null);
		validator.checkUpdateUser2(user.getId().toHexString(), user);
	}

	@Test
	public void testCheckUpdateUser8() {
		UserValidator validator = new UserValidator();
		User user = TestUtils.generateRandomUser();
		user.setId(new ObjectId());
		user.setRole(Role.ADMIN);
		validator.checkUpdateUser(user);
	}

	@Test
	public void testCheckUpdateUser9() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The parameter id and the field user.id should match");

		UserValidator validator = new UserValidator();
		User user = TestUtils.generateRandomUser();
		user.setId(new ObjectId());
		validator.checkUpdateUser2("id", user);
	}

	@Test
	public void testCheckCreateOneOrder() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The parameter token is not allowed to be null or empty");

		UserValidator validator = new UserValidator();
		validator.checkCreateOneOrder("id", null, true);
	}

	@Test
	public void testCheckUpdateOneOrder() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field order.id is not allowed to be null or empty");

		Order order = TestUtils.generateRandomOrder();
		order.setId(null);
		UserValidator validator = new UserValidator();
		validator.checkUpdateOneOrder("id", "orderId", order);
	}

	@Test
	public void testCheckUpdateOneOrder2() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The parameter orderId and the field order.id should match");

		Order order = TestUtils.generateRandomOrder();
		order.setId(new ObjectId());
		UserValidator validator = new UserValidator();
		validator.checkUpdateOneOrder("id", "orderId", order);
	}

	@Test
	public void testCheckCheckUser() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The parameter check is not allowed to be null or empty");

		UserValidator validator = new UserValidator();
		validator.checkCheckUser("id", null);
	}

	@Test
	public void testCheckLogin() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The parameter password is not allowed to be null or empty");

		UserValidator validator = new UserValidator();
		validator.checkLogin("email", null);
	}

	@Test
	public void testCheckCreateOneOrder2() {
		UserValidator validator = new UserValidator();
		validator.checkCreateOneOrder("id", null, false);
	}

	@Test
	public void testCheckRights() throws EpickurException {
		UserValidator validator = new UserValidator();
		validator.checkRightsBefore(Role.ADMIN, Crud.CREATE, null);
	}

	@Test(expected = ForbiddenException.class)
	public void testCheckRights2() throws EpickurException {
		UserValidator validator = new UserValidator();
		validator.checkRightsBefore(Role.SUPER_USER, Crud.CREATE, null);
	}

	@Test
	public void testCheckRights3() throws EpickurException {
		UserValidator validator = new UserValidator();
		validator.checkRightsBefore(Role.SUPER_USER, Crud.READ, null);
	}

	@Test
	public void testCheckRights4() throws EpickurException {
		UserValidator validator = new UserValidator();
		validator.checkRightsBefore(Role.SUPER_USER, Crud.UPDATE, null);
	}

	@Test(expected = ForbiddenException.class)
	public void testCheckRights5() throws EpickurException {
		UserValidator validator = new UserValidator();
		validator.checkRightsBefore(Role.SUPER_USER, Crud.DELETE, null);
	}

	@Test(expected = ForbiddenException.class)
	public void testCheckRights6() throws EpickurException {
		UserValidator validator = new UserValidator();
		validator.checkRightsBefore(Role.USER, Crud.CREATE, null);
	}

	@Test
	public void testCheckRights7() throws EpickurException {
		UserValidator validator = new UserValidator();
		validator.checkRightsBefore(Role.USER, Crud.READ, null);
	}

	@Test
	public void testCheckRights8() throws EpickurException {
		UserValidator validator = new UserValidator();
		validator.checkRightsBefore(Role.USER, Crud.UPDATE, null);
	}

	@Test(expected = ForbiddenException.class)
	public void testCheckRights9() throws EpickurException {
		UserValidator validator = new UserValidator();
		validator.checkRightsBefore(Role.USER, Crud.DELETE, null);
	}

	@Test
	public void testCheckRightsOrder() throws EpickurException {
		UserValidator validator = new UserValidator();
		validator.checkRightsBefore(Role.ADMIN, Crud.CREATE, "order");
	}

	@Test
	public void testCheckRightsOrder2() throws EpickurException {
		UserValidator validator = new UserValidator();
		validator.checkRightsBefore(Role.SUPER_USER, Crud.CREATE, "order");
	}

	@Test
	public void testCheckRightsOrder3() throws EpickurException {
		UserValidator validator = new UserValidator();
		validator.checkRightsBefore(Role.SUPER_USER, Crud.READ, "order");
	}

	@Test
	public void testCheckRightsOrder4() throws EpickurException {
		UserValidator validator = new UserValidator();
		validator.checkRightsBefore(Role.SUPER_USER, Crud.UPDATE, "order");
	}

	@Test(expected = ForbiddenException.class)
	public void testCheckRightsOrder5() throws EpickurException {
		UserValidator validator = new UserValidator();
		validator.checkRightsBefore(Role.SUPER_USER, Crud.DELETE, "order");
	}

	@Test
	public void testCheckRightsOrder6() throws EpickurException {
		UserValidator validator = new UserValidator();
		validator.checkRightsBefore(Role.USER, Crud.CREATE, "order");
	}

	@Test
	public void testCheckRightsOrder7() throws EpickurException {
		UserValidator validator = new UserValidator();
		validator.checkRightsBefore(Role.USER, Crud.READ, "order");
	}

	@Test
	public void testCheckRightsOrder8() throws EpickurException {
		UserValidator validator = new UserValidator();
		validator.checkRightsBefore(Role.USER, Crud.UPDATE, "order");
	}

	@Test(expected = ForbiddenException.class)
	public void testCheckRightsOrder9() throws EpickurException {
		UserValidator validator = new UserValidator();
		validator.checkRightsBefore(Role.USER, Crud.DELETE, "order");
	}

	@Test(expected = EpickurException.class)
	public void testCheckRightsOrder10() throws EpickurException {
		UserValidator validator = new UserValidator();
		validator.checkRightsBefore(Role.USER, Crud.DELETE, "unknown type");
	}
	
	@Test
	public void testCheckRightsAfter() throws EpickurException {
		UserValidator validator = new UserValidator();
		Key key = TestUtils.generateRandomKey();
		key.setRole(Role.USER);
		ObjectId userId = new ObjectId();
		key.setUserId(userId);
		User user = TestUtils.generateRandomUser();
		user.setId(userId);
		validator.checkUserRightsAfter(key.getRole(), key.getUserId(), user, Crud.READ);
	}
	
	@Test
	public void testCheckRightsAfter2() throws EpickurException {
		UserValidator validator = new UserValidator();
		Key key = TestUtils.generateRandomKey();
		key.setRole(Role.USER);
		ObjectId userId = new ObjectId();
		key.setUserId(userId);
		User user = TestUtils.generateRandomUser();
		user.setId(userId);
		validator.checkUserRightsAfter(key.getRole(), key.getUserId(), user, Crud.UPDATE);
	}
	
	@Test
	public void testCheckRightsAfter3() throws EpickurException {
		UserValidator validator = new UserValidator();
		Key key = TestUtils.generateRandomKey();
		key.setRole(Role.SUPER_USER);
		ObjectId userId = new ObjectId();
		key.setUserId(userId);
		User user = TestUtils.generateRandomUser();
		user.setId(userId);
		validator.checkUserRightsAfter(key.getRole(), key.getUserId(), user, Crud.READ);
	}
	
	@Test
	public void testCheckRightsAfter4() throws EpickurException {
		UserValidator validator = new UserValidator();
		Key key = TestUtils.generateRandomKey();
		key.setRole(Role.SUPER_USER);
		ObjectId userId = new ObjectId();
		key.setUserId(userId);
		User user = TestUtils.generateRandomUser();
		user.setId(userId);
		validator.checkUserRightsAfter(key.getRole(), key.getUserId(), user, Crud.UPDATE);
	}
	
	@Test(expected = ForbiddenException.class)
	public void testCheckRightsAfter5() throws EpickurException {
		UserValidator validator = new UserValidator();
		Key key = TestUtils.generateRandomKey();
		key.setRole(Role.SUPER_USER);
		ObjectId userId = new ObjectId();
		key.setUserId(userId);
		User user = TestUtils.generateRandomUser();
		user.setId(new ObjectId());
		validator.checkUserRightsAfter(key.getRole(), key.getUserId(), user, Crud.UPDATE);
	}

	@Test(expected = EpickurException.class)
	public void testCheckRightsAfter6() throws EpickurException {
		UserValidator validator = new UserValidator();
		Key key = TestUtils.generateRandomKey();
		key.setRole(Role.USER);
		User user = TestUtils.generateRandomUser();
		validator.checkUserRightsAfter(key.getRole(), key.getUserId(), user, Crud.CREATE);
	}
}
