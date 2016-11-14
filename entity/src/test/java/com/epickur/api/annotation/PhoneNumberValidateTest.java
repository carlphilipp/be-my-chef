package com.epickur.api.annotation;

import com.epickur.api.entity.User;
import com.epickur.api.operation.Update;
import com.google.i18n.phonenumbers.Phonenumber;
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
import static org.junit.Assert.assertThat;

public class PhoneNumberValidateTest {

	private Validator validator;

	@Before
	public void init() {
		ValidatorFactory vf = Validation.buildDefaultValidatorFactory();
		this.validator = vf.getValidator();
	}

	@Test
	public void testValidateNewPasswordSuccess() {
		// Given
		User user = new User();
		user.setId(new ObjectId());
		// When

		Set<ConstraintViolation<User>> violations = validator.validate(user, Update.class);

		// Then
		assertThat(violations, is(empty()));
	}

	@Test
	public void testValidateNewPasswordSuccess2() {
		// Given
		User user = new User();
		user.setId(new ObjectId());
		Phonenumber.PhoneNumber phoneNumber = new Phonenumber.PhoneNumber();
		phoneNumber.setCountryCode(33);
		phoneNumber.setNationalNumber(383400775);
		user.setPhoneNumber(phoneNumber);

		// When
		Set<ConstraintViolation<User>> violations = validator.validate(user, Update.class);

		// Then
		assertThat(violations, is(empty()));
	}

	@Test
	public void testValidateNewPasswordFail() {
		// Given
		User user = new User();
		user.setId(new ObjectId());
		Phonenumber.PhoneNumber phoneNumber = new Phonenumber.PhoneNumber();
		phoneNumber.setCountryCode(1);
		phoneNumber.setNationalNumber(383);
		user.setPhoneNumber(phoneNumber);

		// When
		Set<ConstraintViolation<User>> violations = validator.validate(user, Update.class);

		// Then
		assertThat(violations, is(not(empty())));
	}
}
