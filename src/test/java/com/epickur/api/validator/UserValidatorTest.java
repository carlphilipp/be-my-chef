package com.epickur.api.validator;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import org.bson.types.ObjectId;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.epickur.api.TestUtils;
import com.epickur.api.entity.Key;
import com.epickur.api.entity.Order;
import com.epickur.api.entity.User;
import com.epickur.api.enumeration.Operation;
import com.epickur.api.enumeration.Role;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.exception.EpickurForbiddenException;
import com.epickur.api.exception.EpickurIllegalArgument;
import com.epickur.api.integration.UserIT;
import com.stripe.Stripe;
import com.stripe.exception.APIConnectionException;
import com.stripe.exception.APIException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;

public class UserValidatorTest {

	@BeforeClass
	public static void beforeClass() {
		InputStreamReader in = null;
		try {
			in = new InputStreamReader(UserIT.class.getClass().getResourceAsStream("/test.properties"));
			Properties prop = new Properties();
			prop.load(in);
			in.close();
			Stripe.apiKey = prop.getProperty("stripe.key");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
	}

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
	public void testCheckCreateUser4() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field user.country is not allowed to be null or empty");

		UserValidator validator = new UserValidator();
		User user = TestUtils.generateRandomUser();
		user.setCountry(null);
		validator.checkCreateUser(user);
	}

	@Test
	public void testCheckCreateUser5() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field user.zipcode is not allowed to be null or empty");

		UserValidator validator = new UserValidator();
		User user = TestUtils.generateRandomUser();
		user.setZipcode(null);
		validator.checkCreateUser(user);
	}

	@Test
	public void testCheckCreateUser6() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field user.state is not allowed to be null or empty");

		UserValidator validator = new UserValidator();
		User user = TestUtils.generateRandomUser();
		user.setState(null);
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
		validator.checkUpdateUser(new ObjectId().toHexString(), user);
	}

	@Test
	public void testCheckUpdateUser2() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("No user has been provided");

		UserValidator validator = new UserValidator();
		validator.checkUpdateUser(new ObjectId().toHexString(), null);
	}

	@Test
	public void testCheckUpdateUser3() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field user.password is mandatory when a new password is provided");

		UserValidator validator = new UserValidator();
		User user = TestUtils.generateRandomUser();
		user.setNewPassword("test");
		user.setPassword(null);
		validator.checkUpdateUser(new ObjectId().toHexString(), user);
	}

	@Test
	public void testCheckUpdateUser4() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field user.id is not allowed to be null or empty");

		UserValidator validator = new UserValidator();
		User user = TestUtils.generateRandomUser();
		user.setId(null);
		validator.checkUpdateUser(new ObjectId().toHexString(), user);
	}

	@Test
	public void testCheckUpdateUser5() {
		UserValidator validator = new UserValidator();
		User user = TestUtils.generateRandomUser();
		ObjectId id = new ObjectId();
		user.setId(id);
		user.setRole(null);
		validator.checkUpdateUser(id.toHexString(), user);
	}

	@Test
	public void testCheckUpdateUser6() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field user.id is not allowed to be null or empty");

		UserValidator validator = new UserValidator();
		User user = TestUtils.generateRandomUser();
		user.setNewPassword("password");
		user.setPassword("password");
		validator.checkUpdateUser(new ObjectId().toHexString(), user);
	}

	@Test
	public void testCheckUpdateUser7() {
		UserValidator validator = new UserValidator();
		User user = TestUtils.generateRandomUser();
		user.setId(new ObjectId());
		user.setNewPassword("password");
		user.setPassword("password");
		user.setAllow(null);
		validator.checkUpdateUser(user.getId().toHexString(), user);
	}

	@Test
	public void testCheckUpdateUser8() {
		UserValidator validator = new UserValidator();
		User user = TestUtils.generateRandomUser();
		ObjectId id = new ObjectId();
		user.setId(id);
		user.setRole(Role.ADMIN);
		validator.checkUpdateUser(id.toHexString(), user);
	}

	@Test
	public void testCheckUpdateUser9() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The parameter id and the field user.id should match");

		UserValidator validator = new UserValidator();
		User user = TestUtils.generateRandomUser();
		user.setId(new ObjectId());
		validator.checkUpdateUser(new ObjectId().toHexString(), user);
	}

	@Test
	public void testCheckUpdateOneOrder() throws AuthenticationException, InvalidRequestException, APIConnectionException, CardException,
			APIException {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field order.id is not allowed to be null or empty");

		Order order = TestUtils.generateRandomOrder();
		order.setId(null);
		UserValidator validator = new UserValidator();
		validator.checkUpdateOneOrder(new ObjectId().toHexString(), new ObjectId().toHexString(), order);
	}

	@Test
	public void testCheckUpdateOneOrder2() throws AuthenticationException, InvalidRequestException, APIConnectionException, CardException,
			APIException {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The parameter orderId and the field order.id should match");

		Order order = TestUtils.generateRandomOrder();
		order.setId(new ObjectId());
		UserValidator validator = new UserValidator();
		validator.checkUpdateOneOrder(new ObjectId().toHexString(), new ObjectId().toHexString(), order);
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
	public void testCheckCreateOneOrder() throws AuthenticationException, InvalidRequestException, APIConnectionException, CardException,
			APIException {
		UserValidator validator = new UserValidator();
		Order order = TestUtils.generateRandomOrder();
		validator.checkCreateOneOrder(new ObjectId().toHexString(), order);
	}

	@Test
	public void testCheckCreateOneOrder2() {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage(Validator.NO_ORDER_PROVIDED);

		UserValidator validator = new UserValidator();
		Order order = null;
		validator.checkCreateOneOrder(new ObjectId().toHexString(), order);
	}
	
	@Test
	public void testCheckCreateOneOrder3() throws AuthenticationException, InvalidRequestException, APIConnectionException, CardException,
			APIException {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field order.cardToken is not allowed to be null or empty");
		
		UserValidator validator = new UserValidator();
		Order order = TestUtils.generateRandomOrder();
		order.setCardToken(null);
		validator.checkCreateOneOrder(new ObjectId().toHexString(), order);
	}
	
	@Test
	public void testCheckCreateOneOrder4() throws AuthenticationException, InvalidRequestException, APIConnectionException, CardException,
			APIException {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field order.description is not allowed to be null or empty");
		
		UserValidator validator = new UserValidator();
		Order order = TestUtils.generateRandomOrder();
		order.setDescription(null);
		validator.checkCreateOneOrder(new ObjectId().toHexString(), order);
	}
	
	@Test
	public void testCheckCreateOneOrder5() throws AuthenticationException, InvalidRequestException, APIConnectionException, CardException,
			APIException {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field order.amount is not allowed to be null or empty");
		
		UserValidator validator = new UserValidator();
		Order order = TestUtils.generateRandomOrder();
		order.setAmount(null);
		validator.checkCreateOneOrder(new ObjectId().toHexString(), order);
	}
	
	@Test
	public void testCheckCreateOneOrder6() throws AuthenticationException, InvalidRequestException, APIConnectionException, CardException,
			APIException {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field order.currency is not allowed to be null or empty");
		
		UserValidator validator = new UserValidator();
		Order order = TestUtils.generateRandomOrder();
		order.setCurrency(null);
		validator.checkCreateOneOrder(new ObjectId().toHexString(), order);
	}
	
	@Test
	public void testCheckCreateOneOrder7() throws AuthenticationException, InvalidRequestException, APIConnectionException, CardException,
			APIException {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field order.pickupdate is not allowed to be null or empty");
		
		UserValidator validator = new UserValidator();
		Order order = TestUtils.generateRandomOrder();
		order.setPickupdate(null);
		validator.checkCreateOneOrder(new ObjectId().toHexString(), order);
	}
	
	@Test
	public void testCheckCreateOneOrder8() throws AuthenticationException, InvalidRequestException, APIConnectionException, CardException,
			APIException {
		thrown.expect(EpickurIllegalArgument.class);
		thrown.expectMessage("The field order.paid can not be true");
		
		UserValidator validator = new UserValidator();
		Order order = TestUtils.generateRandomOrder();
		order.setPaid(true);
		validator.checkCreateOneOrder(new ObjectId().toHexString(), order);
	}

	@Test
	public void testCheckRightsAfter() throws EpickurException {
		UserValidator validator = new UserValidator();
		Key key = TestUtils.generateRandomAdminKey();
		key.setRole(Role.USER);
		ObjectId userId = new ObjectId();
		key.setUserId(userId);
		User user = TestUtils.generateRandomUser();
		user.setId(userId);
		validator.checkUserRightsAfter(key.getRole(), key.getUserId(), user, Operation.READ);
	}

	@Test
	public void testCheckRightsAfter2() throws EpickurException {
		UserValidator validator = new UserValidator();
		Key key = TestUtils.generateRandomAdminKey();
		key.setRole(Role.USER);
		ObjectId userId = new ObjectId();
		key.setUserId(userId);
		User user = TestUtils.generateRandomUser();
		user.setId(userId);
		validator.checkUserRightsAfter(key.getRole(), key.getUserId(), user, Operation.UPDATE);
	}

	@Test
	public void testCheckRightsAfter3() throws EpickurException {
		UserValidator validator = new UserValidator();
		Key key = TestUtils.generateRandomAdminKey();
		key.setRole(Role.SUPER_USER);
		ObjectId userId = new ObjectId();
		key.setUserId(userId);
		User user = TestUtils.generateRandomUser();
		user.setId(userId);
		validator.checkUserRightsAfter(key.getRole(), key.getUserId(), user, Operation.READ);
	}

	@Test
	public void testCheckRightsAfter4() throws EpickurException {
		UserValidator validator = new UserValidator();
		Key key = TestUtils.generateRandomAdminKey();
		key.setRole(Role.SUPER_USER);
		ObjectId userId = new ObjectId();
		key.setUserId(userId);
		User user = TestUtils.generateRandomUser();
		user.setId(userId);
		validator.checkUserRightsAfter(key.getRole(), key.getUserId(), user, Operation.UPDATE);
	}

	@Test(expected = EpickurForbiddenException.class)
	public void testCheckRightsAfter5() throws EpickurException {
		UserValidator validator = new UserValidator();
		Key key = TestUtils.generateRandomAdminKey();
		key.setRole(Role.SUPER_USER);
		ObjectId userId = new ObjectId();
		key.setUserId(userId);
		User user = TestUtils.generateRandomUser();
		user.setId(new ObjectId());
		validator.checkUserRightsAfter(key.getRole(), key.getUserId(), user, Operation.UPDATE);
	}

	@Test(expected = EpickurException.class)
	public void testCheckRightsAfter6() throws EpickurException {
		UserValidator validator = new UserValidator();
		Key key = TestUtils.generateRandomAdminKey();
		key.setRole(Role.USER);
		User user = TestUtils.generateRandomUser();
		validator.checkUserRightsAfter(key.getRole(), key.getUserId(), user, Operation.CREATE);
	}

	@Test(expected = EpickurForbiddenException.class)
	public void testCheckOrderRightsAfter() throws AuthenticationException, InvalidRequestException, APIConnectionException, CardException,
			APIException {
		UserValidator validator = new UserValidator();
		Order order = TestUtils.generateRandomOrder();
		validator.checkOrderRightsAfter(Role.SUPER_USER, new ObjectId(), order, Operation.READ);
	}

	@Test(expected = EpickurForbiddenException.class)
	public void testCheckOrderRightsAfter2() throws AuthenticationException, InvalidRequestException, APIConnectionException, CardException,
			APIException {
		UserValidator validator = new UserValidator();
		Order order = TestUtils.generateRandomOrder();
		validator.checkOrderRightsAfter(Role.SUPER_USER, new ObjectId(), order, Operation.DELETE);
	}
}
