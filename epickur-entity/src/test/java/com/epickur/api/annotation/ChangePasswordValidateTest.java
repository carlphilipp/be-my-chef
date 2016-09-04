package com.epickur.api.annotation;

import com.epickur.api.entity.User;
import com.epickur.api.operation.Update;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;

public class ChangePasswordValidateTest {

	private Validator validator;

	@Before
	public void init() {
		ValidatorFactory vf = Validation.buildDefaultValidatorFactory();
		this.validator = vf.getValidator();
	}

	@Test
	public void testValidateNewPasswordSuccess() {
		User user = new User();
		user.setId(new ObjectId());
		user.setNewPassword("new password");
		user.setPassword("password");
		Set<ConstraintViolation<User>> violations = validator.validate(user, Update.class);
		assertThat(violations, is(empty()));
	}

	@Test
	public void testValidateNewPasswordFail() {
		User user = new User();
		user.setId(new ObjectId());
		user.setNewPassword("new password");
		Set<ConstraintViolation<User>> violations = validator.validate(user, Update.class);
		assertThat(violations, is(not(empty())));
		ConstraintViolation<User> next = violations.iterator().next();
		assertEquals("The field user.password is mandatory when a new password is provided", next.getMessage());
	}
}
