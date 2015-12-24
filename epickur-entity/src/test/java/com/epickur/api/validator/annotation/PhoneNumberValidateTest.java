package com.epickur.api.validator.annotation;

import com.epickur.api.entity.User;
import com.epickur.api.validator.operation.Update;
import com.google.i18n.phonenumbers.Phonenumber;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PhoneNumberValidateTest {

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
		Set<ConstraintViolation<User>> violations = validator.validate(user, Update.class);
		assertTrue(violations.isEmpty());
	}

	@Test
	public void testValidateNewPasswordSuccess2() {
		User user = new User();
		user.setId(new ObjectId());
		Phonenumber.PhoneNumber phoneNumber = new Phonenumber.PhoneNumber();
		phoneNumber.setCountryCode(33);
		phoneNumber.setNationalNumber(383400775);
		user.setPhoneNumber(phoneNumber);
		Set<ConstraintViolation<User>> violations = validator.validate(user, Update.class);
		assertTrue(violations.isEmpty());
	}

	@Test
	public void testValidateNewPasswordFail() {
		User user = new User();
		user.setId(new ObjectId());
		Phonenumber.PhoneNumber phoneNumber = new Phonenumber.PhoneNumber();
		phoneNumber.setCountryCode(1);
		phoneNumber.setNationalNumber(383);
		user.setPhoneNumber(phoneNumber);
		Set<ConstraintViolation<User>> violations = validator.validate(user, Update.class);
		assertFalse(violations.isEmpty());
	}

}
